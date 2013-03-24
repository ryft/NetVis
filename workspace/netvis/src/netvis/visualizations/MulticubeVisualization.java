package netvis.visualizations;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.swing.JLabel;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataUtilities;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public class MulticubeVisualization extends Visualization{
	private GLU glu;
	private static final long serialVersionUID = 1L;
	public MulticubeVisualization(DataController dataController,
			OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		super(dataController, joglPanel, visControlsContainer);
	}
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		if(this.allDataChanged){
	        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);        
	        gl.glColor3d(0, 1, 0);
	        
			gl.glBegin(GL.GL_LINES);
			
			gl.glEnd();
	        
	           
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
		}
		Packet p;
		gl.glPointSize(3);
		gl.glBegin (GL2.GL_POINTS);

		for (int i = 0; i < newPackets.size(); i++) {
			p = newPackets.get(i);
			
			double x = DataUtilities.normalizeV4Ip(p.sip);
			double z = DataUtilities.normalizeV4Ip(p.dip);
			double y = DataUtilities.normalizePort(p.sport);
			System.out.println(x);
			gl.glColor4d(x, y, z, 0.3);
			gl.glVertex3d(x, y, z);
		}
		gl.glEnd();
	}

	@Override
	public String name() {
		return "Custom Cube";
	}

	@Override
	protected JPanel createControls() {
		JPanel panel = new JPanel();
		
		panel.add(new JLabel("Customizable soon. Now it is Source IP / Dest IP / SPort"));
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
