package graph;

public class Edge {
	
	private long id;
	
	private long toNodeId;
	private int flow;
	private int capacity;
	
	public Edge(long id, long toNodeId, int flow, int capacity) {
		this.id = id;
		this.toNodeId = toNodeId;
		this.flow = flow;
		this.capacity = capacity;
	}
	
	public Edge(String str) {
		String body = str.substring(1, str.length()-1);
		String[] info = body.split(",");
		
		this.id = Long.parseLong(info[0]);
		this.toNodeId = Long.parseLong(info[1]);
		this.flow = Integer.parseInt(info[2]);
		this.capacity = Integer.parseInt(info[3]);
	}

	public long getId() {
		return id;
	}
	
	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}
	
	public long getToNodeId() {
		return toNodeId;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getResidualCapacity(){
		return capacity - flow;
	}

	public Edge getReversedEdge(long srcNodeId){
		return new Edge(-1*this.id, srcNodeId, -1*flow, capacity);
	}
	
	//this function doesn't do any validation
	public void augment(int deltaFlow) {
		this.flow += deltaFlow;
	}
	
	public boolean isSaturated() {
		return this.flow >= this.capacity;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		
		sb.append(id);
		sb.append(",");
		sb.append(toNodeId);
		sb.append(",");
		sb.append(flow);
		sb.append(",");
		sb.append(capacity);
		
		sb.append(")");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + capacity;
		result = prime * result + flow;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (toNodeId ^ (toNodeId >>> 32));
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
		Edge other = (Edge) obj;
		if (id != other.id)
			return false;	
		if (toNodeId != other.toNodeId)
			return false;
		if (flow != other.flow)
			return false;
		if (capacity != other.capacity)
			return false;
		return true;
	}	
}
