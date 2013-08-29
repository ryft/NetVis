package netvis.visualisations.comets;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.media.opengl.GL2;

import netvis.data.model.Packet;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;

public class HeatNode extends Node {

	int warning;
	
	public int getWarning() {
		return warning;
	};

	double[] bgColor;
	double opacity;

	public double[] getBGColor() {
		return bgColor;
	}
	
	public double getOpacity () {return opacity;}

	public void setBGColor(double r, double g, double b) {
		bgColor[0] = r;
		bgColor[1] = g;
		bgColor[2] = b;
	}
	
	String tex;
	
	public String getTexture() {
		return tex;
	}
	
	boolean selected;

	public boolean getSelected() {
		return selected;
	};
	
	long cumultime;
	
	// How much data was transferred
	long data;
	// And with what protocols
	HashMap<String, Long> protocollengths;
	long maxVal = 0;
	public String maxProto = "";
	
	public HeatNode (String tname, String nname)
	{
		super (nname);
		
		// Cumulating time
		cumultime = 0;
		
		// How much data was transferred
		data = 0;

		tex = tname;
		
		bgColor = new double[3];
		opacity = 1.0;

		// Set the background color
		bgColor[0] = 0.5;
		bgColor[1] = 1.0;
		bgColor[2] = 0.7;
		warning = 0;
		
		selected = false;
		
		protocollengths = new HashMap<String, Long> ();
	}
	
	@Override
	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}
	
	@Override
	public void UpdateWithData(Packet pp) {
		data += pp.length;
		while (warning < data/1024)
			IncreaseWarning();
		
		Long val = protocollengths.get (pp.protocol);
		if (val == null)
		{
			val = new Long(0);
			protocollengths.put(pp.protocol, val);
		};
		
		val += pp.length;
		protocollengths.put(pp.protocol, val);
		
		// Compare it with the current most common protocol
		if (val > maxVal)
		{
			maxVal = val;
			maxProto = pp.protocol;
		};
	}

	@Override
	public void UpdateAnimation(long time) {
		cumultime += time;
		if (cumultime >= 5000)
		{
			// Every second decrease the warning of the node
			if (warning > 0)
				DecreaseWarning();
			else if (warning == 0)
			{
				// Delete the node by calling the parent method
				//this.GetParent().DetachNode (this);
			}
			
			// Reset the counter
			cumultime = 0;
		}

	}

	@Override
	public int Priority() {
		return warning;
	}
	

	public void IncreaseWarning() {
		if (warning == 0)
		{
			// Make it colorful again
			bgColor[0] = 0.5;
			bgColor[1] = 1.0;
			bgColor[2] = 0.7;
			
			opacity = 1.0;
		}
	
		warning += 1;
		bgColor[0] *= 1.3;
		bgColor[1] *= 0.9;
		bgColor[2] *= 0.9;
	}

	public void DecreaseWarning() {
		warning -= 1;
		bgColor[0] /= 1.3;
		bgColor[1] /= 0.9;
		bgColor[2] /= 0.9;
		
		if (warning == 0)
		{
			// Make it grey
			bgColor[0] = 0.7;
			bgColor[1] = 0.7;
			bgColor[2] = 0.7;
			
			opacity = 0.5;
		}
	}

	@Override
	public void MouseClick (MouseEvent e) {
		if (e.getClickCount() == 1) {
			selected = !selected;
		}
	}

}
