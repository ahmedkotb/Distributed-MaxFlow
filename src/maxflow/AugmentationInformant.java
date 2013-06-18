package maxflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class AugmentationInformant implements AugmentationInformantIF{

	private HashMap<Long, Integer> flowMap; //<EdgeID, aggregatedFlowThroughEdge> tuples representing augmentedEdges[round-1] file
	
	/**
	 * Constructor: reads augmentedEdges file and populates the flowMap with it
	 * @param filePath HDFS path for augmentedEdges[round-1] file
	 * @throws IOException
	 */
	public AugmentationInformant(String filePath) throws IOException{
		org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(filePath);

		//setting up configuration for the filesystem
		Configuration conf = new Configuration();
		conf.addResource(new org.apache.hadoop.fs.Path(MaxFlowSettings.HADOOP_CORE_SITE_PATH));
        FileSystem fs = FileSystem.get(conf);
        
        //read augmentedEdges file
		BufferedReader bf = new BufferedReader(new InputStreamReader(fs.open(path)));
		flowMap = new HashMap<Long, Integer>();
		String line;
		String[] toks;
		Long id;
		Integer dflow;
		while((line = bf.readLine()) != null){
			toks = line.split("[ \t]+");
			id = Long.parseLong(toks[0]);
			dflow = Integer.parseInt(toks[1]);
			flowMap.put(id, dflow);
		}
		bf.close();
	}
	
	@Override
	public int getAugmentedFlow(long edgeID) {
		Integer dflow = flowMap.get(edgeID);
		return dflow==null? 0 : dflow;
	}

}
