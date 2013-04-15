package netvis.visualizations.comets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Entity {
	
	public class Position
	{
		public Position(int posx, int posy) {
			x = posx;
			y = posy;
		}
		public int x;
		public int y;
	}
	
	List<Position> oldPositions; public List<Position> getPositions () {return oldPositions;}

	int centerx;
	int centery;
	
	double posx; public int getx() {return (int) posx;};
	double posy; public int gety() {return (int) posy;};
	
	double velocityx;
	double velocityy;
	
	double kx;
	double ky;

	double colr;
	double colg;
	double colb;
	
	double tilt; public double gettilt() {return tilt;};
	
	public Entity (int cx, int cy, int amp, double tt)
	{
		super();
		
		oldPositions = new ArrayList<Position> ();
		
		centerx = cx;
		centery = cy;
		
		// Really pick it randomly from -1 to 1
		Random rand = new Random();
		double randspeed = rand.nextDouble() * 10 + 10;
		double randsin = rand.nextDouble();
		double randcos = Math.sqrt(1.0 - randsin*randsin);

		tilt = tt;
		
		posx = centerx + (int) Math.floor(Math.sin(tilt) * amp);
		posy = centery + (int) Math.floor(Math.cos(tilt) * amp);
		
		velocityx = Math.cos(tilt) * randspeed;
		velocityy = Math.sin(tilt) * randspeed;
		
		kx = 1.0;
		ky = 1.0;
	}
	
	public void Step ()
	{
		oldPositions.add(new Position ((int) Math.floor(posx), (int) Math.floor(posy)));
		
		// Keep only the last 20
		oldPositions = oldPositions.subList(Math.max(0, oldPositions.size()-20), oldPositions.size());
		
		posx += velocityx * 1.0;
		posy += velocityy * 1.0;
		
		velocityx -= (posx-centerx) * 0.004 * kx;
		velocityy -= (posy-centery) * 0.004 * ky;
	}
}
