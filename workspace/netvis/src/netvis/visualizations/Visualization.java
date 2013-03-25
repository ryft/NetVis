package netvis.visualizations;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public abstract class Visualization extends GLCanvas implements DataControllerListener, GLEventListener{

	private static final long serialVersionUID = 1L;
	final List<Packet> listOfPackets;
	List<Packet> newPackets;
	final OpenGLPanel joglPanel;
	final DataController dataController;

	boolean allDataChanged;
	JPanel visControls;
	final VisControlsContainer visContainer;
	public Visualization(DataController dataController, OpenGLPanel joglPanel, VisControlsContainer visControlsContainer){

		super(new GLCapabilities(GLProfile.getDefault()));
		this.setSize(800, 500);

		this.addGLEventListener(this);
		
		dataController.addListener(this);
		this.listOfPackets = dataController.getPackets();
		this.newPackets = new ArrayList<Packet>();
		this.dataController = dataController;
		this.joglPanel = joglPanel;
		this.visControls = this.createControls();
		this.visContainer = visControlsContainer;
	}
	
	protected abstract JPanel createControls();
	
	@Override
	public void allDataChanged(List<Packet> allPackets, int updateInterval, int intervalsComplete) {
		if (joglPanel.getVis() == this){
			this.newPackets = allPackets;
			this.allDataChanged = true;
			this.display();
		}
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		if (joglPanel.getVis() == this){
			this.newPackets = newPackets;
			this.render();
		}
	}

	public void activate(){
		joglPanel.setVis(this);
		visContainer.setVisControl(visControls);
		this.newPackets = this.listOfPackets;
		allDataChanged = true;
		this.render();
	}
	
	protected void render(){
		this.display();
		allDataChanged = false;
	}
	
	 public abstract String name();
}
