import java.io.IOException;

import maxflow.*;

public class MaxFlowRunner {

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		new DistributedMaxFlow().run();
		long end = System.currentTimeMillis();
		double runtime = (end-start)/1000.0;
		int minutes = (int) (runtime / 60);
		int seconds = (int) (runtime % 60);
		
		System.out.printf("Runtime = %d:%d minutes\n", minutes, seconds);
	}
}
