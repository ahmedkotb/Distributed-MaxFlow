package maxflow;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class MaxFlowReducer extends MapReduceBase implements Reducer<LongWritable, Text, LongWritable, Text>{

	/*
	 * the reducer outputs two different files: the graph and the augmentedEdges files
	 * check the class: org.apache.hadoop.mapred.lib.MultipleTextOutputFormat
	 * useful links:
	 * http://hadoop.apache.org/docs/stable/api/
	 * https://sites.google.com/site/hadoopandhive/home/how-to-write-output-to-multiple-named-files-in-hadoop-using-multipletextoutputformat
	 */
	@Override
	public void reduce(LongWritable arg0, Iterator<Text> arg1,
			OutputCollector<LongWritable, Text> arg2, Reporter arg3)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

}
