package netvis.visualizations;

import java.util.List;

import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public abstract class AbstractVisualization implements Visualization, DataControllerListener{
	List<Packet> listOfPackets;
	OpenGLPanel joglPanel;
	final DataController dataController;
	boolean firstDraw;
	JPanel visControls;
	final VisControlsContainer visContainer;
	public AbstractVisualization(DataController dataController, OpenGLPanel joglPanel, VisControlsContainer visControlsContainer){
		dataController.addListener(this);
		listOfPackets = dataController.getPackets();
		this.dataController = dataController;
		this.joglPanel = joglPanel;
		this.visControls = new JPanel();
		this.visContainer = visControlsContainer;
	}
	
	@Override
	public void allDataChanged(List<Packet> allPackets) {
		joglPanel.redraw();
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		joglPanel.redraw();
	}

	public void activate(){
		joglPanel.setVis(this);
		visContainer.setVisControl(visControls);
		firstDraw = true; // Can use this to draw stuff only at the first render
		joglPanel.redraw();
	}
}
