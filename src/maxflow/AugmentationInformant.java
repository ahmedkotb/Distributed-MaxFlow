package maxflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class AugmentationInformant implements AugmentationInformantIF{

	private HashMap<Long, Integer> flowMap;
	
	public AugmentationInformant(String filePath) throws IOException{
		org.apache.hadoop.fs.Path pt = new org.apache.hadoop.fs.Path(filePath);
		Configuration conf = new Configuration();
		conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
        FileSystem fs = FileSystem.get(conf);
        
		BufferedReader bf = new BufferedReader(new InputStreamReader(fs.open(pt)));
//		BufferedReader bf = new BufferedReader(new FileReader(filePath));
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
