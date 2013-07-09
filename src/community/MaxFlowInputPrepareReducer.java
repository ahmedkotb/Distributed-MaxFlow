package community;

import graph.Edge;
import graph.Node;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class MaxFlowInputPrepareReducer extends MapReduceBase
		implements Reducer<LongWritable, Text, NullWritable, Text> {

	//TODO: find another way to assign ids to src and dst nodes
	public static final long SRC_ID = 101010101;
	public static final long DST_ID = 010101010;
	public static final int INF = 999999999;
	
	@Override
	public void reduce(LongWritable id, Iterator<Text> values,
			OutputCollector<NullWritable, Text> output, Reporter arg3)
			throws IOException {
		
		long nid = id.get();
		if (nid == SRC_ID || nid == DST_ID) {
			Node n = new Node(nid);
			
			while (values.hasNext()) {
				Node o = new Node(values.next().toString());
				for (Edge e : o.getEdges())
					n.addEdge(e);
			}
			
			Text txt = new Text(n.toString());
			output.collect(NullWritable.get(), txt);
		}else {
			//only one value as node is sent one time only in mapper
			while (values.hasNext()) {
				Text txt = values.next();
				output.collect(NullWritable.get(), txt);
			}
		}
	}

}
