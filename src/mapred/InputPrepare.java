package mapred;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class InputPrepare extends Configured implements Tool {

	static final String inputDir = "/user/hduser/maxflow/raw_input";
	static final String outputDir = "/user/hduser/maxflow/input";
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new InputPrepare(), args);
		System.exit(res);	
	}

	@Override
	public int run(String[] args) throws Exception {
		JobClient client = new JobClient();
		JobConf conf = new JobConf(mapred.InputPrepare.class);

		conf.setOutputKeyClass(LongWritable.class);
		conf.setOutputValueClass(LongWritable.class);

		FileInputFormat.setInputPaths(conf, new Path(inputDir));
		FileOutputFormat.setOutputPath(conf, new Path(outputDir));

		conf.setMapperClass(mapred.InputPrepareMapper.class);
		conf.setReducerClass(mapred.InputPrepareReducer.class);

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
