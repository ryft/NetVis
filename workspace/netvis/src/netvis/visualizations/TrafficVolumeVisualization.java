package netvis.visualizations;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import netvis.data.DataController;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.util.Utilities.MapComparator;

import com.jogamp.opengl.util.gl2.GLUT;

public class TrafficVolumeVisualization extends Visualization {

	private static final long serialVersionUID = 1L;

	private final GLUT glut = new GLUT();

	private final Queue<Map<String, Integer>> protocolCountMaps = new LinkedList<Map<String, Integer>>();
	private final Map<String, Integer> globalProtocolCount = new HashMap<String, Integer>();
	private final Map<String, GLColour3d> protocolColours = new HashMap<String, GLColour3d>();
	private int maxX = 64;
	private double maxY = 0;
	
	// Fields governing the update frequency of the graph
	private int updateInterval = 1;
	private int updateIteration = 0;

	class GLColour3d {
		double red = 0.0;
		double green = 0.0;
		double blue = 0.0;
		public GLColour3d(double red, double green, double blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
		public void setGLColour(GL2 gl) {
			gl.glColor3d(red, green, blue);
		}
	}

	public TrafficVolumeVisualization(DataController dc,
			final OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {
		super(dc, joglPanel, visControlsContainer);

	}

	protected JPanel createControls() {
		JPanel localControls = new JPanel();
		
		JLabel labelMaxX = new JLabel("Set x-axis resolution");
		final JTextField textMaxX = new JTextField();
		textMaxX.setText("64");
		
		JButton buttonMaxX = new JButton("Apply");
		buttonMaxX.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int newMaxX = Integer.valueOf(textMaxX.getText());
					maxX = newMaxX;
					textMaxX.setText(String.valueOf(maxX));
				} catch (NumberFormatException nfe) { }
			}	
		});
		localControls.setLayout(new BoxLayout(localControls, BoxLayout.Y_AXIS));
		localControls.add(labelMaxX);
		localControls.add(textMaxX);
		localControls.add(buttonMaxX);

		return localControls;
	}

	@Override
	// Change behaviour of packets arriving so we can process them.
	public void newPacketsArrived(List<Packet> newPackets) {
		if (joglPanel.getVis() == this) {
			
			if (allDataChanged) {
				globalProtocolCount.clear();
				protocolCountMaps.clear();
				protocolColours.clear();
			}
			
			this.newPackets = newPackets;
			maxY = Math.max(maxY, newPackets.size());
			processNewPackets(newPackets);
			
			if (updateIteration == 0)
				this.render();
			updateIteration = (updateIteration + 1) % updateInterval;
		}
	}

	protected void processNewPackets(List<Packet> newPackets) {

		// Get the number of packets using each protocol
		HashMap<String, Integer> protocolCount = new HashMap<String, Integer>();
		for (Packet p : newPackets) {
			protocolSeen(p.protocol);
			if (protocolCount.get(p.protocol) == null)
				protocolCount.put(p.protocol, 1);
			else
				protocolCount.put(p.protocol, protocolCount.get(p.protocol) + 1);
		}

		// Add it to the list of all such maps
		while (protocolCountMaps.size() >= maxX && protocolCountMaps.size() > 0)
			protocolCountMaps.remove();
		protocolCountMaps.add(protocolCount);
	}

	protected void protocolSeen(String protocol) {
		
		if (!protocolColours.containsKey(protocol)) {
			Random r = new Random();
			GLColour3d c = new GLColour3d(r.nextDouble(), r.nextDouble(), r.nextDouble());
			protocolColours.put(protocol, c);
		}
		
		if (!globalProtocolCount.containsKey(protocol))
			globalProtocolCount.put(protocol, 1);
		else
			globalProtocolCount.put(protocol, globalProtocolCount.get(protocol) + 1);
	}
	
	// Positions for the key labels in the protocol colour key (x100).
	protected Point[] keyPositions = {
			new Point(-93, -87),
			new Point(-93, -97),
			new Point(-50, -77),
			new Point(-50, -87),
			new Point(-50, -97),
			new Point(-10, -77),
			new Point(-10, -87),
			new Point(-10, -97),
			new Point(30, -77),
			new Point(30, -87),
			new Point(30, -97),
			new Point(70, -77),
			new Point(70, -87),
			new Point(70, -97)
	};

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		/*
		 * Draw the white background
		 */
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3d(0.95, 0.95, 0.95);
		gl.glVertex2d(-1, -0.7);
		gl.glVertex2d(-1, 0.9);
		gl.glVertex2d(1, 0.9);
		gl.glVertex2d(1, -0.7);
		gl.glEnd();

		double intervalWidth = 2.0 / protocolCountMaps.size();
		double xPos = -1.0;

		// For each vertical strip
		for (Map<String, Integer> currentPackets : protocolCountMaps) {

			double yPos = -0.7;

			// For each protocol to be drawn in the strip
			for (String protocol : currentPackets.keySet()) {

				GLColour3d c = protocolColours.get(protocol);
				double height = currentPackets.get(protocol) * 1.6 / maxY;

				gl.glBegin(GL2.GL_QUADS);
				gl.glColor3d(c.red, c.green, c.blue);
				gl.glVertex2d(xPos, yPos);
				gl.glVertex2d(xPos, yPos + height);
				gl.glVertex2d(xPos + intervalWidth, yPos + height);
				gl.glVertex2d(xPos + intervalWidth, yPos);
				gl.glEnd();

				yPos += height;
			}

			xPos += intervalWidth;
		}
		
		// Sort protocols by the most frequently-used first
		MapComparator<String> comparator = new MapComparator<String>(globalProtocolCount);
		TreeMap<String, Integer> sortedProtocolMap = new TreeMap<String, Integer>(comparator);
		sortedProtocolMap.putAll(globalProtocolCount);
		
		// Clear the key
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3d(0, 0, 0);
		gl.glVertex2d(-1, -1);
		gl.glVertex2d(-1, -0.7);
		gl.glVertex2d(1, -0.7);
		gl.glVertex2d(1, -1);
		gl.glEnd();
		
		// Draw key title
		gl.glColor3d(1, 1, 1);
		gl.glRasterPos2d(-0.95, -0.77);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Protocol Colour Key");
		
		// Generate key
		int i = 0;
		Iterator<String> protocolIterator =
				sortedProtocolMap.descendingKeySet().iterator();
		Stack<String> protocols = new Stack<String>();
		while (protocolIterator.hasNext()) protocols.push(protocolIterator.next());
		
		while (i < keyPositions.length && !protocols.isEmpty()) {
			String protocol = protocols.pop();
			
			// Set label and colour
			String label = protocol + " (" + globalProtocolCount.get(protocol) + ")";
			
			// Set colour and position
			GLColour3d colour = protocolColours.get(protocol);
			colour.setGLColour(gl);
			Point position = keyPositions[i];
			gl.glRasterPos2d(position.getX()/100, position.getY()/100);
			
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, label);
			i++;
		}

	}

	public String name() {
		return "Traffic Volume";
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor3d(0, 0, 0);
		gl.glRectd(-1, -1, 1, 1);
		gl.glColor3f(1, 1, 1);
		 
		gl.glRasterPos2d(-0.9, 0.93); // Set title position
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Traffic Volume by Protocol");
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub

	}
}
