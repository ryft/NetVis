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
	
	public static int DimToCap (int dim)
	{
		if (dim == 0) return 0;
		return 3*(dim*dim - dim)+1;
	}
	
	public static Position CoordinateByRingAndShift (int dim, int ring, int shift)
	{
		// Rings are indexed from 1
		ring -= 1;
		
		// Adjust the jump size by the dimension
		ring  *= (2*dim - 1);
		shift *= (2*dim - 1);
		
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
	
	public static Position PositionByCoordinate (int base, Position coord)
	{
		// Converts the position from the skewed Euclidean to Euclidean
		
		Position pos = new Position (0,0);
		
		pos.x += Math.round(coord.x * Math.sqrt(3.0) * base + coord.y * Math.sqrt(3.0) * base * Math.cos(Math.PI/3));
		pos.y += Math.round(coord.y * Math.sqrt(3.0) * base * Math.sin(Math.PI/3));
		
		return pos;
	}
	
	public static Position CoordinateByPosition (int base, Position pos)
	{
		double r3 = Math.sqrt(3.0);
		Position p = new Position(0, 0);

		p.y = (int) Math.round (pos.y / (base * r3 * Math.sin(Math.PI/3)));
		p.x = (int) Math.round ((pos.x - p.y * base * r3 * Math.cos(Math.PI/3)) / (base * r3));

		return p;
	}
}
