package maxflow;

public interface AugmentationInformantIF {
	/**
	 * searches the augmented edges in the previous round for the edgeID and
	 * returns the flow augmented through it
	 * @param edgeID edge ID
	 * @return flow augmented throw the passed edge; zero if no flow augmented
	 *         through this edge
	 */
	public int getAugmentedFlow(long edgeID);
}
