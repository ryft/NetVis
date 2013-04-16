package netvis.visualizations.comets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Comet {
	
	List<Position> tail ; public List<Position> getTail  () {return tail;}
	List<Position> trace; public List<Position> getTrace () {return trace;}
	boolean tracefinished = false;

	int centerx;
	int centery;
	
	double posx; public int getx() {return (int) posx;};
	double posy; public int gety() {return (int) posy;};
	
	double velocityx;
	double velocityy;

	double colr;
	double colg;
	double colb;
	
	double tilt; public double gettilt() {return tilt;};
	
	public Comet (int cx, int cy, int amp, double tt)
	{
		super();
		
		tail = new ArrayList<Position> ();
		trace = new ArrayList<Position> ();
		
		centerx = cx;
		centery = cy;
		
		Random rand = new Random();
		double randspeed = rand.nextDouble() * 10 + 10;
		double randsin = rand.nextDouble();
		double randcos = Math.sqrt(1.0 - randsin*randsin);

		tilt = tt;
		
		posx = centerx + (int) Math.floor(Math.sin(tilt) * amp);
		posy = centery + (int) Math.floor(Math.cos(tilt) * amp);
		
		velocityx = Math.cos(tilt) * randspeed;
		velocityy = Math.sin(tilt) * randspeed;
	}
	
	public void Step ()
	{
		int x = (int) Math.floor(posx);
		int y = (int) Math.floor(posy);
		
		// Add an entry to keep track of the tail
		tail.add(new Position (x, y));
		// Keep only the last 20
		tail = tail.subList(Math.max(0, tail.size()-20), tail.size());
		
		
		if (trace.size() < 60 || !tracefinished)
		{
			trace.add(new Position (x, y));
			tracefinished =  Math.abs (trace.get(0).x - x) < 10 && Math.abs (trace.get(0).y - y) < 10;
		}
		
		// Update the position
		posx += velocityx * 1.0;
		posy += velocityy * 1.0;
		
		ForcesAct();
	}
	
	public void ForcesAct()
	{
		// Force is proportional to the 1/distance^2
		double distance = Math.sqrt(Math.pow(posx-centerx, 2) + Math.pow(posy-centery, 2));	
		
		velocityx -= (posx-centerx) * 0.004;
		velocityy -= (posy-centery) * 0.004;
		
		//velocityx -= (posx-centerx) * 10000 / Math.pow(distance, 3); // * kx;
		//velocityy -= (posy-centery) * 10000 / Math.pow(distance, 3);
	}
	
	public void SimulateTrace ()
	{
		int x = (int) Math.floor(posx);
		int y = (int) Math.floor(posy);

		if (trace.size() < 60 || !tracefinished)
		{
			trace.add(new Position (x, y));
			tracefinished =  Math.abs (trace.get(0).x - x) < 10 && Math.abs (trace.get(0).y - y) < 10;
		}
	}
}
