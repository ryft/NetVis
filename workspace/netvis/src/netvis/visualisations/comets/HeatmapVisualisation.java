package netvis.visualisations.comets;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
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
import netvis.visualisations.comets.Map.NodeWithPosition;
import netvis.visualisations.gameengine.FramebufferPool;
import netvis.visualisations.gameengine.TextRendererPool;
import netvis.visualisations.gameengine.TexturePool;
import netvis.visualisations.gameengine.ValueAnimator;

public class HeatmapVisualisation extends Visualisation {

	int width;
	int height;

	Point oldpos = null;

	ValueAnimator middlex;
	ValueAnimator middley;
	ValueAnimator viewfieldanim;

	Date oldTime;
	int frameNum;
	JLabel fps;

	Timer animator, cleaner;

	Map currentMap;

	HashMap<String, Candidate> candidates;

	public HeatmapVisualisation(DataController dataController, OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {

		super(dataController, joglPanel, visControlsContainer);

		viewfieldanim = new ValueAnimator(5.0);
		middlex = new ValueAnimator(0.0);
		middley = new ValueAnimator(0.0);

		width = joglPanel.getWidth();
		height = joglPanel.getHeight();

		candidates = new HashMap<String, Candidate>();

		currentMap = new Map(width, height);

		ActionListener animatum = new ActionListener() {
			long lasttime = (new Date()).getTime();

			public void actionPerformed(ActionEvent evnt) {
				// Animate the entities!
				long newtime = (new Date()).getTime();

				try {
					currentMap.StepAnimation(newtime - lasttime);
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

		// Now add the keyboard listener which will be responsible for zooming
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_EQUALS) {
					ZoomIn();
				}
				;
				if (e.getKeyChar() == KeyEvent.VK_MINUS) {
					ZoomOut();
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		this.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					ZoomIn();
				}
				if (e.getWheelRotation() > 0) {
					ZoomOut();
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				double viewfield = viewfieldanim.toDouble();
				if (oldpos != null) {
					middlex.MoveTo(middlex.getGoal() - (e.getX() - oldpos.x) * viewfield, 0);
					middley.MoveTo(middley.getGoal() + (e.getY() - oldpos.y) * viewfield, 0);
					// middlex -= (e.getX()-oldpos.x)*viewfield;
					// middley += (e.getY()-oldpos.y)*viewfield;
				}
				;
				oldpos = e.getPoint();
			}
		});

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				double viewfield = viewfieldanim.toDouble();
				int x = (int) Math.round(middlex.toDouble() + (e.getX() - (width / 2)) * viewfield);
				int y = (int) Math.round(middley.toDouble() - (e.getY() - (height / 2)) * viewfield);

				NodeWithPosition n = currentMap.FindClickedNode(x, y);
				if (n != null) n.node.MouseClick(e);

				if (e.getClickCount() == 2) {
					// Sort the map
					currentMap.SortNodes();

					// Zoom on the selected node - such that it will fill the
					// screen
					double goal = currentMap.ZoomOn();
					viewfieldanim.MoveTo(goal, 1000);
				}

				if (n != null) {
					// Move to the selected node
					middlex.MoveTo(n.pos.x, 1000);
					middley.MoveTo(n.pos.y, 1000);
				}

				e.consume();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// If it is over some node - drag this node - TODO

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				oldpos = null;
			}

		});

		// Add test nodes
		for (int i = 0; i < 0; i++) {
			Packet pp = new Packet (0, 0, "testk" + i, "", 11, "test" + i, "", 0, "", 0, "");
			ArrayList<Packet> plist = new ArrayList<Packet> ();
			plist.add(pp);
			currentMap.SuggestNode (pp.sip, pp.dip, plist);
		}

	}

	private void ZoomIn() {
		double viewfield = viewfieldanim.getGoal();
		viewfieldanim.MoveTo(viewfield * 0.9, 100);
	}

	private void ZoomOut() {
		double viewfield = viewfieldanim.getGoal();
		viewfieldanim.MoveTo(viewfield * 1.1, 100);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void display(GLAutoDrawable drawable) {
		double viewfield = viewfieldanim.toDouble();

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

		gl.glOrtho(middlex.toDouble() - this.width * viewfield / 2, middlex.toDouble() + this.width
				* viewfield / 2, middley.toDouble() - this.height * viewfield / 2,
				middley.toDouble() + this.height * viewfield / 2, 1000, 2000);

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
		gl.glTranslated(0.0, 0.0, -1000.0);
		// Make the map draw all of the elements
		currentMap.DrawEverything(gl);
		gl.glPopMatrix();
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
				currentMap.SuggestNode(can.sip, can.dip, can.GetWaitingPackets());
				currentMap.SuggestNode(can.dip, can.sip, can.GetWaitingPackets());
				
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

		currentMap.SetSize(width, height, gl);

		// TexturePool.Rebind(gl);
		TexturePool.DiscardTextures();
		TextRendererPool.Recreate();
		// FramebufferPool.RegenerateAll(gl);
		FramebufferPool.DiscardAll();
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
		return "Heatmap of activity";
	}

	@Override
	public String getDescription() {
		return getName() + "\n\n" + "A hexagonal grid displaying clients active in the network.\n" +
				"Color indicated how much data is being transferred.\n" +
				"Nodes 'heat up' when amount of data goes over specified threshold and 'heat down' in time.";
	}

}