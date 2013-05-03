package netvis.visualisations;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.NormaliseFactory;
import netvis.data.NormaliseFactory.Normaliser;
import netvis.data.UndoAction;
import netvis.data.UndoController;
import netvis.data.model.Packet;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.util.ColourPalette;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * <pre>
 * Data Flow Visualisation
 * 
 * Saturation indicates time (the gray lines are past lines); the coloured lines
 * are more recent.
 * 
 * All the lines are transparent therefore if a line stands out it means lots of
 * packets go through that line.
 * 
 * The red triangles show traffic. They shrink with each iteration by some
 * percentage and grow linearly with each packet on that interval. They also
 * have an upper limit.
 */
public class DataflowVisualisation extends Visualisation implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	protected List<Normaliser> normPasses;
	float[][] trafficMeasure;
	private GLUT glut;
	private int visHighlighted;
	private boolean displaySelectionBar;
	private Color fColor, lColor;
	
	public DataflowVisualisation(DataController dataController, OpenGLPanel joglPanel,
			VisControlsContainer visControlsContainer) {
		super(dataController, joglPanel, visControlsContainer);
		normPasses = new ArrayList<Normaliser>();
		normPasses.add(NormaliseFactory.INSTANCE.getNormaliser(4));
		normPasses.add(NormaliseFactory.INSTANCE.getNormaliser(2));
		normPasses.add(NormaliseFactory.INSTANCE.getNormaliser(0));
		normPasses.add(NormaliseFactory.INSTANCE.getNormaliser(1));
		normPasses.add(NormaliseFactory.INSTANCE.getNormaliser(3));
		normPasses.add(NormaliseFactory.INSTANCE.getNormaliser(5));
		displaySelectionBar = false;
		trafficMeasure = new float[normPasses.size()][100];
		fColor = Color.green;
		lColor = Color.blue;
		
		this.addMouseListener(this);		
		this.addMouseMotionListener(this);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glColor3d(0.2, 0.2, 0.2);
		gl.glRasterPos3f(0, 1.01f, 0); // set position
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Brightness: Time, Opacity: Volume");

		for (int j = 1; j < normPasses.size(); j++) {
			gl.glColor3d(((double) (j % 2)) / 30 + 0.8, ((double) (j % 2)) / 30 + 0.8,
					((double) (j % 2)) / 30 + 0.82);
			gl.glRectd(((double) j - 1) / (normPasses.size() - 1), 0,
					((double) j) / (normPasses.size() - 1), 1);
		}
		float conditional = 0;
		for (int i = 0; i < normPasses.size(); i++) {
			gl.glColor3d(0.2, 0.2, 0.2);
			if (i == normPasses.size() - 1)
				conditional = -0.1f;
			else 
				conditional = 0f;
			gl.glRasterPos3f(((float) i * 1.2f) / normPasses.size() + conditional, -0.04f, 0); // set
																					// position
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, normPasses.get(i).name());
		}
		Packet p;


		gl.glColor3d(0, 0, 0);
		for (int i = 0; i < listOfPackets.size(); i++) {
			p = listOfPackets.get(i);
			
			double standout = (double)Math.max(0, i - listOfPackets.size() + 300) / 300; 
			double entropy = (standout*(Math.random() - 0.5)) / 300;
			
			ColourPalette.setColour(gl, ColourPalette.getColourShade( 
					ColourPalette.getColourShade(
					fColor, lColor, normPasses.get(0).normalise(p)
					), Color.LIGHT_GRAY, standout));
			gl.glBegin(GL2.GL_LINE_STRIP);
			for (int j = 0; j < normPasses.size(); j++) {
				double normVal = normPasses.get(j).normalise(p) + entropy;
				gl.glVertex2d(((double) j) / (normPasses.size() - 1), normVal);
			}
			gl.glEnd();
		}
		

		for (int i = 0; i < newPackets.size(); i++) {
			p = newPackets.get(i);
			for (int j = 0; j < normPasses.size(); j++) {
				double normVal = normPasses.get(j).normalise(p);
				trafficMeasure[j][(int) (normVal * 99)] += 0.001;
			}
		}
		for (int i = 0; i < normPasses.size(); i++) {
			for (int j = 0; j < 100; j++) {
				if ((int) (trafficMeasure[i][j] * 2048) != 0) {
					if (trafficMeasure[i][j] > 0.1f)
						trafficMeasure[i][j] = 0.1f;
					gl.glColor4f(0.6f + trafficMeasure[i][j] * 4, 0f, 0, trafficMeasure[i][j] * 4);

					drawActivityBar(gl, ((float) i) / (normPasses.size() - 1), (float) j*0.01f,
							trafficMeasure[i][j] / 2);
					trafficMeasure[i][j] = trafficMeasure[i][j] * 0.99f;
				}
			}
		}
		
		if (displaySelectionBar){
			gl.glColor4d(0, 0, 0, 0.5);
	    	gl.glBegin(GL2.GL_POLYGON);
	    	float x = ((float) visHighlighted) / (normPasses.size() - 1);
	    	
	    	gl.glVertex2f(x+0.002f, 1f);
			gl.glVertex2f(x+0.002f, 0f);
			gl.glVertex2f(x-0.002f, 0f);
			gl.glVertex2f(x-0.002f, 1f);
			
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
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glScaled(1.8, 1.8, 1);
		gl.glTranslated(-0.5, -0.5, 0);
		gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
		gl.glLineWidth(1);
		glut = new GLUT();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
	}

	@Override
	protected JPanel createControls() {
		return null;
	}

	private void drawActivityBar(GL2 gl, float xc, float yc, float radius) {
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(xc+radius, yc+0.01f);
		gl.glVertex2f(xc+radius, yc);
		gl.glVertex2f(xc-radius, yc);
		gl.glVertex2f(xc-radius, yc+0.01f);

		gl.glEnd();

	}

	@Override
	public String getName() {
		return "Data Flow";
	}

	@Override
	public String getDescription() {
		return getName() + "\n\n"
				+ "Saturation indicates time (the gray lines are \n"
				+ "past lines) the coloured lines are more recent.\n" +
				" You can follow a packet by it's colour.\n" +
				" The red bars show traffic volume.\n" +
				" They shrink with each iteration by some percentage\n" +
				" and grow linearly with each packet. They also have\n" +
				" an upper limit.";
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		double xClicked = (double)e.getX()/this.getSize().getWidth();
		int visChosen = (int)(xClicked * normPasses.size());
		visChosen = NormaliseFactory.INSTANCE.getNormalisers().indexOf(normPasses.get(visChosen));
		System.out.println("Vis Chosen: " + String.valueOf(visChosen));
		VisualisationsController vc = VisualisationsController.GetInstance();
		vc.ActivateById(2);
		vc.getVList().get(2).setState(visChosen);
		UndoController.INSTANCE.addUndoMove(new UndoAction(){
			private Visualisation backVis;
			@Override
			public void execute_undo() {
				VisualisationsController vc = VisualisationsController.GetInstance();
				vc.ActivateByReference(backVis);
			}
			private UndoAction init(Visualisation bV){
				backVis = bV;
				return this;
			}
		}.init(this));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		displaySelectionBar = true;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		displaySelectionBar = false;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double xClicked = (double)e.getX()/this.getSize().getWidth();
		visHighlighted = (int)(xClicked * normPasses.size());
	}

}
