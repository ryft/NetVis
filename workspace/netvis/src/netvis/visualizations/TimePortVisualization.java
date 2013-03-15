package netvis.visualizations;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import netvis.data.DataController;
import netvis.ui.OpenGLPanel;

public class TimePortVisualization extends AbstractVisualization {
	public TimePortVisualization(DataController dc, OpenGLPanel joglPanel){
		super(dc, joglPanel);
	}
	
	public void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();	    
	    
	    int[] Ports = new int[10000];
	    for (int i = 0; i < listOfPackets.size(); i++) {
			Ports[listOfPackets.get(i).sport]++;
		}
	    for (int i = 0; i < 10000; i++){
	    	gl.glBegin(GL.GL_LINES);
	    	gl.glColor3d(i/1000, i/1000, i/1000);
	    	gl.glVertex2i (i/10, Ports[i]*2);
	        gl.glVertex2i (i/10, 0);
	    	gl.glEnd();
	    }
	}

}
