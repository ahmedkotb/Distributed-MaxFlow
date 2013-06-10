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
	public static enum counters{SOURCE_MOVE, SINK_MOVE, AUGMENTED_FLOW}; //counters needed for the algoirthm
	public static final int SRC_NODE_ID = 1; //TODO
	public static final int SINK_NODE_ID = 4; //TODO
	public static final String MAXFLOW_PATH = "hdfs://localhost:54310/user/lifemaker/maxflow"; //TODO
	public static int currentRound = 0;
	public static final int K = 5;
	public static final int INFINITY = Integer.MAX_VALUE >> 1;
}
