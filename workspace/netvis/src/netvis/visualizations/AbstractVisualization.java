package netvis.visualizations;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public abstract class AbstractVisualization implements Visualization, DataControllerListener{
	final List<Packet> listOfPackets;
	List<Packet> newPackets;
	final OpenGLPanel joglPanel;
	final DataController dataController;
	boolean firstDraw, allDataChanged;
	JPanel visControls;
	final VisControlsContainer visContainer;
	public AbstractVisualization(DataController dataController, OpenGLPanel joglPanel, VisControlsContainer visControlsContainer){
		dataController.addListener(this);
		listOfPackets = dataController.getPackets();
		this.newPackets = new ArrayList<Packet>();
		this.dataController = dataController;
		this.joglPanel = joglPanel;
		this.visControls = new JPanel();
		this.visContainer = visControlsContainer;
	}
	
	@Override
	public void allDataChanged(List<Packet> allPackets) {
		this.newPackets = allPackets;
		
		this.allDataChanged = true;
		joglPanel.redraw();

	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		this.newPackets = newPackets;
		joglPanel.redraw();
	}

	public void activate(){
		joglPanel.setVis(this);
		visContainer.setVisControl(visControls);
		this.newPackets = this.listOfPackets;
		allDataChanged = true;
		firstDraw = true; // Can use this to draw stuff only at the first render
		joglPanel.redraw();
	}
	
	public void renderAbstract(GLAutoDrawable drawable){
		this.render(drawable);
		allDataChanged = false;
		firstDraw = false;
	}
}
