package netvis.visualizations;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataUtilities;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public class CopyOfTimePortVisualization extends Visualization {
	private static final long serialVersionUID = 1L;

	public CopyOfTimePortVisualization(DataController dc, OpenGLPanel joglPanel, VisControlsContainer visControlsContainer){
		super(dc, joglPanel, visControlsContainer);
	}
	
	public void display(GLAutoDrawable drawable) {
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
	    
	    int noPorts = DataUtilities.MAX_PORT;
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
		return "Ports Copy";
	}

	@Override
	protected JPanel createControls() {
		// TODO Auto-generated method stub
		return null;
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
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

}
