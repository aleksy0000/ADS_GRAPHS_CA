import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;

class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    class Edge {
        int u;
        int v;
        int wgt;

        Edge(int u, int v, int wgt) {
            this.u = u;
            this.v = v;
            this.wgt = wgt;
        }
    }

    class DisjointSet {
        int[] parent;
        int[] rank;

        DisjointSet(int n) {
            parent = new int[n + 1];
            rank = new int[n + 1];

            for (int i = 1; i <= n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int findSet(int v) {
            if (parent[v] != v) {
                parent[v] = findSet(parent[v]);
            }
            return parent[v];
        }

        void union(int u, int v) {
            int rootU = findSet(u);
            int rootV = findSet(v);

            if (rootU == rootV) return;

            if (rank[rootU] < rank[rootV]) {
                parent[rootU] = rootV;
            } else if (rank[rootU] > rank[rootV]) {
                parent[rootV] = rootU;
            } else {
                parent[rootV] = rootU;
                rank[rootU]++;
            }
        }

        public void displayParent(){
            System.out.println("Parent Array:");
            for(int i = 0;i < V + 1;i++){
                System.out.print(parent[i] + " ");
            }
            System.out.println();
        }

        public void displayRank(){
            System.out.println("Rank Array:");
            for(int i = 0;i < V + 1;i++){
                System.out.print(rank[i] + " ");
            }
            System.out.println();
        }
        
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

    public void kruskal() {
        Edge[] edges = new Edge[E];
        int edgeCount = 0;

        // Collect each undirected edge only once
        for (int u = 1; u <= V; u++) {
            for (Node n = adj[u]; n != z; n = n.next) {
                int v = n.vert;

                if (u < v) {
                    edges[edgeCount++] = new Edge(u, v, n.wgt);
                }
            }
        }

        heapSort(edges, edgeCount);

        DisjointSet ds = new DisjointSet(V);

        int mstWeight = 0;
        int mstEdges = 0;

        System.out.println("\nKruskal MST:");

        for (int i = 0; i < edgeCount; i++) {
            System.out.println();
            System.out.println("----------------------------");
            System.out.println();
            Edge e = edges[i];

            if (ds.findSet(e.u) != ds.findSet(e.v)) {
                ds.union(e.u, e.v);
                ds.displayParent();
                ds.displayRank();

                System.out.println(toChar(e.u) + " --(" + e.wgt + ")-- " + toChar(e.v));

                mstWeight += e.wgt;
                mstEdges++;

                if (mstEdges == V - 1) {
                    break;
                }
            }
        }

        System.out.println("Total MST weight = " + mstWeight);
    }

    private void heapSort(Edge[] edges, int n) {
        // build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(edges, n, i);
            //System.out.print(toChar(edges[i].u) + "-> ");
        }

        // extract largest to end
        for (int end = n - 1; end > 0; end--) {
            swap(edges, 0, end);
            heapify(edges, end, 0);
        }
    }

    private void heapify(Edge[] edges, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && edges[left].wgt > edges[largest].wgt) {
            largest = left;
        }

        if (right < n && edges[right].wgt > edges[largest].wgt) {
            largest = right;
        }

        if (largest != i) {
            swap(edges, i, largest);
            heapify(edges, n, largest);
        }
    }

    private void swap(Edge[] edges, int i, int j) {
        Edge temp = edges[i];
        edges[i] = edges[j];
        edges[j] = temp;
    }
}

public class Kruskal {
    public static void main(String[] args) throws IOException
    {
        String fname = "wGraph3.txt";               

        Graph g = new Graph(fname);
       
        //g.display();

        g.kruskal();  
    }
}
