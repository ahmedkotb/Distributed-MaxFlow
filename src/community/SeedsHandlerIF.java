package community;

import java.io.IOException;

public interface SeedsHandlerIF {

	public boolean addSeed(long id);
	
	public boolean isSeed(long id);
	
	public void writeSeeds(String filePath) throws IOException;
	
	public int getSeedsSize();
	
}
