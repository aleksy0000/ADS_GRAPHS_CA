// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;

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
                j = j + 1; //right child is smaller
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
        siftDown(1);
        
        return v;
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
    private int[] visited;
    private int id;
    
    
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
        visited = new int[V + 1];

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
        visited[s] = 1;
        System.out.println("Visited: " + toChar(s));
        
        Node t = adj[s];

        while(t != z){
            System.out.println();
            System.out.println("Exploring " + toChar(s));
            System.out.println();

            int v = t.vert;

            System.out.println("Found " + toChar(s) + "->" + toChar(v));

            if(visited[v] == 0){
                System.out.println("Going from " + toChar(s) + " to " + toChar(v));
                DF(v); //recursive call
                System.out.println("Going back to " + toChar(s) + " from " + toChar(v));
            }else{
                System.out.println(toChar(v) + " Already Visited, Moving on to the next vertex adjacent to " + toChar(s));
            }

            t = t.next;
        }  
    }

    public void breadthFirst(int s){
        Queue<Integer> q = new ArrayDeque<>();

        visited[s] = 1;
        System.out.println("Visited Initial Vertex " + toChar(s));
        System.out.println("Inserting initial vertex into queue");

        q.add(s);

        while(!q.isEmpty()){
            int u = q.remove();
            System.out.println();
            System.out.println("Exploring: " + toChar(u));
            System.out.println();

            Node t = adj[u];

            while(t != z){
                int v = t.vert;
                System.out.println("Found " + toChar(u) + "->" + toChar(v));

                if(visited[v] == 0){
                    System.out.println("Inserting " + toChar(v) + " into queue");
                    visited[v] = 1;
                    q.add(v);
                }
                
                displayQueue(q);

                t = t.next;
            }
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
        int[]  dist, parent, hPos;
        Node t;

        //code here
        
        //dist[s] = 0;
        
        //Heap h =  new Heap(V, dist, hPos);
        //h.insert(s);
        
        //while ( ...)  
        //{
            // most of alg here
            
       // }
        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        
                  		
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

        g.DF(s);
        System.out.println("Depth First Traversal Complete");
        //g.breadthFirst(s);
        //g.MST_Prim(s);   
        //g.SPT_Dijkstra(s);               
    }
}
