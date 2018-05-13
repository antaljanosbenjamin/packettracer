package hu.bme.mit.mdsd.packettracer.design.utils;

import java.util.HashSet;
import java.util.Set;

public class Graph {
	private final Set<Vertex> vertexes;
	private final Set<Edge> edges;

	public Graph() {
		this.vertexes = new HashSet<Vertex>();
		this.edges = new HashSet<Edge>();
	}
	
	public boolean addBidirectionalEdge(Vertex v1, Vertex v2, Double weight) {
		if (!vertexes.contains(v1) || !vertexes.contains(v2)) {
			throw new RuntimeException("Graph doesn't contain on of the vertexes: " + v1.toString() + "," + v2.toString());
		}
		Edge e1 = new Edge(v2, weight);
		Edge e2 = new Edge(v1, weight);
		edges.add(e1);
		edges.add(e2);
		boolean v1Added = v1.adjacencies.add(e1);
		boolean v2Added = v2.adjacencies.add(e2);
		if (v1Added || v2Added) {
			System.out.println("Vertex added: " + v1.name + " <---> " + v2.name);
		}
		return v1Added || v2Added;
	}
	
	public boolean addVertex(Vertex v) {
		return vertexes.add(v);
	}
	
	public Set<Vertex> getVertexes() {
		return vertexes;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

}