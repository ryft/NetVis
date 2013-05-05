package netvis.visualisations;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JPanel;

import com.jogamp.opengl.util.FPSAnimator;

import netvis.data.DataController;
import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public abstract class Visualisation extends GLJPanel implements DataControllerListener,
		GLEventListener {

	private static final long serialVersionUID = 1L;
	final List<Packet> listOfPackets;
	List<Packet> newPackets;
	final OpenGLPanel joglPanel;
	final DataController dataController;

	FPSAnimator fpskeep;

	boolean allDataChanged;
	JPanel visControls;
	final VisControlsContainer visContainer;

	public Visualisation(DataController dataController, OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {

		super(CreateCapabilities());

		this.setPreferredSize(new Dimension(800, 500));
		this.setSize(800, 500);
		this.setFocusable(true);

		this.addGLEventListener(this);

		dataController.addListener(this);
		this.listOfPackets = dataController.getPackets();
		this.newPackets = new ArrayList<Packet>();
		this.dataController = dataController;
		this.joglPanel = joglPanel;
		this.visControls = this.createControls();
		this.visContainer = visControlsContainer;

		// Create the timer that will keep the FPS - don't start it yet
		this.fpskeep = new FPSAnimator(this, 600);
	}

	private static GLCapabilitiesImmutable CreateCapabilities() {
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
		//caps.setSampleBuffers (true);
		//caps.setNumSamples (2);

		return caps;
	}

	protected abstract JPanel createControls();

	@Override
	public void allDataChanged(List<Packet> allPackets, int updateInterval, int intervalsComplete) {
		if (joglPanel.getVis() == this) {
			this.newPackets = allPackets;
			this.allDataChanged = true;
			//this.display();
		}
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		if (joglPanel.getVis() == this) {
			this.newPackets = newPackets;
		}
	}

	public void activate() {
		joglPanel.setVis(this);
		visContainer.setVisControl(visControls);
		this.newPackets = this.listOfPackets;
		allDataChanged = true;

		// Start the FPSAnimator to keep the framerate around 120
		System.setProperty("sun.awt.noerasebackground", "true");

		// this.fpskeep.add(this);
		this.fpskeep.start();
	}

	public void deactivate() {
		this.fpskeep.stop();
	}

	protected void render() {
		this.display();
		allDataChanged = false;
	}

	public abstract String getName();

	public abstract String getDescription();

	public void everythingEnds() {
		this.destroy();
		this.fpskeep.stop();
	}

	public void setState(int insideI) {}
}
