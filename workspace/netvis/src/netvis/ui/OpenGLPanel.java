package netvis.ui;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import netvis.visualizations.Visualization;

public class OpenGLPanel extends JPanel implements GLEventListener {

	private static final long serialVersionUID = 1L;
	private Visualization currentVis;
	private GLCanvas glCanvas;
	public OpenGLPanel() {

		// Set up OpenGL window
		GLProfile glProfile = GLProfile.getDefault();
		GLProfile.initSingleton();
		GLCapabilities glCaps = new GLCapabilities(glProfile);

		// The canvas is the widget that's drawn in the JFrame
		glCanvas = new GLCanvas(glCaps);
		glCanvas.setSize(800, 500);

		add(glCanvas);
		glCanvas.addGLEventListener(this);
	}
	public void redraw(){
		glCanvas.display();
	}
	public void setVis(Visualization vis){
		currentVis = vis;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		currentVis.render(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	}

}
