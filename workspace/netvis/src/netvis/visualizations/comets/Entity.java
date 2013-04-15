package netvis.visualizations.comets;

import java.util.Random;

public class Entity {

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
	
	public Entity (int cx, int cy, int amp)
	{
		super();
		centerx = cx;
		centery = cy;
		
		// Really pick it randomly from -1 to 1
		Random rand = new Random();
		double randspeed = rand.nextDouble() * 20 + 15;
		double randsin = rand.nextDouble();
		double randcos = Math.sqrt(1.0 - randsin*randsin);

		tilt = rand.nextDouble() * Math.PI;
		
		posx = centerx; //(int) Math.floor(randcos * amp);
		posy = centery + amp; //+ (int) Math.floor(randsin * amp);
		
		velocityx = randspeed;
		velocityy = 0.0;
		
		kx = 1.0;
		ky = 1.0;
	}
	
	public void Step ()
	{
		posx += velocityx * 1.0;
		posy += velocityy * 1.0;
		
		velocityx -= (posx-centerx) * 0.004 * kx;
		velocityy -= (posy-centery) * 0.004 * ky;
	}
}
