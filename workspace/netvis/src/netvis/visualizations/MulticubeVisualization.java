package netvis.visualizations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.data.DataController;
import netvis.data.NormalizeFactory;
import netvis.data.NormalizeFactory.Normalizer;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public class MulticubeVisualization extends Visualization{
	private GLU glu;
	private GLUT glut;
	private float yrot = 0;
	private Normalizer xNormalizer, yNormalizer, zNormalizer;
	private static final long serialVersionUID = 1L;
	public MulticubeVisualization(DataController dataController,
			OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		super(dataController, joglPanel, visControlsContainer);
		xNormalizer = NormalizeFactory.INSTANCE.getNormalizer(0);
		yNormalizer = NormalizeFactory.INSTANCE.getNormalizer(1);
		zNormalizer = NormalizeFactory.INSTANCE.getNormalizer(2);
		yrot = 1f;
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glTranslated(0.5, 0, 0.5);
		gl.glRotatef(yrot,0.0f,1.0f,0.0f);
		gl.glTranslated(-0.5, 0, -0.5);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);        
        gl.glColor3d(0, 1, 0);
        
        gl.glColor3f(0.2f, 0.2f, 0.2f);

        for (float i =1 ; i < 5; i++){
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3d(0, 0, i/5);
			gl.glVertex3d(1, 0, i/5);

			gl.glVertex3d(i/5, 0, 0);
			gl.glVertex3d(i/5, 0, 1);
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
		gl.glBegin (GL2.GL_POINTS);

		for (int i = 0; i < this.listOfPackets.size(); i++) {
			p = listOfPackets.get(i);
			
			double x = xNormalizer.normalize(p);
			double z = yNormalizer.normalize(p);
			double y = zNormalizer.normalize(p);
			gl.glColor4d(x, y, z, 0.3);
			gl.glVertex3d(x, y, z);
		}
		gl.glEnd();
		
        gl.glColor3d(1, 1, 1);
        gl.glRasterPos3d(0,0, 0); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Origin");
        gl.glRasterPos3d(1,0,0); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, xNormalizer.name());
        gl.glRasterPos3d(0,1,0); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, yNormalizer.name());
        gl.glRasterPos3d(0,0,1); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, zNormalizer.name());

	}

	@Override
	public String name() {
		return "Custom Cube";
	}

	@Override
	protected JPanel createControls() {
		JPanel panel = new JPanel();
		String[] array = new String[NormalizeFactory.INSTANCE.getAttrs().size()];
		NormalizeFactory.INSTANCE.getAttrs().toArray(array);
		
		final JComboBox<String> xAxisBox = new JComboBox<String>(array);
		xAxisBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				xNormalizer = NormalizeFactory.INSTANCE.getNormalizer(
						xAxisBox.getSelectedIndex());	
			}
		});
		final JComboBox<String> yAxisBox = new JComboBox<String>(array);
		yAxisBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				yNormalizer = NormalizeFactory.INSTANCE.getNormalizer(
						yAxisBox.getSelectedIndex());		
			}
		});		
		final JComboBox<String> zAxisBox = new JComboBox<String>(array);
		zAxisBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				zNormalizer = NormalizeFactory.INSTANCE.getNormalizer(
						zAxisBox.getSelectedIndex());	
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
        gl.glColor3d(0, 0, 0);
        glut = new GLUT();

        // We want a nice perspective.
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
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
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {		
	}
}
