package netvis.visualizations;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import netvis.data.DataController;
import netvis.ui.OpenGLPanel;

public class CopyOfTimePortVisualization extends AbstractVisualization {
	public CopyOfTimePortVisualization(DataController dc, OpenGLPanel joglPanel){
		super(dc, joglPanel);
	}
	
	public void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();	    
	  
	    /*
	     * Draw the white background
	     */
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glColor3f(0, 0, 0);
	    gl.glVertex2f(-1,-1);
	    gl.glVertex2f(-1,1);
	    gl.glVertex2f(1,1);
	    gl.glVertex2f(1,-1);
	    gl.glEnd();
	    float nedLog = 0;
	    
	    int noPorts = 70000;
	    int[] Ports = new int[noPorts];
	    for (int i = 0; i < noPorts; i++)
	    	Ports[i] = 0;
	    for (int i = 0; i < listOfPackets.size(); i++) {
	    	if (listOfPackets.get(i).sport >= noPorts)
	    		System.out.println(listOfPackets.get(i).sport);
	    	else
	    		Ports[listOfPackets.get(i).sport]++;
		}
    	gl.glLineWidth(3);

	    for (int i = 0; i < noPorts; i++){
	    	/*
	    	 * Draw the lines for  each port
	    	 */
	    	gl.glBegin(GL.GL_LINES);
	    	nedLog = (float)(Math.log(Ports[i])/ Math.log(2)/7);
	    	gl.glColor3f(0, nedLog, 0);
	        gl.glVertex2f(-1 + 2*((float)i/noPorts) , (float) -0.8);
	    	gl.glVertex2f(-1 + 2*((float)i/noPorts), (float) (-0.8 + nedLog));
	
	    	gl.glEnd();
	    }
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Ports Copy";
	}

}
