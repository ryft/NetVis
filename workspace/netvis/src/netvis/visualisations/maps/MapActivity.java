package netvis.visualisations.maps;

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

import junit.framework.Assert;

import netvis.data.model.Packet;
import netvis.visualisations.comets.Connection;
import netvis.visualisations.comets.FlipNode;
import netvis.visualisations.comets.GraphNode;
import netvis.visualisations.comets.HeatNode;
import netvis.visualisations.comets.MultiNode;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.Painter;
import netvis.visualisations.gameengine.Position;
import netvis.visualisations.gameengine.Units;

public class MapActivity extends Map {

	MapPainter painter;
	
	// Top dimension
	int dim;
	
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
	
	public MapActivity(int w, int h) {
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
		MultiNode groupnode = nodesByName.get(groupname);
		if (groupnode == null)
		{
			// If there is no grouping node - create it
			groupnode = new MultiNode (2, null);
			groupnode.SetName(groupname);
			nodesByName.put(groupname, groupnode);

			// Try putting the newly created group somewhere
			boolean placed = false;
			for (MultiNode n : nodes.values())
			{
				if (n.AddNode(groupname, groupnode))
				{
					placed = true;
					break;
				}
			}
			
			// If there is nowhere to place the new group - add the top level node
			if (!placed)
			{
				MultiNode newtop = AllocateTopLevelNode ();
				newtop.AddNode(groupname, groupnode);
			}
				
			// Put the indicator node in the middle of the group node
			HeatNode midnode = new HeatNode(textureName, groupname);
			midnode.setBGColor(0.5, 0.6, 1.0);

			groupnode.AddNode (groupname, midnode);
		}

		// If the node is not already there - add it
		Node found = groupnode.GetNode (nodename);
		if (found == null)
		{
			// Prepare the new node
			HeatNode front = new HeatNode  (textureName, nodename);
			GraphNode back = new GraphNode (nodename);

			// Make it into the flip node - the node that has two sides
			FlipNode newnode = new FlipNode (front, back);
			
			if (TryAdding (newnode, groupnode) == null)
			{
				boolean placed = false;
				MultiNode parent = (MultiNode) groupnode.GetParent();
				if (parent == null)
				{
					// If this is the top level node - resize everything
					ResizeBaseNode ();
					
					// Now we need to try adding this node once again
					placed = (TryAdding (newnode, groupnode) != null);

					if (!placed)
					{
						MultiNode newtop = AllocateTopLevelNode ();
						placed = newtop.AddNode (groupname, groupnode);
					}
					
					// It has to fit now
					Assert.assertEquals(placed, true);
				}

				if (!placed)
				{
					// Take the node out
					parent.DetachNode (groupnode);
					
					// Wrap it in the bigger node
					MultiNode trygroupnode = WrapExpand (groupnode);

					// Try putting the node somewhere else
					for (MultiNode n : nodes.values())
					{
						if (TryAdding (trygroupnode, n) != null)
						{
							placed = true;
							groupnode = trygroupnode;
							break;
						}
					}
				}
				
				// If that failed try putting the node in the new top level suernode
				if (!placed)
				{
					MultiNode newtop = AllocateTopLevelNode ();
					placed = newtop.AddNode (groupname, groupnode);
				}
				
				TryAdding (newnode, groupnode);
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
		if ((parent != null) && (parent.GetSubSize() == 1))
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
		
		for (Entry<String, MultiNode> en : nodesByName.entrySet())
		{
			// Try putting the node somewhere else
			boolean placed = false;
			for (MultiNode n : nodes.values())
			{
				if (TryAdding (en.getValue(), n) != null)
				{
					placed = true;
					break;
				}
			}
			
			if (!placed)
			{
				// If there is no top level grouping node - create it
				MultiNode newtoplevel = AllocateTopLevelNode ();
				
				newtoplevel.AddNode (en.getKey(), en.getValue());
			}
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
		wrapper.AddNode (groupname, groupnode);
		
		return wrapper;
	}

	@Override
	public Node FindClickedNode (int x, int y)
	{
		// Optimised version
		
		// Center of the clicked meta-node
		Position centercoo = Units.CoordinateByPosition (base, new Position(x, y));
		System.out.println("Clicked near the node at metaposition: (" + centercoo.x + ", " + centercoo.y + ")");
		
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
	
}
