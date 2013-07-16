package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import maxflow.MaxFlowSettings;


public class Node {
	
	private long id;
	private List<Path> sourcePaths;
	private List<Path> sinkPaths;
	private List<Edge> edges;
	private HashSet<Long> visitedNeighbours;
	private NodeColor color;
		
	public Node(long id) {
		this.id = id;
		sourcePaths = new ArrayList<Path>();
		sinkPaths = new ArrayList<Path>();
		edges = new ArrayList<Edge>();
		visitedNeighbours = new HashSet<Long>();
		color = NodeColor.WHITE;
		
		if(this.id == MaxFlowSettings.SRC_NODE_ID)
			sourcePaths.add(new Path());
		else if(this.id == MaxFlowSettings.SINK_NODE_ID)
			sinkPaths.add(new Path());
	}
	
	public Node(String str) {
		
		sourcePaths = new ArrayList<Path>();
		sinkPaths = new ArrayList<Path>();
		edges = new ArrayList<Edge>();
		visitedNeighbours = new HashSet<Long>();
		
		String body = str.substring(1, str.length() - 1);
		String[] mainInfo = body.split("\t");
		
		String[] nodeIdState = mainInfo[0].split("-");
		this.id = Integer.parseInt(nodeIdState[0]);
		this.color = NodeColor.valueOf(nodeIdState[1]);
		
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
		
		for(Edge e : p.getExtendingEdges())
			visitedNeighbours.add(e.getToNodeId());
	}
	
	@Deprecated
	public void removeSourcePath(Path p) {
		sourcePaths.remove(p);
		
		for(Edge e : p.getExtendingEdges())
			visitedNeighbours.remove(e.getToNodeId());
	}
	
	public void removeSourcePath(int index){
		Path p = sourcePaths.get(index);
		sourcePaths.remove(index);
		
		for(Edge e : p.getExtendingEdges())
			visitedNeighbours.remove(e.getToNodeId());
	}
	
	public void addSinkPath(Path p) {
		if(this.id == MaxFlowSettings.SINK_NODE_ID)
			return;
		this.sinkPaths.add(p);
		for(Edge e : p.getExtendingEdges())
			visitedNeighbours.add(e.getToNodeId());
	}

	@Deprecated
	public void removeSinkPath(Path p) {
		sinkPaths.remove(p);
		
		for(Edge e : p.getExtendingEdges())
			visitedNeighbours.remove(e.getToNodeId());
	}
	
	public void removeSinkPath(int index){
		Path p = sinkPaths.get(index);
		this.sinkPaths.remove(index);
		
		for(Edge e : p.getExtendingEdges())
			visitedNeighbours.remove(e.getToNodeId());
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
	}
	
	public boolean isVisitedNeighbour(Long vertexId){
		return visitedNeighbours.contains(vertexId);
	}
	
	public void visitNeighbour(Long vertexId){
		visitedNeighbours.add(vertexId);
	}
	
	public void freeNeighbour(Long vertexId){
		visitedNeighbours.remove(vertexId);
	}

	
	public NodeColor getColor() {
		return color;
	}

	public void setColor(NodeColor color) {
		this.color = color;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//ID
		sb.append("(");
		
		sb.append(this.id);
		sb.append("-");
		sb.append(this.color.toString());
		
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
