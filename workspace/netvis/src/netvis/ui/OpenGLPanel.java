package netvis.ui;

import javax.swing.JPanel;

import netvis.visualizations.Visualization;

public class OpenGLPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Visualization currentVis;
	public OpenGLPanel() {
		currentVis = null;
	}
	public void redraw(){
		currentVis.display();
	}
	public void setVis(Visualization vis){
		if (currentVis != null)
			this.remove(currentVis);
		currentVis = vis;
		this.add(vis);
	}
	public Visualization getVis(){
		return currentVis;
	}
}
