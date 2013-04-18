package netvis.visualizations.comets;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import netvis.visualizations.Visualization;

public class ActivityVisualisation extends Visualization {

	int width;
	int height;
	
	Point oldpos = null;
	Position middle = new Position(0,0);
	double viewfield = 5.0;
	
	Date oldTime;
	int frameNum;
	JLabel fps;
	
	Timer animator, cleaner;
	
	Map currentMap;
	
	HashMap<String, Candidate> candidates;
	
	public class Candidate {
		
		// How close the client is to the internal network 0-closest 10-furthest
		public int proximity;
		
		// How much data it send throughout the last interval
		public int datasize;
		
		// Source and destination
		public String sip, dip;
		
		public Candidate (int prox, int dat, String s, String d)
		{
			proximity = prox;
			datasize = dat;
			
			sip = s;
			dip = d;
		}
	}
	
	public ActivityVisualisation(DataController dataController, OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		
		super(dataController, joglPanel, visControlsContainer);

		
		width = joglPanel.getWidth();
		height = joglPanel.getHeight();
		
		candidates = new HashMap<String, Candidate> ();
		
		currentMap = new Map (width, height);
		
		ActionListener animatum = new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent evnt) {
				// Animate the entities!
				currentMap.StepNodes();
			}
		};
		
		animator = new Timer (20, animatum);
		animator.start();
		
		ActionListener clearing = new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent evnt) {
				// If enough time has passed reset the candidates -- > TODO
				candidates.clear();
			}
		};
		
		cleaner = new Timer (2000, clearing);
		cleaner.start();
		
		// Now add the keyboard listener which will be responsible for zoomming
		this.addKeyListener(new KeyListener () {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_EQUALS)
				{
					ZoomIn();
				};
				if (e.getKeyChar() == KeyEvent.VK_MINUS)
				{
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
		
		this.addMouseWheelListener(new MouseWheelListener () {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0)
				{
					ZoomIn();
				}
				if (e.getWheelRotation() > 0)
				{
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
				if (oldpos != null)
				{
					middle.x -= (e.getX()-oldpos.x)*viewfield;
					middle.y += (e.getY()-oldpos.y)*viewfield;
				};
				oldpos = e.getPoint();
			}
		});
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int x = (int) Math.round (middle.x + (e.getX()-(width/2))*viewfield);
				int y = (int) Math.round (middle.y - (e.getY()-(height/2))*viewfield);
				Node n = currentMap.FindClickedNode(x, y);
			
				if (n != null)
				{
					n.toggleSelected();
					
					if (n.getSelected())
					{
						// Zoom on the selected node
						middle = n.getCenter();
						viewfield = 1.0;
					}
				}
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
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				oldpos = null;
			}
			
		});
		
		// Add test nodes
		for (int i=0; i<20; i++)
		{
			currentMap.SuggestNode ("testk" + i, "test" + i);
		}
		
	}

	private void ZoomIn() {
		viewfield *= 0.9;
	}
	private void ZoomOut() {
		viewfield *= 1.1;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void display(GLAutoDrawable drawable) {
		
		// Count the FPS
		if (oldTime == null)
		{
			frameNum = 0;
			oldTime = new Date();
		} else
		{
			Date now = new Date();
			long diff = now.getTime() - oldTime.getTime();
			if (diff > 2000)
			{
				double fpsnum = Math.round (10000.0 * frameNum / (diff)) / 10.0;
				fps.setText("FPS: " + fpsnum);
				
				oldTime = null;
			}
		}
		frameNum++;
		
		GL2 gl = drawable.getGL().getGL2();

		// Set the width and height to the actuall width and height in pixels, (0, 0) is in the middle
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glOrtho(middle.x-this.width*viewfield/2, middle.x+this.width*viewfield/2, middle.y-this.height*viewfield/2, middle.y+this.height*viewfield/2, -10, 10);
		
		// Clear the board
		gl.glClearColor (1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClearDepth (0.0);
		gl.glClear (GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glShadeModel(GL2.GL_FLAT);
		
		// Depth things - probably unnecessary
		//gl.glEnable(GL.GL_DEPTH_TEST);
		//gl.glDepthFunc(GL2.GL_ALWAYS);

		// Use the typical blending options
		gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable (GL.GL_BLEND);
		
		//glut.glutInitDisplayMode(GLUT. GLUT_DOUBLE | GLUT.GLUT_RGBA | GLUT.GLUT_DEPTH | GLUT_MULTISAMPLE);

		// Try making rendering as nice as possible
		gl.glEnable(GL2.GL_MULTISAMPLE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
	    gl.glEnable (GL2.GL_LINE_SMOOTH);
	    gl.glHint (GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST );
	    gl.glHint (GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST );
		
	    // Make the map draw all of the elements
		currentMap.DrawEverything(gl);
		
		gl.glFlush();
	}
	
	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		// Dispatch the data to the specific points in the Map
		for (Packet i : newPackets)
		{
			Candidate dri = candidates.get(i.sip);
			if (dri == null)
			{
				// Create the candidate to be displayed
				dri = new Candidate (0, i.length, i.sip, i.dip);
				candidates.put (i.sip, dri);
			} else
			{
				dri.datasize += i.length;
			}
		}
		
		// Decide on which candidates should be displayed
		for (String ip : candidates.keySet())
		{
			Candidate can = candidates.get(ip);
			
			//System.out.println("I'm considering IP: " + ip + " which dataflow: " + can.datasize);
			if (can.datasize >= 2000)
			{
				//System.out.println("IP: " + ip + " which dataflow: " + can.datasize + " added to the simulation");
				currentMap.SuggestNode (can.sip, can.dip);
				currentMap.SortNodes();
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
		
		gl.glClearColor (1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClearDepth (0.0);
		gl.glClear (GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int wi, int he) {		
		GL2 gl = drawable.getGL().getGL2();

		if (wi != width || he != height)
		{
			width = wi;
			height = he;
			this.setSize(wi, he);
			this.setPreferredSize(new Dimension(wi,he));
			
			currentMap.SetSize (width, height, gl);
		}
		
		TexturePool.Rebind(gl);
	}

	@Override
	protected JPanel createControls() {
		JPanel mypanel = new JPanel();
		fps = new JLabel ("FPS : 0");
		
		mypanel.add(fps);
		return mypanel;
	}

	@Override
	public String name() {
		return "Heatmap of activity";
	}

	@Override
	public void everythingEnds() {
		super.everythingEnds();
		System.out.println("The only visualization stops, you fools...");		
	}
	
	@Override
	public void activate ()
	{
		TexturePool.DiscardTextures();
		
		super.activate();
	}

}