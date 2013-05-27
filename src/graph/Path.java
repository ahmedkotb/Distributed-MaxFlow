package graph;

import java.util.List;
import java.util.ArrayList;

public class Path implements Cloneable {
	private List<Edge> edges;

	public Path() {
		edges = new ArrayList<Edge>();
	}

	public Path(String str) {
		edges = new ArrayList<Edge>();
		String body = str.substring(1, str.length()-1);
		String[] components = body.split(">");
		for (String c : components)
			this.extend(new Edge(c));
	}
	
	public void concatenate(Path p) {
		// TODO
	}

	public void extend(Edge e) {
		edges.add(e);
	}

	public String toString() {
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
		
		return p;
	}
	
}
