package netvis.visualisations.comets;

import java.awt.event.MouseEvent;

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

	String name;

	public String getName() {
		return name;
	};
	
	boolean selected;

	public boolean getSelected() {
		return selected;
	};
	
	long cumul;
	
	public HeatNode (String tname, String nname)
	{
		cumul = 0;
		name = nname;
		tex = tname;
		
		bgColor = new double[3];
		opacity = 1.0;

		// Set the background color
		bgColor[0] = 0.5;
		bgColor[1] = 1.0;
		bgColor[2] = 0.7;
		warning = 0;
		
		selected = false;
	}
	
	@Override
	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}
	
	@Override
	public void UpdateWithData(Packet pp) {
		IncreaseWarning();
	}

	@Override
	public void UpdateAnimation(long time) {
		cumul += time;
		if (cumul >= 5000)
		{
			// Every second decrease the warning of the node
			if (warning > 0)
				DecreaseWarning();
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
		
		cumul = 0;

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
		
		cumul = 0;
	}

	@Override
	public void MouseClick (MouseEvent e) {
		if (e.getClickCount() == 1) {
			selected = !selected;
		}
	}

}
