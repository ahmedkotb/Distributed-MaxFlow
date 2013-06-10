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

	/*
	 * the reducer outputs two different files: the graph and the augmentedEdges files
	 * check the class: org.apache.hadoop.mapred.lib.MultipleTextOutputFormat
	 * useful links:
	 * http://hadoop.apache.org/docs/stable/api/
	 * https://sites.google.com/site/hadoopandhive/home/how-to-write-output-to-multiple-named-files-in-hadoop-using-multipletextoutputformat
	 */
	@Override
	public void reduce(LongWritable key, Iterator<Text> values,
			OutputCollector<NullWritable, Text> collector, Reporter reporter)
			throws IOException {
		Node oldMaster = null;
		Node newMaster = new Node(key.get());
		
		Accumulator srcAcc = new Accumulator();
		Accumulator sinkAcc = new Accumulator();
		Accumulator tAcc = new Accumulator();
		Text value = null;
		
		while(values.hasNext()){
			value = values.next();
			Node u = new Node(value.toString());
			if(u.getEdges() != null && u.getEdges().size() > 0){ //master node
				oldMaster = u;
				newMaster.setEdges(oldMaster.getEdges());
			}
			
			for(Path p : u.getSourcePaths()){
				//XXX beware of the == (should be .equals() to be be valid if we changed the ID datatype)
				if(u.getId() == MaxFlowSettings.SINK_NODE_ID)
					tAcc.accept(p);
				else if(srcAcc.size() < MaxFlowSettings.K && srcAcc.accept(p))
					newMaster.addSourcePath(p);
			}
			
			for(Path p : u.getSinkPaths()){
				if(sinkAcc.size() < MaxFlowSettings.K && sinkAcc.accept(p))
					newMaster.addSinkPath(p);
			}	
		}
		
		if(oldMaster.getSourcePaths().size() == 0 && srcAcc.size() > 0)
			reporter.incrCounter(MaxFlowSettings.counters.SOURCE_MOVE, 1);
		
		if(oldMaster.getSinkPaths().size() == 0 && sinkAcc.size() > 0)
			reporter.incrCounter(MaxFlowSettings.counters.SINK_MOVE, 1);
		
		if(key.get() == MaxFlowSettings.SINK_NODE_ID){
			reporter.incrCounter(MaxFlowSettings.counters.AUGMENTED_FLOW, tAcc.getFlow());
			generateAugmentationFile(tAcc);
		}
		
		collector.collect(NullWritable.get(), new Text(newMaster.toString()));
	}

	public void generateAugmentationFile(Accumulator acc){
		try{
			String outputFile = MaxFlowSettings.MAXFLOW_PATH + "/augmentedEdges[" + MaxFlowSettings.currentRound + "]";
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
