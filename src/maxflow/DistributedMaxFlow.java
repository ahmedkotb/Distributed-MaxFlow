package maxflow;

import inputprepare.InputPrepare;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DistributedMaxFlow  extends Configured implements Tool{

	private long totalFlow = 0;
	private long sourceMoveCounter = 0;
	private long sinkMoveCounter = 0;
	
	private JobConf configureJob(){
		JobConf conf = new JobConf();
		conf.setJobName("maxflow");
		conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
		
		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(Text.class);
		
		conf.setMapperClass(MaxFlowMapper.class);
//		conf.setCombinerClass(theClass); //TODO: can you come up with a good combiner?
		conf.setReducerClass(MaxFlowReducer.class);
		
		//add file to distributed cache //TODO
//		if(MaxFlowSettings.currentRound > 1){
//			String augmentedEdgesPath = MaxFlowSettings.MAXFLOW_PATH + "/[" + (MaxFlowSettings.currentRound-1) + "]";
//			try {
//				DistributedCache.addCacheFile(new URI(augmentedEdgesPath), conf);
//			} catch (URISyntaxException e) {
//				System.err.println("DistributedCache: File Not Found! Invalid URI!");
//				e.printStackTrace();
//			}
//		}

		String inDir = MaxFlowSettings.MAXFLOW_PATH + "/round_" + (MaxFlowSettings.currentRound-1);
		String outDir = MaxFlowSettings.MAXFLOW_PATH + "/round_" + (MaxFlowSettings.currentRound);
		FileInputFormat.setInputPaths(conf, inDir);
		FileOutputFormat.setOutputPath(conf, new Path(outDir));

		return conf;
	}
	
	public void run() throws IOException{
		//TODO: check if the input graph is already created, then don't call InputPrepare

		//creating a flow network out of the input graph
		try {
			System.out.println("Round #0: Preparing input graph");
			ToolRunner.run(new Configuration(), new InputPrepare(), null);
		} catch (Exception e) {
			System.err.println("Failed to prepare input graph. Terminating the Job!");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Input graph prepared successfully!");
		
		MaxFlowSettings.currentRound = 1;
		totalFlow = 0;

		//Main loop: chained map reduce jobs
		while(true){
			try {
				System.out.println("Triggering Round #" + MaxFlowSettings.currentRound);
				ToolRunner.run(new Configuration(), this, null);
			} catch (Exception e) {
				System.err.println("Round " + MaxFlowSettings.currentRound + " failed!");
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Round " + MaxFlowSettings.currentRound + " completed successfully!");
			
			if(sourceMoveCounter == 0 || sinkMoveCounter == 0)
				break;
			
			MaxFlowSettings.currentRound++;
		}
		
		System.out.println("Maximum Flow Value = " + totalFlow);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		JobClient client = new JobClient();
		JobConf conf = new JobConf(DistributedMaxFlow.class);

		conf.setJobName("maxflow");
		conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
		
		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(Text.class);
		
		conf.setMapperClass(MaxFlowMapper.class);
		conf.setReducerClass(MaxFlowReducer.class);
		
		String inDir = MaxFlowSettings.MAXFLOW_PATH + "/round_" + (MaxFlowSettings.currentRound-1);
		String outDir = MaxFlowSettings.MAXFLOW_PATH + "/round_" + (MaxFlowSettings.currentRound);
		FileInputFormat.setInputPaths(conf, inDir);
		FileOutputFormat.setOutputPath(conf, new Path(outDir));

		client.setConf(conf);
		
		try {
			RunningJob job = JobClient.runJob(conf);
			job.waitForCompletion();
			
			Counters counters = job.getCounters();
			
			//incrementing total flow
			long flow = counters.getCounter(MaxFlowSettings.counters.AUGMENTED_FLOW);
			totalFlow += flow; 
			
			//checking for termination condition
			sourceMoveCounter = counters.getCounter(MaxFlowSettings.counters.SOURCE_MOVE);
			sinkMoveCounter = counters.getCounter(MaxFlowSettings.counters.SINK_MOVE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return 0;
	}
}
