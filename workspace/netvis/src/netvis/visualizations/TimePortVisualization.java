package netvis.visualizations;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.data.DataController;
import netvis.data.DataUtilities;
import netvis.ui.OpenGLPanel;

public class TimePortVisualization extends AbstractVisualization {
	int noPorts;
	public TimePortVisualization(DataController dc, OpenGLPanel joglPanel){
		super(dc, joglPanel);

	    noPorts = DataUtilities.MAX_PORT;
	}
	
	public void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();	    
	  
	    /*
	     * Draw the white background
	     */
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glColor3f(1, 1, 1);
	    gl.glVertex2f(-1,-1);
	    gl.glVertex2f(-1,1);
	    gl.glVertex2f(1,1);
	    gl.glVertex2f(1,-1);
	    gl.glEnd();
	    
        
	    gl.glColor3f(0, 0, 0);

	    final GLUT glut = new GLUT();
        gl.glRasterPos2d(-0.9,0.9); // set position

        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Source/Destination Port Traffic");
        gl.glColor3d(0.8, 0, 0);
        gl.glRasterPos2d(-0.9,0.85); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "\n Source");
        
        gl.glColor3d(0, 0.8, 0);
        gl.glRasterPos2d(-0.9,0.8); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "\n Destination");
	    
	    int[] sourcePorts = new int[noPorts];
	    int[] destPorts = new int[noPorts];
	    for (int i = 0; i < noPorts; i++){
	    	sourcePorts[i] = 0;
	    	destPorts[i] = 0;
	    }
	    for (int i = 0; i < listOfPackets.size(); i++) {
	    		sourcePorts[listOfPackets.get(i).sport]++;
	    		destPorts[listOfPackets.get(i).dport]++;
		}
    	gl.glLineWidth(3);

	    for (int i = 0; i < noPorts; i++){
	    	/*
	    	 * Draw the lines for  each port
	    	 */
	    	if (sourcePorts[i] > destPorts[i]){
	    		drawLine(gl, sourcePorts[i], i, 0);
	    		drawLine(gl, destPorts[i], i, 1);
	    	} else {
	    		drawLine(gl, destPorts[i], i, 1);
	    		drawLine(gl, sourcePorts[i], i, 0);
	    	}
	    
	    }
	}

	private void drawLine(GL2 gl, int val, int i, int type) {
	    float nedLog = 0;
		gl.glBegin(GL.GL_LINES);

    	nedLog = (float)(Math.log(val)/ Math.log(2)/7);
    	if (type == 0)
    		gl.glColor3f(nedLog, 0, 0);
    	else
    		gl.glColor3f(0, nedLog, 0);
    	gl.glVertex2f(-1 + 2*((float)i/noPorts) , (float) -0.8);
    	gl.glVertex2f(-1 + 2*((float)i/noPorts), (float) (-0.8 + nedLog));

    	gl.glEnd();		
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Ports";
	}

}
