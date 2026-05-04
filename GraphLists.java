// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs
// Next Steps
// 1. Ensure the code uses enum colors instead of visited[]
// 2. Complete the code i.e., do kruskal in seperate file.
// 3. Go over the code, understand fully, be able to explain it.
// 4. Run real world graph like a road network, run the mst and kruskal code on it and not running time and memory usage
// 5. Write report

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;

enum Color { WHITE, GRAY, BLACK }

class Heap
{
    //heap array, parent: i / 2, left child: i * 2, right child i * 2 + 1.
    private int[] a;	
    // hPos[h[k]] == k, index position relates to vertex i.e., index 3 = vertex C, the stored value relates to the position of vertex C in the heap array a[], i.e, hPos[3] = 5, a[5] == 3
    private int[] hPos;	    
    // dist[v] = priority of v, index position relates to vertex, i.e., index 1 = vertex A, the stored value relates to lowest weight edge connecting the vertex to the minimum spanning tree.
    private int[] dist;    
    private int N;         // heap size
   
    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) 
    {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }


    public boolean isEmpty() 
    {
        return N == 0;
    }


    public void siftUp( int k) 
    {
        int v = a[k];

        // code yourself
        // must use hPos[] and dist[] arrays
        while(k > 1 && dist[v] < dist[a[k / 2]]){
            //swap
            a[k] = a[k / 2];
            hPos[a[k]] = k;
                
            k = k / 2;
        }

        a[k] = v;
        hPos[v] = k;
    }


    public void siftDown(int k) 
    {
        int v, j;
       
        v = a[k]; //assign the initial vertex
        
        // code yourself 
        // must use hPos[] and dist[] arrays
        // Check if there is atleast a left child
        while(2 * k <= N){
            j = 2 * k; //left child, for now it's smallest.

            //compare left child with right
            if(j < N && dist[a[j + 1]] < dist[a[j]]){
                j++; //right child is smaller
            }

            //if v is already smaller than or equal to smallest child, stop
            if(dist[v] <= dist[a[j]]){
                break;
            }

            //swap parent with smallest child
            a[k] = a[j];
            hPos[a[k]] = k; 

            k = j;
        }
        
        a[k] = v;
        hPos[v] = k;

    }


    public void insert( int x) 
    {   

        a[++N] = x;
        siftUp( N);
    }


    public int remove() 
    {   
        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N+1] = 0;  // put null node into empty spot
        
        a[1] = a[N--];

        if(N > 0){
            siftDown(1);
        }
        
        return v;
    }


    public void heapDisplay(){
        System.out.println("Heap Contents:");
        for(int i = 1;i <= N;i++){
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

}

class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }
    
    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    // the index of the element in adj is the vertex i.e., vertex 3 is stored at index 3
    // at index 3, the neighbours of vertex 3 are stored in a linkedlist.
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;
    
    // used for traversing graph
    //private int[] visited;
    //private int id;
    private Color[] color;
    
    
    // default constructor
    public Graph(String graphFile)  throws IOException
    {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // multiple whitespace as delimiter
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // create sentinel node
        z = new Node(); 
        z.next = z;
        
        // create adjacency lists, initialised to sentinel node z       
        adj = new Node[V+1];        
        for(v = 1; v <= V; ++v)
            adj[v] = z;               
        
        // create array for representing vertex visits
        //visited = new int[V + 1];
        color = new Color[V + 1];

        for(v = 1; v <= V; v++){
            color[v] = Color.WHITE;
        }

       // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));   

           
            
            // write code to put edge into adjacency list     
            //insert v into u's adjacency list
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t; 

            //insert u into v's adjacency list
            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;

        }	       
    }
   
    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
    
    // method to display the graph representation
    public void display() {
        int v;
        Node n;
        
        for(v=1; v<=V; ++v){
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next) 
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        }
        System.out.println("");
    }

    public void DF(int s){
        //visited[s] = 1;
        color[s] = Color.GRAY;

        System.out.println("Visited: " + toChar(s));
        
        Node t = adj[s];

        while(t != z){
            System.out.println();
            System.out.println("Exploring " + toChar(s));
            System.out.println();

            int v = t.vert;

            System.out.println("Found " + toChar(s) + "->" + toChar(v));

            if(color[v] == Color.WHITE){
                System.out.println("Going from " + toChar(s) + " to " + toChar(v));
                DF(v); //recursive call
                System.out.println("Going back to " + toChar(s) + " from " + toChar(v));
            }else{
                System.out.println(toChar(v) + " Already Visited, Moving on to the next vertex adjacent to " + toChar(s));
            }

            t = t.next;
        }  

        color[s] = Color.BLACK;
    }

    public void breadthFirst(int s){
        Queue<Integer> q = new ArrayDeque<>();

        //visited[s] = 1;
        //System.out.println("Visited Initial Vertex " + toChar(s));
        //System.out.println("Inserting initial vertex into queue");

        color[s] = Color.GRAY;
        q.add(s);

        System.out.println("Starting BFS at " + toChar(s));

        while(!q.isEmpty()){
            int u = q.remove();
            System.out.println();
            System.out.println("Exploring: " + toChar(u));
            System.out.println();

            Node t = adj[u];

            while(t != z){
                int v = t.vert;
                System.out.println("Found " + toChar(u) + "->" + toChar(v));

                if(color[v] == Color.WHITE){
                    System.out.println("Inserting " + toChar(v) + " into queue");
                    //visited[v] = 1;
                    color[v] = Color.GRAY;
                    q.add(v);
                }
                
                displayQueue(q);

                t = t.next;
            }

            color[u] = Color.BLACK;
        }
    }

    public void displayQueue(Queue<Integer> q){
        System.out.println("Current State of Queue:");
        for(int x : q){
            System.out.print(toChar(x) + "->");
        }
        System.out.println();
    }
    
	public void MST_Prim(int s)
	{
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist = new int[V + 1]; 
        int[] parent = new int[V + 1]; 
        int[] hPos = new int[V + 1];
        mst = new int[V + 1];
        Node t;

        //initialise arrays
        for(int i = 1; i <= V;i++){
            dist[i] = Integer.MAX_VALUE;
            //System.out.println("dist[" + i + "] = " + dist[i]);
            parent[i] = -1;
            //System.out.println("parent[" + i + "] = " + parent[i]);
            hPos[i] = -1; //not in heap
            //System.out.println("hPos[" + i + "] = " + hPos[i]);
            mst[i] = -1;
            //System.out.println("mst[" + i + "] = " + mst[i]); 
        }


        //insert starting nodes priority
        dist[s] = 0;
        Heap h =  new Heap(V, dist, hPos);
        if(s > 0 && s < V){
            h.insert(s);
        }
        
        int p = 0;
        while (!h.isEmpty())  
        {
            p++;
            System.out.println();
            System.out.println("while(!h.isEmpty()) iteration: " + p);
            System.out.println();
            u = h.remove(); //vertex of smallest dist[u]
            mst[u] = parent[u];
            wgt_sum += dist[u];
            // u is now part of MST
            System.out.println(toChar(u) + " / " + u + " is now part of MST");
            //print heap contents for debugging
            System.out.println();
            h.heapDisplay();
            //print dist contents for debugging
            System.out.println("Dist Contents:");
            for(int i = 1;i <= V;i++){
            if(dist[i] == Integer.MAX_VALUE){
                System.out.print("_ ");
            }
            else{
                System.out.print(dist[i] + " ");
                }
            }
            System.out.println();
            
            //print parent contents for debugging
            System.out.println("Parent Contents:");
            for(int i = 1;i <= V;i++){
                System.out.print(parent[i] + " ");
            }
            System.out.println(); 

    
            t = adj[u];

            int k = 0;
            while(t != z){
                k++;
                System.out.println();
                System.out.println("while(t != z) iteration: " + k);
                System.out.println();
                System.out.println("Traversing neighbours of " + toChar(u));
                v = t.vert;
                //System.out.println("v = " + v);
                wgt = t.wgt;
                //System.out.println("wgt = " + wgt);

                if(hPos[v] == -1){
                    System.out.println(toChar(v) + " / " + v + " Not in heap, inserting...");
                    dist[v] = wgt;
                    parent[v] = u;
                    h.insert(v);
                    //h.heapDisplay();
                     //dist contents for debugging
                    /*System.out.println("Dist Contents:");
                        for(int i = 1;i <= V;i++){
                            if(dist[i] == Integer.MAX_VALUE){
                                System.out.print("-1 ");
                            }
                            else{
                                System.out.print(dist[i] + " ");
                            }
                        }
                    System.out.println();*/
                    //parent contents for debugging
                    /*System.out.println("Parent Contents:");
                        for(int i = 1;i <= V;i++){
                            System.out.print(parent[i] + " ");
                        }
                    System.out.println(); */
                }
                else if(hPos[v] > 0 && wgt < dist[v]){
                    System.out.println("Found lower edge, connecting " + toChar(u) + " and " + toChar(v) +  " updating heap...");
                    dist[v] = wgt;
                    parent[v] = u;

                    h.siftUp(hPos[v]);
                    //h.heapDisplay();
                    
                    //dist contents for debugging
                    /*System.out.println("Dist Contents:");
                        for(int i = 1;i <= V;i++){
                            if(dist[i] == Integer.MAX_VALUE){
                                System.out.print("-1 ");
                            }
                            else{
                                System.out.print(dist[i] + " ");
                            }
                        }
                    System.out.println();*/
                    //parent contents for debugging
                    /*System.out.println("Parent Contents:");
                        for(int i = 1;i <= V;i++){
                            System.out.print(parent[i] + " ");
                        }
                    System.out.println(); */
                }
                
                System.out.println();
                System.out.println("---------------------------------");
                System.out.println();
                t = t.next;
            }
            System.out.println();
            System.out.println("*******************************************");
            System.out.println();
        }
        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");

        showMST();
    }
    
    public void showMST()
    {
            System.out.print("\n\nMinimum Spanning tree parent array is:\n");
            for(int v = 1; v <= V; ++v)
                System.out.println(toChar(v) + " -> " + toChar(mst[v]));
            System.out.println("");
    }

    public void SPT_Dijkstra(int s)
    {

    }

}

public class GraphLists {
    public static void main(String[] args) throws IOException
    {
        int s = 12;
        String fname = "wGraph3.txt";               

        Graph g = new Graph(fname);
       
        g.display();
        
        System.out.println();
        System.out.println("-----------------------------");
        System.out.println("Depth First Search Beginning:");
        //g.DF(s);
        System.out.println("Depth First Search Complete");
        System.out.println();
        System.out.println("------------------------------");
        System.out.println();
        System.out.println("Breadth First Search Beginning:");
        g.breadthFirst(s);
        System.out.println("Breadth First Search Complete");
        System.out.println();
        System.out.println("-------------------------------");
        System.out.println();
        System.out.println("Prim's Beginning:");
        g.MST_Prim(s);
        System.out.println("Prim's Complete");
    }
}
