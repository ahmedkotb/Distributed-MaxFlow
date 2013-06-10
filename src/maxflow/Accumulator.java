package maxflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
//import java.util.Set;

import graph.Edge;
import graph.Path;

public class Accumulator implements AccumulatorIF{

	private HashMap<Long, Integer> flowMap;
	private int size = 0;
	private int flow = 0;
	
	public Accumulator(){
		flowMap = new HashMap<Long, Integer>();
	}
	
	@Override
	public boolean accept(Path p) {
		Iterator<Edge> itr = p.getEdges().iterator();
		Edge e;
		int pathFlow = p.getFlow();
		
		//check for the capacity constraint
		while(itr.hasNext()){
			e = itr.next();
			Integer flow = flowMap.get(e.getId());
			if(flow != null && flow + pathFlow > e.getCapacity())
				return false;
		}
		
		//accept the path and increase edges flow in the accumulator
		itr = p.getEdges().iterator();
		while(itr.hasNext()){
			e = itr.next();
			Integer flow = flowMap.get(e.getId());
			if(flow == null)
				flow=0;
			
			//XXX access the map twice vs get a reference and update it
			flowMap.put(e.getId(), flow+pathFlow);
		}
	
		flow+= p.getFlow();
		size++;
		return true;
	}

	@Override
	public int size() {
		return size;
	}
	
	@Override
	public Iterator<Entry<Long, Integer>> getFlowMapIterator(){
		return flowMap.entrySet().iterator();
	}

	@Override
	public int getFlow() {
		return flow;
	}
}
