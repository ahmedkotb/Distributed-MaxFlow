package maxflow;

import java.util.HashMap;
import java.util.Iterator;

import graph.Edge;
import graph.Path;

public class Accumulator implements AccumulatorIF{

	private HashMap<Long, Integer> flowMap;
	
	public Accumulator(){
		flowMap = new HashMap<Long, Integer>();
	}
	
	@Override
	public boolean accept(Path p) {
		Iterator<Edge> itr = p.getEdges();
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
		itr = p.getEdges();
		while(itr.hasNext()){
			e = itr.next();
			Integer flow = flowMap.get(e.getId());
			if(flow == null)
				flow=0;
			
			//XXX access the map twice vs get a reference and update it
			flowMap.put(e.getId(), flow+pathFlow);
		}
		
		return true;
	}
}
