package netvis.visualisations.comets;

import netvis.visualisations.gameengine.Node;

public class HeatNode extends Node {

	int warning;
	
	public int getWarning() {
		return warning;
	};

	double[] bgColor;

	public double[] getBGColor() {
		return bgColor;
	}

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
	
	public HeatNode (String tname, String nname)
	{
		name = nname;
		tex = tname;
		
		bgColor = new double[3];

		// Set the background color
		bgColor[0] = 0.5;
		bgColor[1] = 1.0;
		bgColor[2] = 0.7;
		warning = 0;
	}
	
	@Override
	public void UpdateWithData(String sip) {
		IncreaseWarning();
	}

	@Override
	public void UpdateAnimation(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public int Priority() {
		return warning;
	}
	

	public void IncreaseWarning() {
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
	}

	@Override
	public void DoubleClick() {
		// TODO Auto-generated method stub

	}

}
