package community;

import graph.Edge;
import graph.Node;
import graph.NodeColor;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class CrawlMapper extends MapReduceBase
			implements Mapper<LongWritable, Text, LongWritable, Text>  {

	
	public static final String seedsFilePath = "/user/hduser/community/seeds";
	private SeedsHandlerIF seedsHandler;
	private boolean loaded = false;
	
	@Override
	public void map(LongWritable arg0, Text value,
			OutputCollector<LongWritable, Text> output, Reporter arg3)
			throws IOException {
		
		if (!loadSeedsFile(seedsFilePath))
			return;
		
		Node node = new Node(value.toString());

		// For each GRAY node or a seedNode, emit each of the edges as a new node (also GRAY)
		if (node.getColor().equals(NodeColor.GRAY) || seedsHandler.isSeed(node.getId())) {
			
			for (Edge e : node.getEdges()) {
				Node nNode = new Node(e.getToNodeId());
				nNode.setColor(NodeColor.GRAY);
				Text txt = new Text(nNode.toString());
				output.collect(new LongWritable(nNode.getId()), txt);
			}
			
			// We're done with this node now, color it BLACK
			node.setColor(NodeColor.BLACK);
		}

		/*
		// No matter what, we emit the input node
		// If the node came into this method GRAY, it will be output as BLACK
		*/
		
		Text txt = new Text(node.toString());
		output.collect(new LongWritable(node.getId()),txt);
		
	}
	
	private boolean loadSeedsFile(String path) {
		if (loaded == true)
			return true;
		
		try{
			seedsHandler = new SeedsHandler(path);
			loaded = true;
		}catch(IOException exception) {
			//TODO LOG ERROR
		}
		return true;
	}

}
