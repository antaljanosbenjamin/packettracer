package hu.bme.mit.mdsd.packettracer.design.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MaxFlow
{ 
	private int vCount;
	
	public MaxFlow() {
		vCount = -1;
	}
	
    /* Returns true if there is a path from source 's' to sink
      't' in residual graph. Also fills parent[] to store the
      path */
	private boolean bfs(Double rGraph[][], int s, int t, int parent[])
    {
        // Create a visited array and mark all vertices as not
        // visited
        boolean visited[] = new boolean[vCount];
        for(int i=0; i<vCount; ++i)
            visited[i]=false;
 
        // Create a queue, enqueue source vertex and mark
        // source vertex as visited
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(s);
        visited[s] = true;
        parent[s]=-1;
 
        // Standard BFS Loop
        while (queue.size()!=0)
        {
            int u = queue.poll();
 
            for (int v=0; v<vCount; v++)
            {
                if (visited[v]==false && rGraph[u][v] > 0)
                {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }
 
        // If we reached sink in BFS starting from source, then
        // return true, else false
        return (visited[t] == true);
    }
 
    // Returns tne maximum flow from s to t in the given graph
    private Double fordFulkerson(Double graph[][], int s, int t)
    {
        int u, v;
 
        // Create a residual graph and fill the residual graph
        // with given capacities in the original graph as
        // residual capacities in residual graph
 
        // Residual graph where rGraph[i][j] indicates
        // residual capacity of edge from i to j (if there
        // is an edge. If rGraph[i][j] is 0, then there is
        // not)
        Double rGraph[][] = new Double[vCount][vCount];
 
        for (u = 0; u < vCount; u++)
            for (v = 0; v < vCount; v++)
                rGraph[u][v] = new Double(graph[u][v].toString());
 
        // This array is filled by BFS and to store path
        int parent[] = new int[vCount];
 
        Double max_flow = 0.0;  // There is no flow initially
 
        // Augment the flow while tere is path from source
        // to sink
        while (bfs(rGraph, s, t, parent))
        {
            // Find minimum residual capacity of the edhes
            // along the path filled by BFS. Or we can say
            // find the maximum flow through the path found.
            Double path_flow = Double.MAX_VALUE;
            for (v=t; v!=s; v=parent[v])
            {
                u = parent[v];
                path_flow = Math.min(path_flow, rGraph[u][v]);
            }
 
            // update residual capacities of the edges and
            // reverse edges along the path
            for (v=t; v != s; v=parent[v])
            {
                u = parent[v];
                rGraph[u][v] -= path_flow;
                rGraph[v][u] += path_flow;
            }
 
            // Add path flow to overall flow
            max_flow += path_flow;
        }
 
        // Return the overall flow
        return max_flow;
    }
    
    public Double calculateMaxFlow(Graph graph, Vertex source, Vertex target) {
    	vCount = graph.getVertexes().size();
    	Map<Vertex, Integer> vertexToInt = new HashMap<Vertex, Integer>();
    	Double[][] innerGraph = new Double[vCount][vCount];
    	for(Double[] row :innerGraph) {
    		for(int i = 0; i < vCount; i++)
    			row[i] = Double.valueOf(0.0);
    	}
    	Integer vertexNum = Integer.valueOf(0);
    	for(Vertex vertex: graph.getVertexes()) {
    		System.out.println(vertex.name + "  == " + vertexNum);
    		vertexToInt.put(vertex, vertexNum++);
    	}
    	
    	for(Vertex vertex: graph.getVertexes()) {
    		Integer actVertexNum = vertexToInt.get(vertex);
    		for(Edge edge : vertex.adjacencies) {
    			innerGraph[actVertexNum][vertexToInt.get(edge.target)] = edge.weight;
    			System.out.println("" + actVertexNum + " ---(" + edge.weight + ")---> " + vertexToInt.get(edge.target));
    		}
    	}
    	
    	return fordFulkerson(innerGraph, vertexToInt.get(source), vertexToInt.get(target));
    }
}
