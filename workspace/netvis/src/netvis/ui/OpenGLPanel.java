package netvis.ui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import netvis.visualizations.Visualization;

public class OpenGLPanel extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 1L;
	private Visualization currentVis;
	public OpenGLPanel() {
		currentVis = null;
		addComponentListener(this);
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
	
	@Override
	public void componentHidden(ComponentEvent arg0) {
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		currentVis.setSize(getSize());
		setVis(currentVis);
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
	}
}
