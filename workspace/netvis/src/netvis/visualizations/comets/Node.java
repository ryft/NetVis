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
	
	double [] bgColor;
	public double[] getBGColor() {return bgColor;}
	public void     setBGColor(double r, double g, double b) {bgColor[0] = r; bgColor[1] = g; bgColor[2] = b;}
	
	Texture tex; public Texture getTexture () {return tex;}
	
	String name; public String getName() {return name;};
	
	public Node (int posx, int posy, Texture tt, String nn)
	{
		name = nn;
		tex = tt;
		x = posx;
		y = posy;
	
		entities = new HashMap<String, Comet>();
		bgColor = new double[3];
		
		// Set the background color
		bgColor[0] = 0.5;
		bgColor[1] = 1.0;
		bgColor[2] = 0.7;
	}
	
	public void IncreaseWarning ()
	{
		bgColor[0] *= 1.3;
		bgColor[1] *= 0.9;
		bgColor[2] *= 0.9;
	}
	
	public void DecreaseWarning ()
	{
		bgColor[0] /= 1.3;
		bgColor[1] /= 0.9;
		bgColor[2] /= 0.9;
	}

	public void AddSatelite(String sip, int amp, double tilt)
	{
		// Look for the entity with the same name
		Comet find = entities.get(sip);
		
		if (find == null)
		{
			entities.put (sip, new Comet ((int) Math.round(x), (int) Math.round(y), amp, tilt));
			IncreaseWarning();
		}
	}
	
	public void RemoveSatelite (String sip)
	{
		// Look for the entity 
		Comet find = entities.get(sip);
		
		if (find != null)
		{
			entities.remove(sip);
			DecreaseWarning();
		}
	}
	
	public void StepSatelites () 
	{
		for (Comet i : entities.values())
			i.Step();
	}
}