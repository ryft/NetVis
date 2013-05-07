package netvis.visualisations;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import netvis.data.DataController;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.util.ColourPalette;
import netvis.util.Utilities.MapComparator;

import com.jogamp.opengl.util.gl2.GLUT;

public class TrafficVolumeVisualisation extends Visualisation {

	private static final long serialVersionUID = 1L;

	// GLUT object for drawing
	private final GLUT glut = new GLUT();

	// Fields holding packet data
	private final Map<Integer, Map<String, Integer>> protocolCountMaps = new HashMap<Integer, Map<String, Integer>>();
	private final Map<String, Integer> globalProtocolCount = new HashMap<String, Integer>();
	private final Map<String, Color> protocolColours = new HashMap<String, Color>();

	// The colour palette
	protected ColourPalette colourPalette = new ColourPalette(ColourPalette.SCHEME_QUALITATIVE);

	// Fields governing the appearance of the graph
	private int maxTime = 0;
	private int maxX = 128;
	private double maxY = 0;

	// Fields governing the update frequency of the graph
	private int updateInterval = 1;
	private int updateIteration = 0;

	public TrafficVolumeVisualisation(DataController dc, final OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {
		super(dc, joglPanel, visControlsContainer);

	}

	protected JPanel createControls() {
		JPanel localControls = new JPanel();

		JLabel labelMaxX = new JLabel("Set max x-axis resolution");
		final JTextField textMaxX = new JTextField();
		textMaxX.setText("128");

		JButton buttonMaxX = new JButton("Apply");
		buttonMaxX.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int newMaxX = Integer.valueOf(textMaxX.getText());
					maxX = newMaxX;
					textMaxX.setText(String.valueOf(maxX));
				} catch (NumberFormatException nfe) {
				}
			}
		});

		Box box = Box.createHorizontalBox();
		box.add(textMaxX);
		box.add(buttonMaxX);

		localControls.setLayout(new BoxLayout(localControls, BoxLayout.Y_AXIS));
		labelMaxX.setAlignmentX(Component.LEFT_ALIGNMENT);
		localControls.add(labelMaxX);
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		localControls.add(box);

		return localControls;
	}

	@Override
	// Change behaviour of packets arriving so we can process them.
	public void newPacketsArrived(List<Packet> newPackets) {
		if (joglPanel.getVis() == this) {
			this.newPackets = newPackets;
			processNewPackets(newPackets);

			// This allows us to only limit updating the visualisation
			// to update only after certain intervals. Currently unused.
			if (updateIteration == 0)
				this.render();
			updateIteration = (updateIteration + 1) % updateInterval;
		}
	}

	protected void processNewPackets(List<Packet> newPackets) {
		// Get the number of packets using each protocol
		for (Packet p : newPackets) {
			int packetTime = (int) Math.round(p.time);
			// If needed, make new protocol maps
			if (packetTime >= maxTime) {
				for (int i = maxTime; i <= packetTime + 1; i++) {
					protocolCountMaps.put(i, new HashMap<String, Integer>());
				}
				maxTime = packetTime + 1;
			}
			protocolSeen(p.protocol);
			// Update protocol map
			Map<String, Integer> protocolCount = protocolCountMaps.get(packetTime);
			if (protocolCount.get(p.protocol) == null)
				protocolCount.put(p.protocol, 1);
			else
				protocolCount.put(p.protocol, protocolCount.get(p.protocol) + 1);
		}

		// Forget data collected far in the past
		// (keep twice too many should user change maxX)
		for (int i = 0; i < maxTime - 2 * maxX; i++)
			protocolCountMaps.remove(i);
	}

	protected void protocolSeen(String protocol) {

		if (!protocolColours.containsKey(protocol)) {
			protocolColours.put(protocol, colourPalette.getNextColour());
		}

		if (!globalProtocolCount.containsKey(protocol))
			globalProtocolCount.put(protocol, 1);
		else
			globalProtocolCount.put(protocol, globalProtocolCount.get(protocol) + 1);
	}

	// Positions for the key labels in the protocol colour key (x100).
	protected Point[] keyPositions = { new Point(-93, -87), new Point(-93, -97),
			new Point(-50, -77), new Point(-50, -87), new Point(-50, -97), new Point(-10, -77),
			new Point(-10, -87), new Point(-10, -97), new Point(30, -77), new Point(30, -87),
			new Point(30, -97), new Point(70, -77), new Point(70, -87), new Point(70, -97) };

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// go through all packets if we need to
		if (allDataChanged) {
			maxTime = 0;
			protocolCountMaps.clear();
			globalProtocolCount.clear();
			processNewPackets(listOfPackets);
		}

		// find maximum heights of bars for scale
		maxY = 0;
		for (int i = maxTime - maxX; i < maxTime; i++) {
			Map<String, Integer> currentPackets = protocolCountMaps.get(i);
			if (currentPackets != null) {
				int sum = 0;
				for (int count : currentPackets.values())
					sum += count;
				maxY = Math.max(maxY, sum);
			}
		}

		// Draw the white background
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3d(0.95, 0.95, 0.95);
		gl.glVertex2d(-1, -0.7);
		gl.glVertex2d(-1, 0.9);
		gl.glVertex2d(1, 0.9);
		gl.glVertex2d(1, -0.7);
		gl.glEnd();

		double intervalWidth = 2.0 / maxX;
		double xPos = -1.0;

		// For each vertical strip
		for (int i = maxTime - maxX; i < maxTime; i++) {

			if (protocolCountMaps.containsKey(i)) {

				Map<String, Integer> currentPackets = protocolCountMaps.get(i);

				double yPos = -0.7;

				// For each protocol to be drawn in the strip
				for (String protocol : currentPackets.keySet()) {

					Color c = protocolColours.get(protocol);
					double height = currentPackets.get(protocol) * 1.6 / maxY;

					gl.glBegin(GL2.GL_QUADS);
					ColourPalette.setColour(gl, c);
					gl.glVertex2d(xPos, yPos);
					gl.glVertex2d(xPos, yPos + height);
					gl.glVertex2d(xPos + intervalWidth, yPos + height);
					gl.glVertex2d(xPos + intervalWidth, yPos);
					gl.glEnd();

					yPos += height;
				}
			}
			xPos += intervalWidth;
		}

		// Clear the title area
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3d(0, 0, 0);
		gl.glVertex2d(-1, 1);
		gl.glVertex2d(1, 1);
		gl.glVertex2d(1, 0.9);
		gl.glVertex2d(-1, 0.9);
		gl.glEnd();

		// Draw title
		gl.glColor3d(1, 1, 1);
		gl.glRasterPos2d(-0.9, 0.93);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,
				"Traffic Volume Visualisation -- Maximum y-value: " + (int) maxY + " packets");

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
		Iterator<String> protocolIterator = sortedProtocolMap.descendingKeySet().iterator();
		Stack<String> protocols = new Stack<String>();
		while (protocolIterator.hasNext())
			protocols.push(protocolIterator.next());

		while (i < keyPositions.length && !protocols.isEmpty()) {
			String protocol = protocols.pop();

			// Set label and colour
			String label = protocol + " (" + globalProtocolCount.get(protocol) + ")";

			// Set colour and position
			Color colour = protocolColours.get(protocol);
			ColourPalette.setColour(gl, colour);
			Point position = keyPositions[i];
			gl.glRasterPos2d(position.getX() / 100, position.getY() / 100);

			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, label);
			i++;
		}

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
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Traffic Volume";
	}

	@Override
	public String getDescription() {
		return getName() + "\n\n" + "Each interval of received packets is presented as a column.\n"
				+ "Individual protocols are displayed in a distinct colour to show "
				+ "how much traffic arrives over one protocol compared to others.\n"
				+ "Once the maximum x-axis resolution has been reached, old data "
				+ "is discarded to allow new data to be shown.\n"
				+ "The y-axis scales up automatically.";
	}
}
