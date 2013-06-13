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
		
		System.out.println("Reading node ...");
		Node u = new Node(value.toString());
		List<Path> srcPaths = u.getSourcePaths();
		List<Path> sinkPaths = u.getSinkPaths();
		List<Edge> edges = u.getEdges();

		//reading augmentedEdges file, and augmenting flow
		if(MaxFlowSettings.currentRound > 1){
			System.out.println("Reading augmentedEdges[" + (MaxFlowSettings.currentRound-1) + "] from distributed cache ...");
			String augFilePath = MaxFlowSettings.MAXFLOW_PATH + "/augmentedEdges[" + (MaxFlowSettings.currentRound - 1) + "]";
			AugmentationInformantIF informant = new AugmentationInformant(augFilePath);
			augmentNode(u, informant);
		}
		
		int srcPathsSize = srcPaths.size();
		int sinkPathsSize = sinkPaths.size();
		int srcPathsIndex = 0;
		int sinkPathsIndex = 0;
		
		//checking for complete augmenting paths
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
		
		//extending source excess paths
		if(srcPathsSize > 0){
			for(Edge e : edges){
				if(!e.isSaturated() && !u.isVisitedNeighbour(e.getToNodeId())){
					Path p = srcPaths.get(srcPathsIndex).clone();
					if(p.extend(e)){
						u.visitNeighbour(e.getToNodeId());
						srcPathsIndex = (srcPathsIndex + 1) % srcPathsSize;
						Node v = new Node(e.getToNodeId());
						v.addSourcePath(p);
						collector.collect(new LongWritable(v.getId()), new Text(v.toString()));
					}
				}
			}
		}
		
		//extending sink excess paths
		if(sinkPathsSize > 0){
			for(Edge e : edges){
				Edge re = e.getReversedEdge(u.getId()); //reversed edge
				if(!re.isSaturated()){
					Path p = sinkPaths.get(sinkPathsIndex).clone();
					if(p.extend(re)){
						sinkPathsIndex = (sinkPathsIndex + 1) % sinkPathsSize;
						Node v = new Node(e.getToNodeId()); //e not re
						v.addSinkPath(p);
						collector.collect(new LongWritable(v.getId()), new Text(v.toString()));
					}
				}
			}
		}
		
		//emit master node
		collector.collect(new LongWritable(u.getId()), new Text(u.toString()));
	}

	/**
	 * Encapsulates the +flow vs -flow part. It queries augmentedEdges for this edge or its reversed counterpart
	 * and returns +dflow or -dflow correspondingly
	 * @param eid Edge id
	 * @param informant Responsible for querying augmented edges from the previous round
	 * @return delta flow that needs to be augmented for edge(eid), or 0 if this edge is not augmented
	 */
	private Integer getAugmentedFlow(long eid, AugmentationInformantIF informant){
		int flow = informant.getAugmentedFlow(eid);
		if(flow != 0) return flow;

		flow = informant.getAugmentedFlow(-1*eid);
		if(flow != 0) return -1*flow;
		
		return 0;
	}

	/**
	 * Constructs the residual node by augmenting all edges, and removing saturated excess paths
	 * @param u Graph node to augment
	 * @param informant Responsible for querying augmented edges from the previous round
	 */
	private void augmentNode(Node u, AugmentationInformantIF informant){
		
		//Augmenting edges in edge list
		for(Edge e : u.getEdges()){
			int flow = getAugmentedFlow(e.getId(), informant);
			if(flow != 0)
				e.augment(flow);
		}
		
		//Augmenting edges in source paths
		for(int i = u.getSourcePaths().size()-1; i>=0; --i){
			boolean pathRemoved = false;
			for(Edge e : u.getSourcePaths().get(i).getEdges()){
				int flow = getAugmentedFlow(e.getId(), informant);
				if(flow != 0){
					e.augment(flow);
					if(e.isSaturated()){
						u.removeSourcePath(i);
						pathRemoved = true;
						break; //no need to complete this path
					}
				}
			}
			
			if(pathRemoved)
				continue;
			//Augmenting extending edges
			for(Edge e : u.getSourcePaths().get(i).getExtendingEdges()){
				int flow = getAugmentedFlow(e.getId(), informant);
				if(flow != 0){
					e.augment(flow);
					if(e.isSaturated())
						u.freeNeighbour(e.getToNodeId());
				}
			}
		}
		
		//Augmenting edges in sink paths
		for(int i = u.getSinkPaths().size()-1; i>=0; --i){
			boolean pathRemoved = false;
			for(Edge e : u.getSinkPaths().get(i).getEdges()){
				int flow = getAugmentedFlow(e.getId(), informant);
				if(flow != 0){
					e.augment(flow);
					if(e.isSaturated()){
						u.removeSinkPath(i);
						pathRemoved = true;
						break; //no need to complete this path
					}
				}
			}
			
			if(pathRemoved)
				continue;
			//Augmenting extending edges
			for(Edge e : u.getSinkPaths().get(i).getExtendingEdges()){
				int flow = getAugmentedFlow(e.getId(), informant);
				if(flow != 0){
					e.augment(flow);
					if(e.isSaturated())
						u.freeNeighbour(e.getToNodeId());
				}
			}

		}
	}
}
