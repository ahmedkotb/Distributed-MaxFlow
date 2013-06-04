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
}
