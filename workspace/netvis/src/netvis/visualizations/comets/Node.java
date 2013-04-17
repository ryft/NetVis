package netvis.visualizations.comets;

import java.util.Collection;
import java.util.HashMap;

public class Node {

	HashMap<String, Comet> entities; public Collection<Comet> getEntities() {return entities.values();}
	
	double x; public int getx() {return (int) x;};
	double y; public int gety() {return (int) y;};
	
	double scale = 1.0;
	public double getScale () {return scale;}
	public void   setScale (double s) {scale = s;}
	
	Texture tex; public Texture getTexture () {return tex;}
	
	String name; public String getName() {return name;};
	
	public Node (int posx, int posy, Texture tt, String nn)
	{
		name = nn;
		tex = tt;
		x = posx;
		y = posy;
	
		entities = new HashMap<String, Comet>();
	}

	public void AddSatelite(String sip, int amp, double tilt)
	{
		// Look for the entity with the same name
		Comet find = entities.get(sip);
		
		if (find == null)
		{
			entities.put (sip, new Comet ((int) Math.round(x), (int) Math.round(y), amp, tilt));
		}
	}
	
	public void RemoveSatelite (String sip)
	{
		// Look for the entity 
		Comet find = entities.get(sip);
		
		if (find != null)
		{
			
		}
	}
	
	public void StepSatelites () 
	{
		for (Comet i : entities.values())
			i.Step();
	}
}