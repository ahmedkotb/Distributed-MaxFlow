package graph;

import java.util.ArrayList;
import java.util.List;

import maxflow.MaxFlowSettings;


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
		
		if(this.id == MaxFlowSettings.SRC_NODE_ID)
			sourcePaths.add(new Path());
		else if(this.id == MaxFlowSettings.SINK_NODE_ID)
			sinkPaths.add(new Path());
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
		
		if(this.id == MaxFlowSettings.SRC_NODE_ID)
			sourcePaths.add(new Path());
		else if(this.id == MaxFlowSettings.SINK_NODE_ID)
			sinkPaths.add(new Path());		
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

	public void setEdges(List<Edge> edges){
		this.edges = edges;
	}
	
	public void addSourcePath(Path p) {
		if(this.id == MaxFlowSettings.SRC_NODE_ID)
			return;
		this.sourcePaths.add(p);
	}
	
	public void removeSourcePath(Path p) {
		sourcePaths.remove(p);
	}
	
	public void removeSourcePath(int index){
		sourcePaths.remove(index);
	}
	
	public void addSinkPath(Path p) {
		if(this.id == MaxFlowSettings.SINK_NODE_ID)
			return;
		this.sinkPaths.add(p);
	}
	
	public void removeSinkPath(Path p) {
		sinkPaths.remove(p);
	}
	
	public void removeSinkPath(int index){
		this.sinkPaths.remove(index);
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//ID
		sb.append("(");
		sb.append(this.id);
		
		sb.append("\t");
		
		//Source Paths
		boolean first=true;
		for (Path p : sourcePaths) {
			if(p != null){
				if(!first) sb.append(";");
				else first = false;
				sb.append(p);
			}
		}
		
		sb.append("|");
		
		//Sink Paths
		first = true;
		for (Path p : sinkPaths) {
			if(p != null){
				if(!first) sb.append(";");
				else first = false;
				sb.append(p);
			}
		}
		
		sb.append("|");
		
		//Edges
		int i = 0;
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
