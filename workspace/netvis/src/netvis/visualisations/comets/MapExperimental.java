package netvis.visualisations.comets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import netvis.data.model.Packet;
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
		Painter.GenerateGrid ("grid", dim);

		rand = new Random();
		
		painter = new MapPainter();
	}

	public void DrawEverything(GL2 gl) {

		for (Entry<Position, MultiNode> en : nodes.entrySet()) {
			Position pos = en.getKey();
			MultiNode mn = en.getValue();
			
			Position realp = Units.PositionByCoordinate (base, pos);

			gl.glPushMatrix();
				// Transpose it to the right spot
				gl.glTranslated (realp.x, realp.y, 0.0);
	
				// Draw it
				Painter.DrawGrid (base, dim, "grid", gl);
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

	public void SuggestNode (String sip, String dip, List<Packet> packets) {
		// Suggests the existence of the node in the network to be displayed
		Node nnn = AddNode (dip, sip, "basic");

		// Update the node with data
		for (Packet pp : packets)
			nnn.UpdateWithData(pp);
	}
	
	private Node AddNode (String groupname, String nodename, String textureName) {

		// Look for the wrapping node
		MultiNode nearnode = nodesByName.get(groupname);
		if (nearnode == null)
		{
			// If there is no grouping node - create it
			nearnode = new MultiNode (2, null);
			nearnode.SetName(groupname);
			nodesByName.put(groupname, nearnode);

			// Try putting the newly created group somewhere
			boolean placed = false;
			for (MultiNode n : nodes.values())
			{
				if (n.AddNode(groupname, nearnode))
				{
					placed = true;
					break;
				}
			}
			
			// If there is nowhere to place the new group - add the top level node
			if (!placed)
			{
				MultiNode newtop = AllocateTopLevelNode ();
				newtop.AddNode(groupname, nearnode);
			}
				
			// Put the indicator node in the middle of the group node
			HeatNode midnode = new HeatNode(textureName, groupname);
			midnode.setBGColor(0.5, 0.6, 1.0);

			nearnode.AddNode (groupname, midnode);
		}

		// If the node is not already there - add it
		Node found = nearnode.GetNode (nodename);
		if (found == null)
		{
			// Prepare the new node
			HeatNode front = new HeatNode  (textureName, nodename);
			GraphNode back = new GraphNode (nodename);

			// Make it into the flip node - the node that has two sides
			FlipNode newnode = new FlipNode (front, back);
			
			if (TryAdding (newnode, nearnode) == null)
			{
				boolean placed = false;
				MultiNode parent = (MultiNode) nearnode.GetParent();
				if (parent == null)
				{
					// If this is the top level node - resize everything
					ResizeBaseNode ();
					
					// Now we need to try adding this node once again
					TryAdding (newnode, nearnode);
					
					placed = true;
				}

				if (!placed)
				{
					// Take the node out
					parent.DetachNode (nearnode);
					
					// Wrap it in the bigger node
					nearnode = WrapExpand (nearnode);

					// Try putting the node somewhere else
					for (MultiNode n : nodes.values())
					{
						if (TryAdding (nearnode, n) != null)
						{
							placed = true;
							break;
						}
					}
				}
				
				// If that failed try putting the node in the new top level suernode
				if (!placed)
				{
					MultiNode newtop = AllocateTopLevelNode ();
					placed = newtop.AddNode (groupname, newnode);
				}
 			}

			found = newnode;
		}

		return found;
	}
	
	private MultiNode TryAdding (Node added, MultiNode group)
	{
		String groupname = group.GetName();
		String nodename  = added.GetName();

		if (group.AddNode(nodename, added))
			return group;

		MultiNode parent = (MultiNode) group.GetParent();
		if ((parent != null) && (parent.subnodes.size() == 1))
		{
			parent.AddNode  (nodename, added);
			
			parent.SetName  (groupname);
			nodesByName.put (groupname, parent);
			return parent;
		}
		
		// Impossible to expand - node has to be put somewhere else
		return null;
	}
	
	private void ResizeBaseNode ()
	{
		// First find the next supernode size
		int newdim = dim;
		while (Units.DimToCap(newdim) < 7*Units.DimToCap(dim))
			newdim += 1;
		dim = newdim;
		Painter.GenerateGrid ("grid", dim);
		
		// Now for all the existing nodes put them through the adding procedure again
		nodes = new HashMap<Position, MultiNode> ();
		
		int i = 0;
		for (Entry<String, MultiNode> en : nodesByName.entrySet())
		{
			// If there is no grouping node - create it
			Position ringshift = Units.FindSpotAround (i++);
			Position coord = Units.CoordinateByRingAndShift (dim, ringshift.x, ringshift.y);
			
			MultiNode nner = new MultiNode (dim, null);
			
			nner.AddNode (en.getKey(), en.getValue());
			
			nodes.put (coord, nner);
		}
	}
	
	private MultiNode AllocateTopLevelNode ()
	{
		Position ringshift = Units.FindSpotAround(nodes.size());
		
		Position coord = Units.CoordinateByRingAndShift (dim, ringshift.x, ringshift.y);
		
		// Create the node - start small
		MultiNode groupnode = new MultiNode (dim, null);
		
		nodes.put(coord, groupnode);
		
		return groupnode;
	}
	
	private MultiNode WrapExpand (MultiNode groupnode)
	{
		String groupname = groupnode.GetName();
		
		// Find the new dimension
		int dim = groupnode.getDimenstion();
		int newdim = dim;
		while (Units.DimToCap(newdim) < 7*Units.DimToCap(dim))
			newdim += 1;
		
		// Create a wrapper node
		MultiNode wrapper = new MultiNode (newdim, null);
		wrapper.SetName (groupname);
		nodesByName.put (groupname, wrapper);
		
		return wrapper;
	}


	public Node FindClickedNode (int x, int y)
	{
		// Optimised version
		
		// Center of the clicked meta-node
		Position centercoo = Units.MetaCoordinateByPosition (dim, base, new Position(x, y)); 
		
		MultiNode node = nodes.get(centercoo);

		if (node != null)
		{
			// Position of the center of the metanode in pixels
			Position centerrealpos = Units.MetaPositionByCoordinate(dim, base, centercoo);
			
			Position delta = new Position (x - centerrealpos.x, y - centerrealpos.y);

			return node.GetClickedNode (base, delta);
		}
		
		return null;
	}

	public double ZoomOn() {
		double screenratio = (1.0 * width) / height;
		if (screenratio < Math.sqrt(3.0)) {
			return (base * Math.sqrt(3.0)) / width;
		}
		return (1.0 * base) / height;
	}
	
}
