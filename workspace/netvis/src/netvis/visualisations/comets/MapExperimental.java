package netvis.visualisations.comets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL2;

import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.Painter;
import netvis.visualisations.gameengine.Position;
import netvis.visualisations.gameengine.Units;

public class MapExperimental {

	MapPainter painter;
	
	// Top dimension
	int dim;
	
	// Basic size of the node
	int base = 400;
	int width;
	int height;
	
	// Top level nodes
	HashMap<Position, MultiNode> nodes;
	
	// Different way of storing them
	HashMap<String, MultiNode> nodesByName;

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
	
	public MapExperimental(int w, int h) {
		width = w;
		height = h;
		
		nodes = new HashMap<Position, MultiNode> ();
		nodesByName = new HashMap<String, MultiNode>();
		
		// Start with one node 14x14
		dim = 5;

		rand = new Random();
		
		painter = new MapPainter();
	}

	public void DrawEverything(GL2 gl) {

		Painter.DrawGrid(base, gl);
		for (Entry<Position, MultiNode> en : nodes.entrySet()) {
			Position pos = en.getKey();
			MultiNode mn = en.getValue();
			
			Position realp = Units.PositionByCoordinate(base, pos);

			gl.glPushMatrix();
				// Transpose it to the right spot
				gl.glTranslated (realp.x, realp.y, 0.0);
	
				// Draw it
				mn.Draw(base, painter, gl);
			gl.glPopMatrix();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void StepAnimation(final long time) throws InterruptedException, ExecutionException {
		ArrayList<Future<?>> list = new ArrayList<Future<?>>();
		for (final Node i : nodes.values())
			list.add(exe.submit(new Callable() {
				@Override
				public Object call() throws Exception {
					i.UpdateAnimation(time);
					return null;
				}
			}));

		for (Future<?> i : list)
			i.get();
	}

	public void SetSize(int w, int h, GL2 gl) {
		width = w;
		height = h;
	}

	public void SuggestNode (String sip, String dip) {
		// Suggests the existence of the node in the network to be displayed
		AddNode (dip, sip, "basic");
	}
	
	private Node AddNode(String near, String name, String textureName) {

		// Look for the wrapping node
		MultiNode nearnode = nodesByName.get(near);
		if (nearnode == null)
		{
			// If there is no grouping node - create it
			Position ringshift = Units.FindSpotAround(nodes.size());
			
			Position coord = Units.CoordinateByRingAndShift (dim, ringshift.x, ringshift.y);
			System.out.println("Node " + name + " placed in coords : " + coord.x + ", " + coord.y);
			
			nearnode = new MultiNode(dim);
			
			nodes.put(coord, nearnode);
			nodesByName.put(near, nearnode);
		}

		// If the node is not already there - add it
		Node found = nearnode.GetNode (name);
		if (found == null)
		{
			// Prepare the new node
			HeatNode front = new HeatNode  (textureName, name);
			GraphNode back = new GraphNode (name);

			// Make it into the flip node - the node that has two sides
			FlipNode newnode = new FlipNode (front, back);
	
			nearnode.AddNode(name, newnode);
			found = newnode;
		}

		// Update the node with data
		found.UpdateWithData (near);

		return found;
	}
	
	public Position FindClickedPosition (int x, int y)
	{
		Position p = new Position(x, y);
		Position c = Units.CoordinateByPosition(base, p);
		
		return c;
	}

	public Node FindClickedNode (int x, int y)
	{
		// Optimised version
		Position c = FindClickedPosition (x, y);

		Node node = nodes.get(c);
		return node;
	}

	public double ZoomOn() {
		double screenratio = (1.0 * width) / height;
		if (screenratio < Math.sqrt(3.0)) {
			return (base * Math.sqrt(3.0)) / width;
		}
		return (1.0 * base) / height;
	}
	
}
