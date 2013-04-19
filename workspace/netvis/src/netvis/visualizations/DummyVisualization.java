package netvis.visualizations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.data.DataController;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public class DummyVisualization extends Visualization {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double bgColor;
	boolean increase;
	
	Timer animator;
	
	public DummyVisualization(DataController dc, final OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		super(dc, joglPanel, visControlsContainer);
		bgColor = 0.5;
		increase = false;
		

		ActionListener animatum = new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent evnt) {

				if (bgColor < 0.4)
					increase = true;
				if (bgColor > 0.6)
					increase = false;
				
				if (increase)
					bgColor = 1.01*bgColor;
				else
					bgColor = 0.99*bgColor;
			}
		};
		
		animator = new Timer (50, animatum);
		animator.start();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

	    final GLUT glut = new GLUT();

		// this is useful if you have a incremental visualization
		// that only draws the new stuff on each iteration
		if (this.allDataChanged) { 
			
		}
		
		gl.glColor3d(1-bgColor, 1-bgColor, 1-bgColor);
		gl.glRectd(-1, -1, 1, 1);
		
		gl.glColor3d(bgColor, bgColor, bgColor);
		gl.glRectd(-0.7, -0.7, 0.7, 0.7);
		
        gl.glColor3d(bgColor, bgColor, bgColor);

        gl.glColor4d (1.0, 1.0, 1.0, 1.0);
        gl.glRasterPos2d(-0.5,0); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Number of new packets: ");
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, String.valueOf(this.newPackets.size()));
      
        gl.glRasterPos2d(0,0); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Total number of packets: ");
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, String.valueOf(this.listOfPackets.size()));
        
        gl.glColor3d(1,1,1);
        gl.glFlush();

	}

	@Override
	public String name() {
		return "Dummy Visualization";
	}

	@Override
	protected JPanel createControls() {
		JPanel dummyPanel = new JPanel();
		dummyPanel.add(new JLabel("Dummy Vis Controls..."));
		JCheckBox checkBox = new JCheckBox();
		checkBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//bgColor = 1 - bgColor;
			}
		});
		dummyPanel.add(checkBox);
		return dummyPanel;
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();	   

		gl.glColor3d(1, 1, 1);
		gl.glRectd(-1, -1, 1, 1);
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {		
	} 

}
