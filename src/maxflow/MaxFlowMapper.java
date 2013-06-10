package maxflow;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import graph.Edge;
import graph.Node;
import graph.Path;

public class MaxFlowMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, LongWritable, Text> {

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<LongWritable, Text> collector, Reporter reporter)
			throws IOException {
		
		Node u = new Node(value.toString());
		List<Path> srcPaths = u.getSourcePaths();
		List<Path> sinkPaths = u.getSinkPaths();
		List<Edge> edges = u.getEdges();

		// TODO augment edges and remove saturated excess paths
		String augFilePath = MaxFlowSettings.OUTPUT_PATH + "/augmentedEdges[" + (MaxFlowSettings.currentRound - 1) + "]";
		AugmentationInformantIF informant = new AugmentationInformant(augFilePath);
		
		int srcPathsSize = srcPaths.size();
		int sinkPathsSize = sinkPaths.size();
		int srcPathsIndex = 0;
		int sinkPathsIndex = 0;
		
		if(srcPathsSize > 0 && sinkPathsSize > 0){
			Node t = new Node(MaxFlowSettings.SINK_NODE_ID);
			LongWritable tKey = new LongWritable(t.getId());
			Accumulator acc = new Accumulator();

			for(Path srcPath : srcPaths){
				for(Path sinkPath : sinkPaths){
					Path augPath = srcPath.concatenate(sinkPath);
					if(acc.accept(augPath)){
						t.addSourcePath(augPath);
					}
				}
			}
			
			collector.collect(tKey, new Text(t.toString()));
		}
		
		if(srcPathsSize > 0){
			for(Edge e : edges){
				if(!e.isIncoming() && !e.isSaturated()){
					Path p = srcPaths.get(srcPathsIndex).clone();
					if(p.extend(e)){
						srcPathsIndex = (srcPathsIndex + 1) % srcPathsSize;
						Node v = new Node(e.getToNodeId());
						v.addSourcePath(p);
						collector.collect(new LongWritable(v.getId()), new Text(v.toString()));
					}
				}
			}
		}
		
		if(sinkPathsSize > 0){
			for(Edge e : edges){
				if(e.isIncoming() && !e.isSaturated()){
					Path p = sinkPaths.get(sinkPathsIndex).clone();
					if(p.extend(e)){
						sinkPathsIndex = (sinkPathsIndex + 1) % sinkPathsSize;
						Node v = new Node(e.getToNodeId());
						v.addSourcePath(p);
						collector.collect(new LongWritable(v.getId()), new Text(v.toString()));
					}
				}
			}
		}
		
		//emit master node
		collector.collect(new LongWritable(u.getId()), new Text(u.toString()));
	}

}
