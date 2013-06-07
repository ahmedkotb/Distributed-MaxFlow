package maxflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class AugmentationInformant implements AugmentationInformantIF{

	private HashMap<Long, Integer> flowMap;
	
	public AugmentationInformant(String filePath) throws IOException{
		BufferedReader bf = new BufferedReader(new FileReader(filePath));
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
