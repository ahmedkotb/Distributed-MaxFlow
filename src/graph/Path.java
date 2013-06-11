package graph;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import maxflow.MaxFlowSettings;

public class Path implements Cloneable {
	
	private List<Edge> edges;	
	private Set<Long> visitedNodes;
	int flow;

	public Path() {
		edges = new ArrayList<Edge>();
		visitedNodes = new HashSet<Long>();
		flow = MaxFlowSettings.INFINITY;
	}

	public Path(String str) {
		edges = new ArrayList<Edge>();
		visitedNodes = new HashSet<Long>();
		flow = MaxFlowSettings.INFINITY;
		String body = str.substring(1, str.length()-1);
		String[] components = body.split(">");
		for (String c : components)
			this.extend(new Edge(c));
	}
	
	public int getFlow() {
		return flow;
	}

	public Path concatenate(Path p) {
		//TODO: check for common edges between the two paths
		// in case the function was used in any time other than the end (from src to end)
		// also set the visited node to union of the two sets
		
		Path newPath = this.clone();
		
		for (Edge e : p.edges)
			newPath.extend(e);
			
		newPath.flow = Math.min(this.flow, p.flow);
		
		return newPath;
	}

	public boolean extend(Edge e) {
		
		//check if edge is already in the path
		if (visitedNodes.contains(e.getToNodeId()))
			return false;
		
		visitedNodes.add(e.getToNodeId());
		edges.add(e);
		
		// adjust the minimum flow
		this.flow = Math.min(this.flow, e.getResidualCapacity());
		
		return true;
	}

	public List<Edge> getEdges(){
		return edges;
	}
	
	@Override
	public String toString() {
		if(edges.size() == 0) return "";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		
		int i=0;
		for (Edge e : edges) {
			sb.append(e);
			if (i != edges.size()-1)
				sb.append(">");
			i++;
		}
		
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		return true;
	}

	public Path clone() {
		
		Path p = new Path();
		
		for (Edge e : edges)
			p.extend(e);
		
		for (long id : visitedNodes)
			p.visitedNodes.add(id);
		
		p.flow = this.flow;
		
		return p;
	}
	
}
