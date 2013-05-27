package mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class InputPrepareMapper extends MapReduceBase
			implements Mapper<LongWritable, Text, LongWritable, LongWritable> {
	

	@Override
	public void map(LongWritable lid, Text value,
			OutputCollector<LongWritable, LongWritable> output, Reporter reporter)
			throws IOException {
		
		String line = value.toString();
		line = line.trim();
		if (line.length() > 0 && line.charAt(0) != '#') {
			String[] nodeInfo = line.split("\t");
			long sid = Long.parseLong(nodeInfo[0]);
			long did = Long.parseLong(nodeInfo[1]);
			output.collect(new LongWritable(sid), new LongWritable(did));
		}
		
	}

}
