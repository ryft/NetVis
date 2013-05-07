package netvis.visualisations.maps;

import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.ValueAnimator;

public abstract class Map {

	ValueAnimator viewfieldanim;
	public double GetViewfield () {return viewfieldanim.toDouble();}
	ValueAnimator middlex;
	public double GetMX () {return middlex.toDouble();}
	ValueAnimator middley;
	public double GetMY () {return middley.toDouble();}
	
	// Basic size of the node
	int base = 400;
	int width;
	int height;
	
	public Map() {
		viewfieldanim = new ValueAnimator(5.0);
		middlex = new ValueAnimator(0.0);
		middley = new ValueAnimator(0.0);
	}
	

	public void ZoomIn() {
		double viewfield = viewfieldanim.getGoal();
		viewfieldanim.MoveTo(viewfield * 0.9, 100);
	}

	public void ZoomOut() {
		double viewfield = viewfieldanim.getGoal();
		viewfieldanim.MoveTo(viewfield * 1.1, 100);
	}


	public abstract Node FindClickedNode(int x, int y);
	

	public double ZoomOn() {
		double screenratio = (1.0 * width) / height;
		if (screenratio < Math.sqrt(3.0)) {
			return (base * Math.sqrt(3.0)) / width;
		}
		return (1.0 * base) / height;
	}

}
