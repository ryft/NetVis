package netvis.visualizations.comets;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import netvis.visualizations.gameengine.NodePainter;

public class GraphPainter implements NodePainter {

	public void DrawNode(int base, Node lum, GL2 gl) {
		// TODO : Draw some graphs
		
		gl.glColor3d (0.0, 0.0, 0.0);
		gl.glLineWidth (15.0f);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(-30, -30);
			gl.glVertex2d(30, 30);
			gl.glVertex2d(-30, 30);
			gl.glVertex2d(30, -30);
		gl.glEnd();
	}

}
