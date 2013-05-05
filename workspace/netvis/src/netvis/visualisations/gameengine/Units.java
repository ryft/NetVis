package netvis.visualisations.gameengine;

public class Units {

	public static Position FindSpotAround (int size)
	{
		int outerring = 1;
		while (DimToCap(outerring) <= size)
			outerring += 1;
		
		int shift = size - DimToCap(outerring - 1);

		return new Position (outerring, shift);
	}
	
	public static Position ActuallRingAndShift (int dim, Position rs)
	{
		int ring  = rs.x;
		int shift = rs.y;

		ring  = (ring-1) * (2*dim - 1) + 1;
		shift *= (2*dim - 1);

		Position p = new Position (ring, shift);
		
		return p;
	}
	
	public static int DimToCap (int dim)
	{
		if (dim == 0) return 0;
		return 3*(dim*dim - dim)+1;
	}
	
	public static Position CoordinateByRingAndShift (int dim, int ring, int shift)
	{
		int comp = -1;
		if ((ring != 1) && (shift % (ring-1) != 0))
		{
			comp = shift / (ring-1);
		}

		Position rs = ActuallRingAndShift (dim, new Position(ring, shift));
		
		ring  = rs.x;
		shift = rs.y;
		
		Position p = new Position (0,0);
		
		// First go to the right ring
		p.y += (ring-1);
		p.x -= (ring-1)/2; // Rounded down (look at the Fig 1)
		
		// Now move around the ring to find the right spot
		int [] xstages = new int [] {+1, +1,  0, -1, -1, 0, +1, +1,  0}; // last three repeat
		int [] ystages = new int [] {0,  -1, -1,  0, +1, +1, 0, -1, -1};
		
		int [] xcompens = new int [] { 0, -1, -1,  0, +1, +1,  0, -1, -1};
		int [] ycompens = new int [] {-1,  0, +1, +1,  0, -1, -1,  0, +1};
		
		int stageid = 0;
		int dx = xstages[0];
		int dy = ystages[0];
		
		int delta = ((ring-1)/2);
		
		// This changes the adjacency style
		//shift += delta;
		
		// Look at the Fig 2
		for (int i=0; i < shift; i++)
		{
			if ((i-delta) % (ring-1) == 0)
			{
				// Switch to the next stage
				stageid += 1;
				dx = xstages[stageid];
				dy = ystages[stageid];
			}
			
			p.x += dx;
			p.y += dy;
		}
		
		// Apply compensation
		if (comp != -1)
		{
			p.x += (dim-1) * xcompens[comp];
			p.y += (dim-1) * ycompens[comp];
		};
			
		return p;
	}
	
	public static Position PositionByCoordinate (int base, Position coord)
	{
		// Converts the position from the skewed Euclidean to Euclidean
		
		Position pos = new Position (0,0);
		
		pos.x += Math.round(coord.x * Math.sqrt(3.0) * base + coord.y * Math.sqrt(3.0) * base * Math.cos(Math.PI/3));
		pos.y += Math.round(coord.y * Math.sqrt(3.0) * base * Math.sin(Math.PI/3));
		
		return pos;
	}
	
	public static Position MetaPositionByCoordinate (int dim, int base, Position pos)
	{
		return Units.PositionByCoordinate (base*(2*dim-1), pos);
	}
	
	public static Position CoordinateByPosition (int base, Position pos)
	{
		double r3 = Math.sqrt(3.0);
		Position p = new Position(0, 0);

		p.y = (int) Math.round (pos.y / (base * r3 * Math.sin(Math.PI/3)));
		p.x = (int) Math.round ((pos.x - p.y * base * r3 * Math.cos(Math.PI/3)) / (base * r3));

		return p;
	}
	
	public static Position MetaCoordinateByPosition (int dim, int base, Position pos)
	{
		return Units.CoordinateByPosition (base*(2*dim-1), pos);
	}
}
