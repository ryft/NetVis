package netvis.visualizations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.data.DataController;
import netvis.data.DataUtilities;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

public class TimePortVisualization extends AbstractVisualization {
	int noPorts;
	boolean sourceEnabled, destEnabled;
	public TimePortVisualization(DataController dc, final OpenGLPanel joglPanel, VisControlsContainer visControlsContainer){
		super(dc, joglPanel, visControlsContainer);
	    noPorts = DataUtilities.MAX_PORT;
	    this.sourceEnabled = true;
	    this.destEnabled = true;
	    
	}
	
	protected JPanel createControls() {
		JPanel localControls = new JPanel();
		JLabel sourceText = new JLabel("Source");
	    JCheckBox sourceCheckBox = new JCheckBox();
	    sourceCheckBox.setSelected(this.sourceEnabled);
	    sourceCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sourceEnabled = !sourceEnabled;
				joglPanel.redraw();
			}
	    });
	    
	    JLabel destText = new JLabel("Dest");
	    JCheckBox destCheckBox = new JCheckBox();
	    destCheckBox.setSelected(this.destEnabled);
	    destCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				destEnabled = !destEnabled;
				joglPanel.redraw();
			}
	    });
	    localControls.add(sourceText);
	    localControls.add(sourceCheckBox);
	    
	    localControls.add(destText);
	    localControls.add(destCheckBox);
		return localControls;
	}

	public void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();	    
	    gl.glEnable(GL2.GL_BLEND);
	    gl.glBlendFunc( GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA );
		if (firstDraw){
			gl.glColor3d(0, 0, 0);
			gl.glRectd(-1, -1, 1, 1);
	        drawBottom(gl);
			firstDraw = false;
		}
	  
	    /*
	     * Draw the white background
	     */
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glColor3d(0.95, 0.95, 0.95);
	    gl.glVertex2d(-1,-0.8);
	    gl.glVertex2d(-1,0.8);
	    gl.glVertex2d(1,0.8);
	    gl.glVertex2d(1,-0.8);
	    gl.glEnd();
	    
        	    
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
    	gl.glLineWidth(6);
    	if (this.sourceEnabled && this.destEnabled)
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
    	else if (this.sourceEnabled)
    	    for (int i = 0; i < noPorts; i++){
		    		drawLine(gl, sourcePorts[i], i, 0);		    
		    }
    	else if (this.destEnabled)
    		for (int i = 0; i < noPorts; i++){
	    		drawLine(gl, destPorts[i], i, 1);    
	    }
	}

	private void drawLine(GL2 gl, int val, int i, int type) {
		if (val != 0){
		    double nedLog = 0;
			gl.glBegin(GL.GL_LINES);
	
	    	nedLog = (Math.log(val)/ Math.log(2)/7);
	    	if (type == 0)
	    		gl.glColor4d(nedLog/4+0.4, 0, 0, 0.6);
	    	else
	    		gl.glColor4d(0, nedLog/4+0.4, 0, 0.6);
	    	gl.glVertex2f(-1 + 2*((float)i/noPorts) , (float) -0.8);
	    	gl.glVertex2f(-1 + 2*((float)i/noPorts), (float) (-0.8 + nedLog));
	
	    	gl.glEnd();		
		}
	}

	public String name() {
		return "Ports";
	}

	private void drawBottom(GL2 gl){		
		gl.glColor3f(1, 1, 1);

	    final GLUT glut = new GLUT();
        gl.glRasterPos2d(-0.9,0.93); // set position

        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Source/Destination Port Traffic (LOG)");
        gl.glColor3d(0.8, 0, 0);
        gl.glRasterPos2d(-0.9,0.88); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "\n Source");
        
        gl.glColor3d(0, 0.8, 0);
        gl.glRasterPos2d(-0.9,0.84); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "\n Destination");
	
		gl.glColor3d(0.9, 0.9,0.9);
		gl.glLineWidth(1);
		for (int i = 0; i < DataUtilities.MAX_PORT; i+=1000){
			gl.glBegin(GL.GL_LINES);
			gl.glVertex2f(-1 + 2*((float)i/noPorts) , (float) -0.8);
	    	gl.glVertex2f(-1 + 2*((float)i/noPorts), (float) (-0.82));
	    	gl.glEnd();	
		}
		
		gl.glLineWidth(3);
		for (int i = 0; i < DataUtilities.MAX_PORT; i+=10000){
			gl.glBegin(GL.GL_LINES);
			gl.glVertex2f(-1 + 2*((float)i/noPorts) , (float) -0.8);
	    	gl.glVertex2f(-1 + 2*((float)i/noPorts), (float) (-0.88));
	    	gl.glEnd();	
	        gl.glRasterPos2d(-0.99 + 2*((float)i/noPorts),-0.87); // set position
	        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(i));
		}
	}
}
