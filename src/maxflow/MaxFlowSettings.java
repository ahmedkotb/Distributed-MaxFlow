package maxflow;

/**
 * This class contains:
 * 		- general options / settings to tune the algorithm
 * 		- public and shared variables needed by the max flow algorithm
 * @author lifemaker
 *
 */
public class MaxFlowSettings {
	
	public static enum counters{SOURCE_MOVE, SINK_MOVE, AUGMENTED_FLOW}; //counters needed for the algoirthm
	
	public static final int SRC_NODE_ID = 2; //TODO to be changed according to input graph!
	public static final int SINK_NODE_ID = 5; //TODO to be changed according to input graph!

	public static final String MAXFLOW_PATH = "hdfs://localhost:54310/user/lifemaker/maxflow"; //TODO to be changed according to host machine!
	public static final String INPUT_PATH = MAXFLOW_PATH + "/raw_input/soc-Epinions1.txt";
//	public static final String INPUT_PATH = MAXFLOW_PATH + "/raw_input/test0";
//	public static final String MAXFLOW_PATH = "s3n://maxflowbucket/maxflow";
//	public static final String INPUT_PATH = MAXFLOW_PATH + "/rawInput/soc-Epinions1.txt";
	public static final String OUTPUT_PATH = MAXFLOW_PATH + "/round_0";

	public static final String HADOOP_CORE_SITE_PATH = "src/core-site.xml";
	
	public static int currentRound = 0;
	public static final int K = 5; //for FF1: max. number of accepted excess paths
	public static final int INFINITY = Integer.MAX_VALUE >> 1;
	public static int NUMBER_OF_NODES = 5;  // TODO
}
