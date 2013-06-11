package maxflow;

import java.util.Iterator;
import java.util.Map.Entry;

import graph.*;

public interface AccumulatorIF {
	
	/**
	 * accepts the excess/augmenting path if it holds the capacity constraint condition 
	 * @param p Source/Sink/Augmenting path to be checked
	 * @return True if path is accepted. False otherwise
	 */
	public boolean accept(Path p);
	
	/**
	 * Getter for number of accepted paths
	 * @return the number of paths accepted by this accumulator
	 */
	public int size();
	
	/**
	 * Getter for the aggregated flow of accepted paths
	 * @return Total flow of accepted paths
	 */
	public int getFlow();
	
	/**
	 * Getter for the <EdgeID, aggregatedFlow> tuples in the accumulator
	 * @return Iterator<EdgeID, aggregatedFlow> for all edges in the accumulator
	 */
	public Iterator<Entry<Long, Integer>> getFlowMapIterator();
}
