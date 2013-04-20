package netvis.visualizations.comets;

import java.util.Collection;
import java.util.HashMap;

import netvis.visualizations.gameengine.NodePainter;
import netvis.visualizations.gameengine.Position;
import netvis.visualizations.gameengine.Texture;
import netvis.visualizations.gameengine.ValueAnimator;

public class Node {

	HashMap<String, Comet> entities; public Collection<Comet> getEntities() {return entities.values();}
	
	Position center; public Position getCenter() {return center;}; public void setCenter(Position nc) {center = nc;};
	
	double scale = 1.0;
	public double getScale () {return scale;}
	public void   setScale (double s) {scale = s;}
	
	ValueAnimator rotation;
	public double getRotation () {return rotation.toDouble();};
	
	int warning; public int getWarning() {return warning;};
	double [] bgColor;
	public double[] getBGColor() {return bgColor;}
	public void     setBGColor(double r, double g, double b) {bgColor[0] = r; bgColor[1] = g; bgColor[2] = b;}
	
	boolean selected; public boolean getSelected() {return selected;};
	public void toggleSelected() {
		selected = !selected;
		if (selected)
			rotation.MoveTo (rotation.getGoal() + 180.0, 1000);
		else
			rotation.MoveTo (rotation.getGoal() - 180.0, 1000);
	};
	
	Texture tex; public Texture getTexture () {return tex;}
	
	String name; public String getName() {return name;};
	
	public Node (int posx, int posy, Texture tt, String nn)
	{
		selected = false;
		
		name = nn;
		tex = tt;
		center = new Position (posx, posy);
		rotation = new ValueAnimator (0.0);
	
		entities = new HashMap<String, Comet>();
		bgColor = new double[3];
		
		// Set the background color
		bgColor[0] = 0.5;
		bgColor[1] = 1.0;
		bgColor[2] = 0.7;
		warning = 0;
	}
	
	public void IncreaseWarning ()
	{
		warning += 1;
		bgColor[0] *= 1.3;
		bgColor[1] *= 0.9;
		bgColor[2] *= 0.9;
		
		//rotation.MoveTo (rotation.getGoal() + 360.0, 1000);
	}
	
	public void DecreaseWarning ()
	{
		warning -= 1;
		bgColor[0] /= 1.3;
		bgColor[1] /= 0.9;
		bgColor[2] /= 0.9;
		
		//rotation.MoveTo (rotation.getGoal() - 360.0, 1000);
	}

	public void AddSatelite(String sip, int amp, double tilt)
	{
		// Look for the entity with the same name
		Comet find = entities.get(sip);
		
		if (find == null)
		{
			entities.put (sip, new Comet (amp, tilt));
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
	
	public void StepSatelites (long time) 
	{
		for (Comet i : entities.values())
			i.Step(time);
	}
	
	
	public NodePainter GetFrontPainter ()
	{
		return new CometPainter();
	}
	
	public NodePainter GetBackPainter ()
	{
		return new GraphPainter();
	}

}