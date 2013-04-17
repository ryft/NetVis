package netvis.visualizations.comets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Comet {
	
	List<Position> tail ; public List<Position> getTail  () {return tail;}
	List<Position> trace; public List<Position> getTrace () {return trace;}
	boolean tracefinished = false;
	
	double posx; public int getx() {return (int) posx;};
	double posy; public int gety() {return (int) posy;};
	
	double velocityx;
	double velocityy;

	double colr;
	double colg;
	double colb;
	
	double tilt; public double gettilt() {return tilt;};
	
	public Comet (int amp, double tt)
	{
		super();
		
		tail = new ArrayList<Position> ();
		trace = new ArrayList<Position> ();
		
		Random rand = new Random();
		double randspeed = rand.nextDouble() * 10 + 10;

		tilt = tt;
		
		posx = (int) Math.floor(Math.sin(tilt) * amp);
		posy = (int) Math.floor(Math.cos(tilt) * amp);
		
		velocityx = Math.sin(tilt + Math.PI/2) * randspeed;
		velocityy = Math.cos(tilt + Math.PI/2) * randspeed;
	}
	
	public void Step ()
	{
		int x = (int) Math.floor(posx);
		int y = (int) Math.floor(posy);
		
		// Add an entry to keep track of the tail
		tail.add(new Position (x, y));
		// Keep only the last 20
		tail = tail.subList(Math.max(0, tail.size()-20), tail.size());
		
		
		if (!tracefinished || trace.size() < 200)
		{
			trace.add(new Position (x, y));
			
			// Do the angular sorting
			Collections.sort(trace, new Comparator<Position> () {
				@Override
				public int compare(Position o1, Position o2) {
					
					if (Angle(o1.x, o1.y) < Angle(o2.x, o2.y))
						return 1;
					else
						return -1;
				}
			});
			
			// Check whether the distances are small enough
			if (trace.size() >= 15)
			{
				tracefinished = true;
				Position oldp = trace.get(0);
				for (int i=1; i<trace.size(); i++)
				{
					Position newp = trace.get(i);
					if (Math.abs (oldp.x - newp.x) > 10 || Math.abs (oldp.y - newp.y) > 10)
					{
						// Fail - the trace is not finished
						tracefinished = false;
						break;
					}
					oldp = newp;
				}
				
				// Check the edge ones
				Position first = trace.get(0);
				Position last  = trace.get(trace.size()-1);
				if (Math.abs (first.x - last.x) > 10 || Math.abs (first.y - last.y) > 10)
					tracefinished = false;
			}
		};
		
		// Update the position
		posx += velocityx * 1.0;
		posy += velocityy * 1.0;
		
		ForcesAct();
	}
	
	public double Angle (double posx, double posy)
	{
		return Math.atan2(posy, posx);
	}
	
	public void ForcesAct()
	{
		// Harmonic oscillator model
		velocityx -= (posx) * 0.004;
		velocityy -= (posy) * 0.004;
		
		// Gravitational model - maybe a better choice? - There is a lot of escaping though
		// Force is proportional to the 1/distance^2
		//double distance = Math.sqrt(Math.pow(posx, 2) + Math.pow(posy, 2));	
		//velocityx -= posx * 10000 / Math.pow(distance, 3); // * kx;
		//velocityy -= posy * 10000 / Math.pow(distance, 3);
	}
	
	public void SimulateTrace ()
	{

	}
}
