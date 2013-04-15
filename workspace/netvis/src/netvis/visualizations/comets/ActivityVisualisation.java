package netvis.visualizations.comets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

import netvis.data.DataController;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.visualizations.Visualization;
import netvis.visualizations.helperlib.Helper;

public class ActivityVisualisation extends Visualization {

	ByteBuffer serverimg;
	Helper help;
	
	int width;
	int height;
	
	Timer animator;
	List<Entity> entities;
	
	public ActivityVisualisation(DataController dataController, OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		super(dataController, joglPanel, visControlsContainer);

		
		width = joglPanel.getWidth();
		height = joglPanel.getHeight();
		help = new Helper (width, height);
		
		entities = new ArrayList<Entity>();
		
		ActionListener animatum = new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent evnt) {
				// Animate the entities!
				StepEntities();
			}
		};
		
		// Add one test entity
		entities.add(new Entity (350, 350, 200));
		entities.add(new Entity (350, 350, 200));
		entities.add(new Entity (350, 350, 200));
		
		animator = new Timer (20, animatum);
		animator.start();

		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(ActivityVisualisation.class.getResource("resources/server.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverimg = help.PrepareImage(bufferedImage);
	}

	public void StepEntities () 
	{
		for (Entity i : entities)
			i.Step();
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glLoadIdentity();
		
		//glut.glutInitDisplayMode(GLUT. GLUT_DOUBLE | GLUT.GLUT_RGBA | GLUT.GLUT_DEPTH | GLUT_MULTISAMPLE);
		gl.glEnable(GL2.GL_MULTISAMPLE);
		//gl.glShadeModel(GL2.GL_SMOOTH);
		//gl.glBlendFunc(GL2.GL_SRC_ALPHA_SATURATE, GL2.GL_ONE);
		gl.glEnable(GL2.GL_BLEND);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glDisable (GL2.GL_DEPTH_TEST);
	    gl.glEnable (GL2.GL_LINE_SMOOTH);
	    gl.glEnable (GL2.GL_POLYGON_SMOOTH );
	    gl.glHint (GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST );
	    gl.glHint (GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST );
		
		gl.glClearColor (1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear (GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		//gl.glDepthFunc(GL2.GL_NEVER);
		gl.glDepthRange (-0.5, 1.0);
        gl.glRasterPos2d (0, 0);
		gl.glColor3d (1.0f, 1.0f, 1.0f);
		//gl.glRectd (-1.0, -1.0, 1.0, 1.0);
		//gl.glTranslated(0, 0, -0.5);

		// Server image
		gl.glDepthRange (0.4, 0.9);
		help.DrawImage (serverimg, 300, 300, 1, 200, 200, gl);
		
		// Draw entities
		gl.glDepthRange (0.3, 0.8);
		for (Entity i : entities)
			help.DrawEntity(i, gl);

		gl.glFlush();
	}
	
	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reshape(GLAutoDrawable gl, int arg1, int arg2, int wi, int he) {
		width = wi;
		height = he;
		help.SetSize (width, height);
	}

	@Override
	protected JPanel createControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "One machine activity";
	}

	@Override
	public void everythingEnds() {
		super.everythingEnds();
		System.out.println("The only visualization stops, you fools...");		
	}

}
