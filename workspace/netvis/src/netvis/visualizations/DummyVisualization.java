package netvis.visualizations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.data.DataController;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public class DummyVisualization extends AbstractVisualization {

	double bgColor;
	
	public DummyVisualization(DataController dc, final OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		super(dc, joglPanel, visControlsContainer);
		bgColor = 0.8;
	}

	@Override
	public void render(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();	   
		if (this.firstDraw) { // First draw, need to paint a background
			gl.glColor3d(1, 1, 1);
			gl.glRectd(-1, -1, 1, 1);
		}
		// this is useful if you have a incremental visualization
		// that only draws the new stuff on each iteration
		if (this.allDataChanged) { 
			
		}
		
		gl.glColor3d(bgColor, bgColor, bgColor);
		gl.glRectd(-0.7, -0.7, 0.7, 0.7);
		
        gl.glColor3d(1-bgColor, 1-bgColor, 1-bgColor);

	    final GLUT glut = new GLUT();
        gl.glRasterPos2d(-0.5,0); // set position

        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Number of new packets: ");
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, String.valueOf(this.newPackets.size()));
        gl.glRasterPos2d(0,0); // set position
        
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Total number of packets: ");
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, String.valueOf(this.listOfPackets.size()));

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
				bgColor = 1 - bgColor;
			}
		});
		dummyPanel.add(checkBox);
		return dummyPanel;
	} 

}
