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

public class MaxFlowInputPrepareMapper extends MapReduceBase
			implements Mapper<LongWritable, Text, LongWritable, Text> {

	
	public static final String seedsFilePath = "/user/hduser/community/seeds";
	private SeedsHandlerIF seedsHandler;
	private boolean loaded = false;
	
	//TODO: find another way to assign ids to src and dst nodes
	public static final long SRC_ID = 101010101;
	public static final long DST_ID = 010101010;
	public static final int INF = 999999999;
	
	private Node srcNode = new Node(SRC_ID);
	private Node dstNode = new Node(DST_ID);
	
	
	OutputCollector<LongWritable, Text> output;
	
	@Override
	public void map(LongWritable lid, Text txt,
			OutputCollector<LongWritable, Text> output, Reporter arg3)
			throws IOException {
		
		this.output = output;
		
		if (!loadSeedsFile(seedsFilePath)) {
			//TODO: log error
			return;
		}
		
		Node node = new Node(txt.toString());
		
		if (seedsHandler.isSeed(node.getId())) {
			srcNode.addEdge(new Edge(srcNode.getId(), node.getId(), 0, INF));
		}
		
		if (node.getColor().equals(NodeColor.GRAY) || node.getColor().equals(NodeColor.BLACK)) {
			for (Edge e : node.getEdges()) {
				e.setCapacity(seedsHandler.getSeedsSize());
			}
			
			if (!seedsHandler.isSeed(node.getId()))
				node.addEdge(new Edge(node.getId(), dstNode.getId(), 0, 1));
			
			Text outTxt = new Text(node.toString());
			output.collect(new LongWritable(node.getId()), outTxt);
		}
		
	}
	
	public void close() {
		try {
			Text outTxt1 = new Text(srcNode.toString());
			output.collect(new LongWritable(srcNode.getId()), outTxt1);
			Text outTxt2 = new Text(dstNode.toString());
			output.collect(new LongWritable(dstNode.getId()), outTxt2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
