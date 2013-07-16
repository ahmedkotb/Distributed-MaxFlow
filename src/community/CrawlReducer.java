package community;

import graph.Edge;
import graph.Node;
import graph.NodeColor;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class CrawlReducer extends MapReduceBase
		implements Reducer<LongWritable, Text, LongWritable, Text>  {

	@Override
	public void reduce(LongWritable id, Iterator<Text> values,
			OutputCollector<LongWritable, Text> output, Reporter reporter)
			throws IOException {
		
		Node mainNode = null;
		Node whiteNode = null;
		while (values.hasNext()) {
			Text value = values.next();
			
			Node u = new Node(value.toString());

			// One (and only one) copy of the node will be the fully expanded
			// version, which is colored in black
			if (u.getColor().equals(NodeColor.BLACK))
				mainNode = u;
			else if (u.getColor().equals(NodeColor.WHITE))
				whiteNode = u;
		}
		
		if (mainNode == null) {
			//white node detected (only if there is no darker nodes sent to reducer)
			Text txt = new Text(whiteNode.toString());
			output.collect(new LongWritable(whiteNode.getId()), txt);
		}else{
			Text txt = new Text(mainNode.toString());
			output.collect(new LongWritable(mainNode.getId()), txt);
		}
		
	}


}
