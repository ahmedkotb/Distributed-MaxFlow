package maxflow;

import inputprepare.InputPrepare;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.ToolRunner;

public class DistributedMaxFlow {
	
	private JobConf configureJob(){
		JobConf conf = new JobConf();
		conf.setJobName("maxflow");
		conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
		
		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(Text.class);
		
		conf.setMapperClass(MaxFlowMapper.class);
//		conf.setCombinerClass(theClass); //TODO: can you come up with a good combiner?
		conf.setReducerClass(MaxFlowReducer.class);
		
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
		long totalFlow = 0;
		while(true){
			System.out.println("Running: Round #" + MaxFlowSettings.currentRound + " ...");
			JobConf conf = configureJob();
			RunningJob job = JobClient.runJob(conf); //this waits till job finishes
			Counters counters = job.getCounters();
			long flow = counters.getCounter(MaxFlowSettings.counters.AUGMENTED_FLOW);
			totalFlow += flow; 
			
			long sourceMoveCounter = counters.getCounter(MaxFlowSettings.counters.SOURCE_MOVE);
			long sinkMoveCounter = counters.getCounter(MaxFlowSettings.counters.SINK_MOVE);

			if(sourceMoveCounter == 0 || sinkMoveCounter == 0)
				break;
			
			MaxFlowSettings.currentRound++;
		}
		
		System.out.println("Maximum Flow Value = " + totalFlow);
	}
}
