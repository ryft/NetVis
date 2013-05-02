package netvis.visualisations.comets;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MapExperimental {

	// Basic size of the node
	int base = 400;
	int width;
	int height;

	// Connections to be drawn
	HashMap<String, Connection> connections;

	Random rand;

	class NamedThreadFactory implements ThreadFactory {
		int i = 0;

		public Thread newThread(Runnable r) {
			return new Thread(r, "Node animating thread #" + (i++));
		}
	}

	// Animation of the nodesByName can be parallelised
	ExecutorService exe = new ThreadPoolExecutor(4, 8, 5000, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory());
	
	public MapExperimental() {
		// TODO Auto-generated constructor stub
	}

}
