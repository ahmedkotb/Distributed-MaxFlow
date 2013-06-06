package inputprepare;

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

public class InputPrepareReducer extends MapReduceBase
		implements Reducer<LongWritable, Text, NullWritable, Text> {

	@Override
	public void reduce(LongWritable key, Iterator<Text> values,
			OutputCollector<NullWritable, Text> output, Reporter reporter)
			throws IOException {
		
		long id = key.get();
		Node n = new Node(id);
		
		while (values.hasNext()) {
			// did == destination id
			String eInfoStr = values.next().toString();
			
			String[] eInfo = eInfoStr.split(",");
			
			long eid = Long.parseLong(eInfo[1]);
			
			if (eInfo[2].equals("f")) {
				long did = Long.parseLong(eInfo[0]);
				n.addEdge(new Edge(eid, did, 0, 1));
			}else if (eInfo[2].equals("i")) {
				long sid = Long.parseLong(eInfo[0]);
				Edge e = new Edge(eid, sid, 0,1);
				e.setIncoming(true);
				n.addEdge(e);
			}
		}
		
		Text txt = new Text(n.toString());
		output.collect(NullWritable.get(), txt);
	}

}
