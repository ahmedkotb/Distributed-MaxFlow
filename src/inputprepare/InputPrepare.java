package inputprepare;

import maxflow.MaxFlowSettings;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.util.Tool;

public class InputPrepare extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		JobClient client = new JobClient();
		JobConf conf = new JobConf(inputprepare.InputPrepare.class);
		
		conf.addResource(new org.apache.hadoop.fs.Path(MaxFlowSettings.HADOOP_CORE_SITE_PATH));

		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(Text.class);

		FileInputFormat.setInputPaths(conf, new Path(MaxFlowSettings.INPUT_PATH));
		FileOutputFormat.setOutputPath(conf, new Path(MaxFlowSettings.OUTPUT_PATH));

		conf.setMapperClass(inputprepare.InputPrepareMapper.class);
		conf.setReducerClass(inputprepare.InputPrepareReducer.class);

		client.setConf(conf);
		
		try {
			System.out.println("Job submitted and waiting for it to finish ...");
			RunningJob job = JobClient.runJob(conf);
			job.waitForCompletion();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
