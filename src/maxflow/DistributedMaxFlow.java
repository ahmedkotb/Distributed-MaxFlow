package maxflow;

import inputprepare.InputPrepare;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.ToolRunner;

public class DistributedMaxFlow {
	
	private static JobConf configureJob(){
		JobConf conf = new JobConf();
		conf.setJobName("maxflow");
		
		conf.setOutputKeyClass(NullWritable.class);
		conf.setOutputValueClass(Text.class);
		
		conf.setMapperClass(MaxFlowMapper.class);
//		conf.setCombinerClass(theClass); //TODO: can you come up with a good combiner?
		conf.setReducerClass(MaxFlowReducer.class);
		
		String augmentedEdgesPath = MaxFlowSettings.OUTPUT_PATH + "/[" + (MaxFlowSettings.currentRound-1) + "]";
		try {
			DistributedCache.addCacheFile(new URI(augmentedEdgesPath), conf);
		} catch (URISyntaxException e) {
			System.err.println("DistributedCache: File Not Found! Invalid URI!");
			e.printStackTrace();
		}
		
		FileInputFormat.setInputPaths(conf, MaxFlowSettings.INPUT_PATH);
		FileOutputFormat.setOutputPath(conf, new Path(MaxFlowSettings.OUTPUT_PATH));

		return conf;
	}
	
	public static void run() throws IOException{
		//TODO: check if the input graph is already created, then don't call InputPrepare
		try {
			ToolRunner.run(new Configuration(), new InputPrepare(), null);
		} catch (Exception e) {
			System.err.println("Failed to prepare input graph. Terminating the Job!");
			e.printStackTrace();
			System.exit(1);
		}
	
		MaxFlowSettings.currentRound = 1;
		while(true){
			JobConf conf = configureJob();
			RunningJob job = JobClient.runJob(conf); //this waits till job finishes
			Counters counters = job.getCounters();
			long sourceMoveCounter = counters.getCounter(MaxFlowSettings.counters.SOURCE_MOVE);
			long sinkMoveCounter = counters.getCounter(MaxFlowSettings.counters.SINK_MOVE);
			
			if(sourceMoveCounter == 0 || sinkMoveCounter == 0)
				break;
			
			MaxFlowSettings.currentRound++;
		}
	}
}
