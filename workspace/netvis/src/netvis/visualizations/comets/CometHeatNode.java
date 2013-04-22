package netvis.visualizations.comets;

import java.util.Collection;
import java.util.HashMap;

import javax.media.opengl.GL2;

import netvis.visualizations.gameengine.Node;
import netvis.visualizations.gameengine.NodePainter;
import netvis.visualizations.gameengine.Texture;

public class CometHeatNode extends Node {

	HashMap<String, Comet> entities; public Collection<Comet> getEntities() {return entities.values();}
	
	int warning; public int getWarning() {return warning;};
	double [] bgColor;
	public double[] getBGColor() {return bgColor;}
	public void     setBGColor(double r, double g, double b) {bgColor[0] = r; bgColor[1] = g; bgColor[2] = b;}
	
	boolean selected; public boolean getSelected() {return selected;};

	
	boolean changed = true;
	public boolean IsChanged ()	{return changed;}
	public void    Draw () {changed = false;}
	
	Texture tex; public Texture getTexture () {return tex;}
	
	String name; public String getName() {return name;};
	
	public CometHeatNode (Texture tt, String nn)
	{
		super ();
		
		selected = false;
		
		name = nn;
		tex = tt;
	
		entities = new HashMap<String, Comet>();
		bgColor = new double[3];
		
		// Set the background color
		bgColor[0] = 0.5;
		bgColor[1] = 1.0;
		bgColor[2] = 0.7;
		warning = 0;
	}
	
	public void Draw (int base, NodePainter painter, GL2 gl)
	{
		painter.DrawNode(base, this, gl);
	}
	
	public void UpdateWithData (String sip)
	{
		// Make their tilts nicely shifted
		AddSatelite (sip, 100, entities.size() * Math.PI/10);
	}
	
	public void UpdateAnimation (long time)
	{
		StepSatelites (time);
	}

	public int Priority ()
	{
		return warning;
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

	@Override
	public void DoubleClick() {
		// TODO Auto-generated method stub
		
	}

}