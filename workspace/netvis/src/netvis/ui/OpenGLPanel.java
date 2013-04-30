package netvis.ui;

import javax.swing.JPanel;

import netvis.visualisations.Visualisation;

public class OpenGLPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Visualisation currentVis;

	public OpenGLPanel() {
		currentVis = null;
	}

	public void redraw() {
		currentVis.display();
	}

	public void setVis(Visualisation vis) {
		if (currentVis != null)
			this.remove(currentVis);
		currentVis = vis;
		this.add(vis);
		resizeVisualisation();
	}

	public Visualisation getVis() {
		return currentVis;
	}

	public void resizeVisualisation() {
		currentVis.setPreferredSize(getSize());
		currentVis.setSize(getSize());
	}
}
