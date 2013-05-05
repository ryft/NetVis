package netvis.visualisations.comets;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import netvis.data.DataController;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.visualisations.Visualisation;
import netvis.visualisations.gameengine.FramebufferPool;
import netvis.visualisations.gameengine.TextRendererPool;
import netvis.visualisations.gameengine.TexturePool;
import netvis.visualisations.gameengine.VertexBufferPool;
import netvis.visualisations.maps.MapActivity;
import netvis.visualisations.maps.MapController;

public class ActivityVisualisation extends Visualisation {

	int width;
	int height;

	Date oldTime;
	int frameNum;
	JLabel fps;

	Timer animator, cleaner;

	MapActivity map;

	HashMap<String, Candidate> candidates;

	public ActivityVisualisation(DataController dataController, OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {

		super(dataController, joglPanel, visControlsContainer);

		width = joglPanel.getWidth();
		height = joglPanel.getHeight();

		candidates = new HashMap<String, Candidate>();

		map = new MapActivity(width, height);
		new MapController (map, this);

		ActionListener animatum = new ActionListener() {
			long lasttime = (new Date()).getTime();

			public void actionPerformed(ActionEvent evnt) {
				// Animate the entities!
				long newtime = (new Date()).getTime();

				try {
					map.StepAnimation(newtime - lasttime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				lasttime = newtime;
			}
		};

		animator = new Timer(25, animatum);
		animator.start();

		ActionListener clearing = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evnt) {
				// If enough time has passed reset the candidates -- > TODO
				candidates.clear();
			}
		};

		cleaner = new Timer(2000, clearing);
		cleaner.start();



		// Add test nodes
		/*
		for (int i = 0; i < 0; i++) {
			currentMap.SuggestNode ("testk" + i, "test" + i);
		}
		
		for (int i = 0; i < 0; i++) {
			currentMap.SuggestNode ("testa" + i, "ServerA");
		}
		*/
		
		/*
		List<Packet> listOfPackets = new ArrayList<Packet> ();
		for (int i = 0; i < 48; i++) {
			currentMap.SuggestNode ("testb" + i, "ServerB", listOfPackets );
		}
		
		currentMap.SuggestNode("testbTOOMUCH", "ServerB", listOfPackets);
		*/
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void display(GLAutoDrawable drawable) {
		double viewfield = map.GetViewfield ();
		double mx = map.GetMX ();
		double my = map.GetMY();

		// Count the FPS
		if (oldTime == null) {
			frameNum = 0;
			oldTime = new Date();
		} else {
			Date now = new Date();
			long diff = now.getTime() - oldTime.getTime();
			if (diff > 2000) {
				double fpsnum = Math.round(10000.0 * frameNum / (diff)) / 10.0;
				fps.setText("FPS: " + fpsnum);

				oldTime = null;
			}
		}
		frameNum++;

		// if (true)
		// return;

		GL2 gl = drawable.getGL().getGL2();

		// Set the width and height to the actuall width and height in pixels,
		// (0, 0) is in the middle
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glOrtho(mx - this.width * viewfield / 2, mx + this.width
				* viewfield / 2, my - this.height * viewfield / 2,
				my + this.height * viewfield / 2, -1000, 2000);

		// Clear the board
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClearDepth(3000.0);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glShadeModel(GL2.GL_FLAT);

		// Depth things - probably unnecessary
		// gl.glEnable(GL.GL_DEPTH_TEST);
		// gl.glDepthFunc(GL2.GL_GEQUAL);

		// Use the typical blending options
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_BLEND);

		// glut.glutInitDisplayMode(GLUT. GLUT_DOUBLE | GLUT.GLUT_RGBA |
		// GLUT.GLUT_DEPTH | GLUT_MULTISAMPLE);

		// Try making rendering as nice as possible
		gl.glEnable(GL2.GL_MULTISAMPLE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);

		gl.glPushMatrix();
			//Painter.StressTestList (currentMap.base, gl);
			map.DrawEverything(gl);
		gl.glPopMatrix();
		
		// Probably unnecessary
		//this.swapBuffers();
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		// Dispatch the data to the specific points in the Map

		for (Packet i : newPackets) {
			Candidate dri = candidates.get(i.sip);
			if (dri == null) {
				// Create the candidate to be displayed
				dri = new Candidate(0, i.length, i.sip, i.dip);
				candidates.put(i.sip, dri);
			}	
			dri.RegisterPacket (i);
		}

		// Decide on which candidates should be displayed
		for (String ip : candidates.keySet()) {
			Candidate can = candidates.get(ip);

			// System.out.println("I'm considering IP: " + ip +
			// " which dataflow: " + can.datasize);
			if (can.datasize >= 2000) {
				// System.out.println("IP: " + ip + " which dataflow: " +
				// can.datasize + " added to the simulation");
				map.SuggestNode (can.sip, can.dip, can.GetWaitingPackets());
				can.ResetWaitingPackets();
			}
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClearDepth(0.0);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int wi, int he) {
		GL2 gl = drawable.getGL().getGL2();

		if (wi != width || he != height) {
			width = wi;
			height = he;
			this.setSize(wi, he);
			this.setPreferredSize(new Dimension(wi, he));
		}

		map.SetSize(width, height, gl);

		// Mark all the graphic card side object as broken
		TexturePool.DiscardTextures();
		TextRendererPool.Recreate();
		FramebufferPool.DiscardAll();
		VertexBufferPool.DiscardAll();
	}

	@Override
	protected JPanel createControls() {
		JPanel mypanel = new JPanel();
		fps = new JLabel("FPS : 0");

		mypanel.add(fps);
		return mypanel;
	}

	@Override
	public void everythingEnds() {
		super.everythingEnds();
	}

	@Override
	public void activate() {
		TexturePool.DiscardTextures();

		super.activate();
	}

	@Override
	public String getName() {
		return "Groups of activity";
	}

	@Override
	public String getDescription() {
		return getName() + "\n\n" + "A hexagonal grid displaying clients active in the network.\n" +
				"They are being grouped around the machine they send packets to.\n" +
				"Nodes 'heat up' when amount of data goes over specified threshold and 'heat down' in time.";
	}

}
