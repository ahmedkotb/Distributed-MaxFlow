package community;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class SeedsHandler implements SeedsHandlerIF {

	private HashSet<Long> seeds;
	
	public SeedsHandler(String filePath) throws IOException {
		org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(filePath);

		//setting up configuration for the filesystem
		Configuration conf = new Configuration();
		conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
        FileSystem fs = FileSystem.get(conf);
        
        //read augmentedEdges file
		BufferedReader bf = new BufferedReader(new InputStreamReader(fs.open(path)));
		seeds = new HashSet<Long>();
		String line;
		Long id;
		while((line = bf.readLine()) != null){
			id = Long.parseLong(line);
			seeds.add(id);
		}
		bf.close();
	}
	
	@Override
	public boolean addSeed(long id) {
		return seeds.add(id);
	}

	@Override
	public boolean isSeed(long id) {
		return seeds.contains(id);
	}

	@Override
	public void writeSeeds(String filePath) throws IOException {
		
		//setting up configuration for the filesystem
		org.apache.hadoop.fs.Path pt = new org.apache.hadoop.fs.Path(filePath);
		Configuration conf = new Configuration();
		conf.addResource(new org.apache.hadoop.fs.Path("/usr/local/hadoop/conf/core-site.xml"));
        
		FileSystem fs = FileSystem.get(conf);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs.create(pt,true)));

        Iterator<Long> itr = seeds.iterator();
        Long id;
        while(itr.hasNext()){
        	id = itr.next();
        	bw.write(id + "\n");
        }
        
        bw.close();	
	}

	@Override
	public int getSeedsSize() {
		return seeds.size();
	}

}
