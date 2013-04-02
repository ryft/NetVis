package netvis.visualizations;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.data.DataController;
import netvis.data.NormalizeFactory;
import netvis.data.NormalizeFactory.Normalizer;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;

/**
 * Data Flow Visualization
 * 
 * Saturation indicates time (the gray lines are past lines) the 
 * colored lines are more recent.
 * 
 * All the lines are transparent therefore if a line stands out it means
 * lots of packets go through that line.
 * 
 * The red triangles show traffic. They shrink with each iteration
 * by some percentage and grow linearly with each packet on that
 * interval. They also have an upper limit.
 */
public class DataflowVisualization extends Visualization{

	private static final long serialVersionUID = 1L;
	List<Normalizer> normPasses;
	float[][] trafficMeasure;
	private GLUT glut;

	public DataflowVisualization(DataController dataController,
			OpenGLPanel joglPanel, VisControlsContainer visControlsContainer) {
		super(dataController, joglPanel, visControlsContainer);
		normPasses = new ArrayList<Normalizer>();
		normPasses.add(NormalizeFactory.INSTANCE.getNormalizer(2));
		normPasses.add(NormalizeFactory.INSTANCE.getNormalizer(0));
		normPasses.add(NormalizeFactory.INSTANCE.getNormalizer(1));
		normPasses.add(NormalizeFactory.INSTANCE.getNormalizer(3));
		
		trafficMeasure = new float[normPasses.size()][100];
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);        
        gl.glColor3d(0.2, 0.2, 0.2);
        gl.glRasterPos3f(0, 1.01f, 0); // set position
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Brightness: Time, Opacity: Volume");

		for (int j=1; j < normPasses.size(); j++){
			gl.glColor3d(((double)(j%2)) / 30 + 0.8, ((double)(j%2)) / 30 + 0.8, ((double)(j%2)) / 30 + 0.82);
			gl.glRectd(((double)j-1) / (normPasses.size() - 1), 0, ((double)j) / (normPasses.size() - 1), 1);
		}
		for (int i =0 ; i < normPasses.size(); i++){
	        gl.glColor3d(0.2, 0.2, 0.2);
	        gl.glRasterPos3f(((float)i*1.2f) / normPasses.size(), -0.04f, 0); // set position
	        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, normPasses.get(i).name());
		}
		Packet p;

		for (int i = 0; i < newPackets.size(); i++) {
			p = newPackets.get(i);
			for (int j=0; j < normPasses.size(); j++){
				double normVal = normPasses.get(j).normalize(p);
				trafficMeasure[j][(int)(normVal * 99)] += 0.001;
			}
		}
		for (int i=0; i < normPasses.size(); i++){
			for (int j = 0; j < 100; j++){
				if ((int)(trafficMeasure[i][j]*2048) != 0){
					if (trafficMeasure[i][j] > 0.07f) trafficMeasure[i][j] = 0.07f;
					gl.glColor4f(0.6f +trafficMeasure[i][j]*4 , 0f, 0, trafficMeasure[i][j]*2);

					drawDiamond(gl,((float)i) / (normPasses.size() - 1), (float)j/100, trafficMeasure[i][j]/2 );
					trafficMeasure[i][j] = trafficMeasure[i][j] * 0.99f;
				}
			}
		}
		
		
		gl.glColor3d(0, 0, 0);
		for (int i = 0; i < listOfPackets.size(); i++) {
			p = listOfPackets.get(i);
			gl.glColor4d(0.6 , 0.6+ (double)i*0.4/listOfPackets.size(), 0.6, 0.06);
			gl.glBegin(GL2.GL_LINE_STRIP);
			
			for (int j=0; j < normPasses.size(); j++){
				double normVal = normPasses.get(j).normalize(p);
				gl.glVertex2d( ((double)j) / (normPasses.size() - 1), normVal);
			}
			
			gl.glEnd();
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// Global settings.
        gl.glEnable( GL2.GL_POINT_SMOOTH );
        gl.glEnable( GL2.GL_LINE_SMOOTH );
        gl.glShadeModel(GL2.GL_SMOOTH );
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glScaled(1.8, 1.8, 1);
        gl.glTranslated(-0.5, -0.5, 0);
        gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        gl.glLineWidth(2);
        glut = new GLUT();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {		
	}

	@Override
	protected JPanel createControls() {
		return null;
	}

	@Override
	public String name() {
		return "Data Flow";
	}
	  private void drawDiamond
	  ( GL2 gl, float xc, float yc, float radius) 
	    {
			gl.glBegin( GL2.GL_POLYGON );
			gl.glVertex2f( xc, yc - radius); 
			gl.glVertex2f( xc - radius/4, yc );
			gl.glVertex2f( xc, yc + radius); 
			gl.glVertex2f( xc + radius/4, yc );
			gl.glEnd();

	    }
}
