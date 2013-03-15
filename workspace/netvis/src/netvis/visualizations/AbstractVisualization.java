package netvis.visualizations;

import java.util.List;

import netvis.data.DataController;
import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;

public abstract class AbstractVisualization implements Visualization, DataControllerListener{
	List<Packet> listOfPackets;
	OpenGLPanel joglPanel;
	public AbstractVisualization(DataController dataController, OpenGLPanel joglPanel){
		dataController.addListener(this);
		listOfPackets = dataController.getPackets();
		this.joglPanel = joglPanel;
	}
	
	@Override
	public void allDataChanged(List<Packet> allPackets) {
		
	}



	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		joglPanel.redraw();
	}

	public void activate(){
		joglPanel.setVis(this);
	}
}
