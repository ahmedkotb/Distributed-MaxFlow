package maxflow;

/**
 * This class contains:
 * 		- general options / settings to tune the algorithm
 * 		- public and shared variables needed by the max flow algorithm
 * @author lifemaker
 *
 */
public class MaxFlowSettings {
	
	public static final boolean ELIMINATE_CYCLES = true; //affects the Path class
	public static enum counters{SOURCE_MOVE, SINK_MOVE}; //counters needed for the algoirthm
	public static final int SRC_NODE_ID = -1; //TODO
	public static final int SINK_NODE_ID = -1; //TODO
	public static final String OUTPUT_PATH = ""; //TODO
	public static int currentRound = 0;
}
