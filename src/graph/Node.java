package graph;

import java.util.ArrayList;
import java.util.List;


public class Node {
	
	private long id;
	private List<Path> sourcePaths;
	private List<Path> sinkPaths;
	private List<Edge> edges;
	
	
	public Node(long id) {
		this.id = id;
		sourcePaths = new ArrayList<Path>();
		sinkPaths = new ArrayList<Path>();
		edges = new ArrayList<Edge>();
	}
	
	public Node(String str) {
		
		sourcePaths = new ArrayList<Path>();
		sinkPaths = new ArrayList<Path>();
		edges = new ArrayList<Edge>();
		
		String body = str.substring(1, str.length() - 1);
		String[] mainInfo = body.split("\t");
		this.id = Integer.parseInt(mainInfo[0]);
		
		String[] parts = mainInfo[1].split("\\|");
		
		
		// Source Paths
		if (parts.length >=1 && parts[0].length() > 0)
			for (String s : parts[0].split(";"))
				this.addSourcePath(new Path(s));
		
		// Sink Paths
		if (parts.length >= 2 && parts[1].length() > 0)
			for (String s : parts[1].split(";"))
				this.addSinkPath(new Path(s));
		
		// Edges
		if (parts.length >=3 && parts[2].length() > 0)
			for (String s : parts[2].split(";")) {
				this.addEdge(new Edge(s));
			}
		
	}
	
	public long getId() {
		return id;
	}

	public List<Path> getSourcePaths() {
		return sourcePaths;
	}

	public List<Path> getSinkPaths() {
		return sinkPaths;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void addSourcePath(Path p) {
		this.sourcePaths.add(p);
	}
	
	public void removeSourcePath(Path p) {
		sourcePaths.remove(p);
	}
	
	public void addSinkPath(Path p) {
		this.sinkPaths.add(p);
	}
	
	public void removeSinkPath(Path p) {
		sinkPaths.remove(p);
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//ID
		sb.append("(");
		sb.append(this.id);
		
		sb.append("\t");
		
		//Source Paths
		int i = 0;
		for (Path p : sourcePaths) {
			sb.append(p);
			if (i != sourcePaths.size() - 1)
				sb.append(";");
			i++;
		}
		
		sb.append("|");
		
		//Sink Paths
		i = 0;
		for (Path p : sinkPaths) {
			sb.append(p);
			if (i != sinkPaths.size() - 1)
				sb.append(";");
			i++;
		}
		
		sb.append("|");
		
		//Edges
		i = 0;
		for (Edge e : edges) {
			sb.append(e);
			if (i != edges.size() - 1)
				sb.append(";");
			i++;
		}
		sb.append(")");
		return sb.toString();
	}
}
