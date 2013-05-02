package netvis.visualisations.comets;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;

import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;
import netvis.visualisations.gameengine.Position;

public class MultiNode extends Node {

	// Dimension of this node
	int dim;
	// Dimension of the subnode
	int subdim;
	
	// Side of the singular hex
	int base;

	@Override
	public int getDimenstion () {return dim;}
	@Override
	public int getCapacity   () {return DimToCap (dim);}
	
	public int getSubDimension () {return subdim;}
	public int getFreeSlots ()
	{
		// If the size of the subnode is not set - the node is empty 
		if (subdim == -1)
			return 1;

		// Calculate how many rings can fit in the node
		int rings = 1;
		while (DimToCap(rings) * DimToCap(subdim) <= DimToCap(dim))
		{
			// While the ring still fits
			rings += 1;
		}
		rings -= 1;
		
		return (DimToCap (rings) - subnodes.size());
 	}
	
	public int DimToCap (int dim)
	{
		return 3*(dim*dim - dim)+1;
	}
	
	HashMap <Position, Node> subnodes;
	
	public MultiNode(int dimension, int tbase) {
		super();
		
		dim = dimension;
		base = tbase;
		subdim = -1;
		
		subnodes = new HashMap<Position, Node> ();
	}
	
	@Override
	public void Draw(int base, NodePainter painter, GL2 gl) {
		// Draw each of the subnodes in the right position
		for (Entry<Position, Node> en : subnodes.entrySet())
		{
			Position coord = en.getKey();
			Node node = en.getValue();
			
			Position pos = PositionByCoordinate (base, coord);
			
			gl.glPushMatrix();
				gl.glTranslated(pos.x, pos.y, 0.0);
				
				node.Draw (base, painter, gl);
				
			gl.glPopMatrix();
		}
	}
	
	@Override
	public boolean AddNode (Node n)
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
				AllocateSubnode (n);
				return true;
			} else
			{
				// We can put this node in the subnode
				subdim = FindNodeDim();
				
				// So allocate the subnode
				Node mn = new MultiNode(subdim, base);
				AllocateSubnode(mn);
				
				// And then add the considered node to the subnode
				return mn.AddNode(n);
			}
		} else
		{
			// Check that the node size is small enough to be allocated
			if (reqdim > subdim)
				return false;
			
			// Try allocating this node in any of the subnodes
			for (Node nodum : subnodes.values())
			{
				if (nodum.AddNode(n))
					return true;
			}
			
			// If it failed - create a new subnode and allocate considered node in it
			Node mn = new MultiNode(subdim, base);
			mn.AddNode(n);
			return AllocateSubnode(mn);
		}
	}
	
	private boolean AllocateSubnode (Node mn)
	{
		// First find the ring to go to
		int outerring = 1;
		while (DimToCap(outerring) <= subnodes.size())
			outerring += 1;
		
		// If the ring is too far away we can not allocate the subnode
		if (outerring > this.dim)
			return false;
		
		int shift = subnodes.size() - DimToCap(outerring - 1);
		
		// Redraw this - it might be incorrect for the bigger ones
		Position p = CoordinateByRingAndShift (outerring * subdim, shift * (subdim+1));

		// Add it to the right place
		subnodes.put (p, mn);
		return true;
	}
	
	public Position CoordinateByRingAndShift (int ring, int shift)
	{
		Position p = new Position (0,0);
		
		// First go to the right ring
		p.y += ring;
		p.x -= ring/2; // Rounded down (look at the Fig 1)
		
		// Now move around the ring to find the right spot
		int [] xstages = new int [] {+1, +1,  0, -1, -1, 0, +1};
		int [] ystages = new int [] {0,  -1, -1,  0, +1, +1, 0};
		
		int stageid = 0;
		int dx = xstages[0];
		int dy = ystages[0];
		
		// Look at the Fig 2
		for (int i=-((ring-1)/2); i<shift-((ring-1)/2); i++)
		{
			if (i % ring == 0)
			{
				// Switch to the next stage
				stageid += 1;
				dx = xstages[stageid];
				dy = ystages[stageid];
			}
			
			p.x += dx;
			p.y += dy;
		}
		
		return p;
	}
	
	public Position PositionByCoordinate (int base, Position coord)
	{
		// Converts the position from the skewed Euclidean to Euclidean
		
		Position pos = new Position (0,0);
		
		pos.x += Math.round(coord.x * Math.sqrt(3.0) * base + coord.y * Math.sqrt(3.0) * base * Math.cos(Math.PI/3));
		pos.y += Math.round(coord.y * Math.sqrt(3.0) * Math.sin(Math.PI/3));
		
		return pos;
	}
	
	public int FindNodeDim ()
	{
		int trydim = dim-1;
		while (7 * DimToCap(trydim) > DimToCap(dim))
			trydim--;
		
		return trydim;
	}
	
	@Override
	public void UpdateWithData(String sip) {
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
