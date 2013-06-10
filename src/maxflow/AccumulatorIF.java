package maxflow;

import java.util.Iterator;
import java.util.Map.Entry;

import graph.*;

public interface AccumulatorIF {
	
	public boolean accept(Path p);
	
	public int size();
	
	public int getFlow();
	
	public Iterator<Entry<Long, Integer>> getFlowMapIterator();
}
