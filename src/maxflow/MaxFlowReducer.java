package maxflow;

import graph.Node;
import graph.Path;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class MaxFlowReducer extends MapReduceBase implements Reducer<LongWritable, Text, NullWritable, Text>{

	@Override
	public void reduce(LongWritable key, Iterator<Text> values,
			OutputCollector<NullWritable, Text> collector, Reporter reporter)
			throws IOException {
		Node oldMaster = null; //holder for the old node
		Node newMaster = new Node(key.get()); //will hold the updated node
		
		Accumulator srcAcc = new Accumulator();
		Accumulator sinkAcc = new Accumulator();
		Accumulator tAcc = new Accumulator(); //accumulator for the sink node
		Text value = null;
		
		while(values.hasNext()){
			value = values.next();
			Node u = new Node(value.toString());
			
			if(u.getEdges() != null && u.getEdges().size() > 0){ //master node
				oldMaster = u;
				newMaster.setEdges(oldMaster.getEdges());
			}
			
			//merge and filter source excess paths
			for(Path p : u.getSourcePaths()){
				//XXX beware of the == (should be .equals() to be be valid if we changed the ID data type)
				if(u.getId() == MaxFlowSettings.SINK_NODE_ID) //augmenting path
					tAcc.accept(p);
//				else if(srcAcc.size() < MaxFlowSettings.K && srcAcc.accept(p))
				else if(srcAcc.accept(p))
					newMaster.addSourcePath(p);
			}
			
			//merge and filter sink excess paths
			for(Path p : u.getSinkPaths()){
//				if(sinkAcc.size() < MaxFlowSettings.K && sinkAcc.accept(p))
				if(sinkAcc.accept(p))
					newMaster.addSinkPath(p);
			}	
		}
		
		//reaching an unexplored node via source excess paths
		if(oldMaster.getSourcePaths().size() == 0 && srcAcc.size() > 0)
			reporter.incrCounter(MaxFlowSettings.counters.SOURCE_MOVE, 1);
		
		//reaching an unexplored node via sink excess paths
		if(oldMaster.getSinkPaths().size() == 0 && sinkAcc.size() > 0)
			reporter.incrCounter(MaxFlowSettings.counters.SINK_MOVE, 1);
		
		//incrementing flow and generating augmentedEdges file for next round
		if(key.get() == MaxFlowSettings.SINK_NODE_ID){
			reporter.incrCounter(MaxFlowSettings.counters.AUGMENTED_FLOW, tAcc.getFlow());
			generateAugmentationFile(tAcc);
		}
		
		//emit updated node
		collector.collect(NullWritable.get(), new Text(newMaster.toString()));
	}

	/**
	 * Generates augmentedEdges file to be read in the next round
	 * @param acc Accumulator of the sink node. It contains the merged augmenting paths of this round
	 */
	public void generateAugmentationFile(Accumulator acc){
		try{
			String outputFile = MaxFlowSettings.MAXFLOW_PATH + "/augmentedEdges[" + MaxFlowSettings.currentRound + "]";
			
			//setting up configuration for the filesystem
			org.apache.hadoop.fs.Path pt = new org.apache.hadoop.fs.Path(outputFile);
			Configuration conf = new Configuration();
			conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
            
			FileSystem fs = FileSystem.get(conf);
            
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(pt,true)));

            Iterator<Entry<Long, Integer>> itr = acc.getFlowMapIterator();
            Entry<Long, Integer> entry;
            while(itr.hasNext()){
            	entry = itr.next();
            	bw.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
            
            bw.close();
	    }catch(Exception e){
	            e.printStackTrace();
	    }
	}
}
