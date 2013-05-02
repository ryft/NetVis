package netvis.visualisations;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataUtilities;
import netvis.data.NormaliseFactory;
import netvis.data.NormaliseFactory.Normaliser;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.util.ColourPalette;

import com.jogamp.opengl.util.gl2.GLUT;

public class DistributionVisualisation extends Visualisation {

	private static final long serialVersionUID = 1L;
	int noPorts;
	int logFactor = 6;
	int resolution = 100;
	private Normaliser normaliser;
	private int[] packetCount;
	private int[] packetCountAnimated;
	public DistributionVisualisation(DataController dc, final OpenGLPanel joglPanel, VisControlsContainer visControlsContainer){
		super(dc, joglPanel, visControlsContainer);
	    normaliser = NormaliseFactory.INSTANCE.getNormaliser(0);
	    packetCount = new int[resolution];
	    packetCountAnimated = new int[resolution + 1];
	}
	
	protected JPanel createControls() {
		JPanel panel = new JPanel();
		String[] array = new String[NormaliseFactory.INSTANCE.getAttrs().size()];
		NormaliseFactory.INSTANCE.getAttrs().toArray(array);
		
		final JComboBox<String> normaliserBox = new JComboBox<String>(array);
		normaliserBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				normaliser = NormaliseFactory.INSTANCE.getNormaliser(normaliserBox.getSelectedIndex());
			}
		});
		panel.add(normaliserBox);
		
		return panel;

	}

	public void display(GLAutoDrawable drawable) {
		
	    GL2 gl = drawable.getGL().getGL2();	    
	  
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
	    
		gl.glLineWidth(1);
		gl.glColor3d(0.8, 0.8, 0.8);
	    final GLUT glut = new GLUT();
	    for (int i = 0; i < 16; i++){

	    	gl.glBegin(GL2.GL_LINES);
	    	double height = (double)i/10 - 0.8;
	    	gl.glVertex2d(-1 , height);
	    	gl.glVertex2d(1, height);
	    	gl.glEnd();
	    	
	    	gl.glRasterPos2d(0, height + 0.02); // set position
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, String.valueOf((int)Math.exp((height+0.8)*logFactor)));
	    }
	    
	    for (int i = 0; i < resolution; i++)
	    	packetCount[i] = 0;
	    for (int i = 0; i < listOfPackets.size(); i++)
	    	packetCount[(int)(normaliser.normalise(listOfPackets.get(i))*(double)resolution)]++;
	    for (int i = 0; i < resolution; i++){
	    	packetCountAnimated[i] = packetCount[i] - (packetCount[i] - packetCountAnimated[i])*2/3;
	    }
	    double currentLog, lastLog;
	    currentLog = Math.max(Math.log(packetCountAnimated[0])/logFactor, 0);
	    gl.glLineWidth(3);
	    for (int i = 1; i <= resolution; i++){
	    	lastLog = currentLog;
	    	currentLog = Math.max(Math.log(packetCountAnimated[i])/logFactor, 0);
	    	
	    	gl.glBegin(GL2.GL_POLYGON);
	    	gl.glColor3d(0.7, 0.7, 0.7);
	    	gl.glVertex2d(-1 + 2*((double)(i-1)/resolution),-0.8 );
	    	gl.glVertex2d(-1 + 2*((double)(i-1)/resolution),-0.8 + lastLog);
	    	gl.glVertex2d(-1 + 2*((double)i/resolution),-0.8 + currentLog);
	    	gl.glVertex2d(-1 + 2*((double)i/resolution),-0.8);
		    gl.glEnd();
		    ColourPalette.setColour(gl, 
		    		ColourPalette.getColourShade(Color.red, Color.blue, (currentLog + lastLog)/2));
		    gl.glBegin(GL2.GL_LINE_STRIP);
	    	gl.glVertex2d(-1 + 2*((double)(i-1)/resolution),-0.8 + lastLog);
	    	gl.glVertex2d(-1 + 2*((double)i/resolution),-0.8 + currentLog);
		    gl.glEnd();
	    }
	      
	}


	@Override
	public void dispose(GLAutoDrawable arg0) {
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();	    
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor3d(0, 0, 0);
		gl.glRectd(-1, -1, 1, 1);
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

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Attribute Distribution";
	}

	@Override
	public String getDescription() {
		return getName()+"\n\n"+
				"Shows the distribution of a certain packet\n" +
				"attribute. It is a graded log chart.\n";
	}
}
