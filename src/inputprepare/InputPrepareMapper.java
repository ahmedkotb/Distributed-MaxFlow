package inputprepare;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class InputPrepareMapper extends MapReduceBase
			implements Mapper<LongWritable, Text, LongWritable, Text> {
	
	
	public static final long NUMBER_OF_NODES = 75888;

	@Override
	public void map(LongWritable lid, Text value,
			OutputCollector<LongWritable, Text> output, Reporter reporter)
			throws IOException {
		
		Text outIds = new Text();
		String line = value.toString();
		line = line.trim();
		
		if (line.length() > 0 && line.charAt(0) != '#') {
			String[] nodeInfo = line.split("\t");
			//source ID
			long sid = Long.parseLong(nodeInfo[0]) + 1;
			//Destinatino ID
			long did = Long.parseLong(nodeInfo[1]) + 1;
			
			// edge ID
			long eid = (sid - 1) * NUMBER_OF_NODES + did;
			
			//emit forward edge from node U
			outIds.set(did + "," + eid);
			output.collect(new LongWritable(sid), outIds);
						
			//emit negative augmentation edge
			outIds.set(sid + "," + (-1 * eid));
			output.collect(new LongWritable(did), outIds);
			
		}
		
	}

}
