package netvis.visualisations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.NormaliseFactory;
import netvis.data.NormaliseFactory.Normaliser;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

import com.jogamp.opengl.util.gl2.GLUT;

public class MulticubeVisualisation extends Visualisation {
	private GLU glu;
	private GLUT glut;
	private boolean spin;
	private float yrot = 0;
	private Normaliser xNormalizer, yNormalizer, zNormalizer;
	private static final long serialVersionUID = 1L;

	public MulticubeVisualisation(DataController dataController, OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {
		super(dataController, joglPanel, visControlsContainer);
		xNormalizer = NormaliseFactory.INSTANCE.getNormaliser(0);
		yNormalizer = NormaliseFactory.INSTANCE.getNormaliser(1);
		zNormalizer = NormaliseFactory.INSTANCE.getNormaliser(2);
		yrot = 1f;
		spin = true;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glTranslated(0.5, 0, 0.5);
		if (spin)
			gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		gl.glTranslated(-0.5, 0, -0.5);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glColor3d(0, 1, 0);

		gl.glColor3f(0.2f, 0.2f, 0.2f);

		for (float i = 1; i < 5; i++) {
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3d(0, 0, i / 5);
			gl.glVertex3d(1, 0, i / 5);

			gl.glVertex3d(i / 5, 0, 0);
			gl.glVertex3d(i / 5, 0, 1);
			gl.glEnd();
		}

		gl.glColor3f(0.3f, 0.7f, 0.8f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 1, 0);
		gl.glVertex3d(0, 1, 1);
		gl.glVertex3d(0, 0, 1);
		gl.glEnd();

		gl.glColor3f(0.3f, 0.7f, 0.8f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(1, 0, 0);
		gl.glVertex3d(1, 1, 0);
		gl.glVertex3d(1, 1, 1);
		gl.glVertex3d(1, 0, 1);
		gl.glEnd();

		gl.glColor3f(0.6f, 0.3f, 0.5f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(1, 0, 0);
		gl.glVertex3d(1, 0, 1);
		gl.glVertex3d(0, 0, 1);
		gl.glEnd();

		gl.glColor3f(0.6f, 0.3f, 0.5f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(0, 1, 0);
		gl.glVertex3d(1, 1, 0);
		gl.glVertex3d(1, 1, 1);
		gl.glVertex3d(0, 1, 1);
		gl.glEnd();

		// Draw the points
		Packet p;
		gl.glPointSize(5);
		gl.glBegin(GL2.GL_POINTS);

		for (int i = 0; i < this.listOfPackets.size(); i++) {
			p = listOfPackets.get(i);
			//we add entropy to make it more obvious when there
			// are many requests on a certain port
			double entropy = (Math.random()- 0.5)/100; 
			double x = xNormalizer.normalise(p) + entropy;
			double y = yNormalizer.normalise(p) + entropy;
			double z = zNormalizer.normalise(p) + entropy;
			gl.glColor4d(x, y, z, 0.3);
			gl.glVertex3d(x, y, z);
		}
		gl.glEnd();

		gl.glColor3d(1, 1, 1);
		gl.glRasterPos3d(0, 0, 0); // set position
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Origin");
		gl.glRasterPos3d(1, 0, 0); // set position
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, xNormalizer.name());
		gl.glRasterPos3d(0, 1, 0); // set position
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, yNormalizer.name());
		gl.glRasterPos3d(0, 0, 1); // set position
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, zNormalizer.name());

	}

	@Override
	protected JPanel createControls() {
		JPanel panel = new JPanel();
		String[] normaliserNames = new String[NormaliseFactory.INSTANCE.getNormalisers().size()];
		for (int i = 0; i < normaliserNames.length; i++){
			normaliserNames[i] = NormaliseFactory.INSTANCE.getNormaliser(i).name();
		}
		final JComboBox<String> xAxisBox = new JComboBox<String>(normaliserNames);
		xAxisBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xNormalizer = NormaliseFactory.INSTANCE.getNormaliser(xAxisBox.getSelectedIndex());
			}
		});
		final JComboBox<String> yAxisBox = new JComboBox<String>(normaliserNames);
		yAxisBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				yNormalizer = NormaliseFactory.INSTANCE.getNormaliser(yAxisBox.getSelectedIndex());
			}
		});
		final JComboBox<String> zAxisBox = new JComboBox<String>(normaliserNames);
		zAxisBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zNormalizer = NormaliseFactory.INSTANCE.getNormaliser(zAxisBox.getSelectedIndex());
			}

		});
		
		final JButton spinButton = new JButton("Start/Stop Spinning");
		spinButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spin = !spin;
			}
		});
		
		xAxisBox.setSelectedIndex(0);
		yAxisBox.setSelectedIndex(1);
		zAxisBox.setSelectedIndex(2);

		Box box = Box.createHorizontalBox();

		box.add(new JLabel("x axis:"));
		box.add(xAxisBox);
		panel.add(box);

		box = Box.createHorizontalBox();
		box.add(new JLabel("y axis:"));
		box.add(yAxisBox);
		panel.add(box);

		box = Box.createHorizontalBox();
		box.add(new JLabel("z axis:"));
		box.add(zAxisBox);
		panel.add(box);
		
		box = Box.createHorizontalBox();
		box.add(spinButton);
		panel.add(box);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		return panel;
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// Global settings.
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_FOG);
		gl.glEnable(GL2.GL_DITHER);
		gl.glHint(GL2.GL_FOG_HINT, GL2.GL_NICEST);

		gl.glColor3d(0, 0, 0);
		glut = new GLUT();

		// We want a nice perspective.
		// Create GLU.
		glu = new GLU();
		// Change to projection matrix.
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// Perspective.
		glu.gluPerspective(45, 1, 1, 100);
		glu.gluLookAt(2.3, 0.5, 1.8, 0.5, 0.5, 0.5, 0, 1, 0);

		// Change back to model view matrix.
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
	}

	@Override
	public String getName() {
		return "Spinning Cube";
	}

	@Override
	public String getDescription() {
		return getName() + "\n\n" + 
				"Visualization based on the Spinning cube of " +
				"potential doom.\n" +
				"Every dimension can be set to show any attribute.\n" +
				"Packets also vibrate to make dense clusters more obvious.";
	}

}