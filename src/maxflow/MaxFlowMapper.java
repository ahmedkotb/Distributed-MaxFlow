package maxflow;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class MaxFlowMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, LongWritable, Text> {

	@Override
	public void map(LongWritable arg0, Text arg1,
			OutputCollector<LongWritable, Text> arg2, Reporter arg3)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

}
