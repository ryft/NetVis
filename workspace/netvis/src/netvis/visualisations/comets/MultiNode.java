package netvis.visualisations.comets;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;

import netvis.data.model.Packet;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;
import netvis.visualisations.gameengine.Position;
import netvis.visualisations.gameengine.Units;

public class MultiNode extends Node {

	// Dimension of this node
	int dim;
	// Dimension of the subnode
	int subdim;

	@Override
	public int getDimenstion () {return dim;}
	@Override
	public int getCapacity   () {return Units.DimToCap (dim);}
	
	public int getSubDimension () {return subdim;}
	public int getFreeSlots ()
	{
		// If the size of the subnode is not set - the node is empty 
		if (subdim == -1)
			return 1;

		// Calculate how many rings can fit in the node
		int rings = 1;
		while (Units.DimToCap(rings) * Units.DimToCap(subdim) <= Units.DimToCap(dim))
		{
			// While the ring still fits
			rings += 1;
		}
		rings -= 1;
		
		return (Units.DimToCap (rings) - subnodes.size());
 	}
	
	HashMap <Position, Node> subnodes;
	HashMap <String,   Node> subnodesByName;
	
	private MultiNode parent = null;
	public MultiNode GetParent() {
		return parent;
	}
	
	@Override
	public Node GetNode (String name) {
		Node nn = subnodesByName.get(name);
		
		if (nn == null)
		{
			// Look in the subnodes
			for (Node n : subnodes.values())
			{
				nn = n.GetNode (name);
				// If found return it
				if (nn != null)
					return nn;
			}
		}
			
		return nn;
	}
	
	@Override
	public Node GetClickedNode (int base, Position pixelposition)
	{
		// Center of the clicked meta-node
		Position centercoo = Units.MetaCoordinateByPosition (subdim, base, pixelposition);
		
		Node chosen = subnodes.get(centercoo);
		if (chosen != null)
		{
			// Go deeper
			
			// Position of the center of the metanode in pixels
			Position centerrealpos = Units.MetaPositionByCoordinate(dim, base, centercoo);
			
			Position delta = new Position (pixelposition.x - centerrealpos.x, pixelposition.y - centerrealpos.y);
			
			return chosen.GetClickedNode (base, delta);
		}
					
		return null;
	}
	
	public MultiNode (int dimension, MultiNode par) {
		super();
		
		parent = par;
		
		dim = dimension;
		subdim = -1;
		
		subnodes = new HashMap<Position, Node> ();
		
		subnodesByName = new HashMap<String, Node> ();
	}
	
	@Override
	public void Draw(int base, NodePainter painter, GL2 gl) {
		// Draw each of the subnodes in the right position
		for (Entry<Position, Node> en : subnodes.entrySet())
		{
			Position coord = en.getKey();
			Node node = en.getValue();
			
			Position pos = Units.PositionByCoordinate (base, coord);
			
			gl.glPushMatrix();
				gl.glTranslated(pos.x, pos.y, 0.0);
				
				node.Draw (base, painter, gl);
				
			gl.glPopMatrix();
		}
		
		// For debug purposes draw a big hexagon around
		gl.glPushMatrix();
			gl.glLineWidth (3.0f);
			gl.glRotated(90.0, 0.0, 0.0, 1.0);
			//Painter.DrawHexagon(GL.GL_LINE_LOOP, 0.0, 0.0, (int) Math.round(base*Math.sqrt(3.0)*(dim-1)), gl);
		gl.glPopMatrix();
	}
	
	@Override
	public boolean AddNode (String name, Node n)
	{
		int reqdim = n.getDimenstion();
		if (subdim == -1)
		{
			// If the node is empty - check whether we can subdivide the node
			if (reqdim > FindNodeDim())
			{
				// We can put this node straight away - but we won't fit anything else
				subdim = reqdim;
				subnodes.put(new Position(0,0), n);
				return true;
			} else if (reqdim == FindNodeDim())
			{
				subdim = reqdim;
				AllocateSubnode (name, n);
				return true;
			} else
			{
				// We can put this node in the subnode
				subdim = FindNodeDim();
				
				// So allocate the subnode
				Node mn = new MultiNode (subdim, this);
				AllocateSubnode (null, mn);
				
				// And then add the considered node to the subnode
				return mn.AddNode (name, n);
			}
		} else
		{
			// Check that the node size is small enough to be allocated
			if (reqdim > subdim)
				return false;
			
			// If the node fits perfectly - allocate it straight away
			if (reqdim == subdim)
			{
				return AllocateSubnode (name, n);
			}
			
			// Try allocating this node in any of the subnodes
			for (Node nodum : subnodes.values())
			{
				if (nodum.AddNode (name, n))
					return true;
			}
			
			// If it failed - create a new subnode and allocate considered node in it
			Node mn = new MultiNode(subdim, this);
			mn.AddNode (name, n);
			return AllocateSubnode (null, mn);
		}
	}
	
	private boolean AllocateSubnode (String name, Node mn)
	{
		// First find the ring to go to
		Position rs = Units.FindSpotAround(subnodes.size());

		// If the ring is too far away we can not allocate the subnode
		if (rs.x > this.dim)
			return false;
		
		// Register the node with its name
		if (name != null)
			subnodesByName.put (name, mn);
		
		// Redraw this - it might be incorrect for the bigger ones
		Position p = Units.CoordinateByRingAndShift (subdim, rs.x, rs.y);

		// Add it to the right place
		subnodes.put (p, mn);
		return true;
	}
	
	public int FindNodeDim ()
	{
		int trydim = dim-1;
		while (7 * Units.DimToCap(trydim) > Units.DimToCap(dim))
			trydim--;
		
		return trydim;
	}
	
	@Override
	public void UpdateWithData(Packet pp) {
		// TODO Auto-generated method stub
	}

	@Override
	public void UpdateAnimation(long time) {
		// TODO Auto-generated method stub
	}

	@Override
	public int Priority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void MouseClick(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
