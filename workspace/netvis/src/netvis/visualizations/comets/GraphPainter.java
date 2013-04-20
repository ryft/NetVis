package netvis.visualizations.comets;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

import netvis.visualizations.gameengine.NodePainter;
import netvis.visualizations.gameengine.Position;
import netvis.visualizations.gameengine.TextRendererPool;

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

		for (Comet i : lum.getEntities())
		{
			if (lum.getSelected())
			{
				CometPainter.DrawEntity (i, new Position(0, 0), gl);
				CometPainter.DrawTail   (i, new Position(0, 0), gl);
				//CometPainter.DrawTrace  (i, new Position(0, 0, gl);
			}
		}
		
		// Write the name of the node
		TextRenderer renderer = TextRendererPool.get("basic");
		renderer.begin3DRendering();
		renderer.setSmoothing(true);
		renderer.setUseVertexArrays(true);
		renderer.setColor (0.2f, 0.2f, 0.2f, 1.0f);
		Rectangle2D noob = renderer.getBounds(lum.getName());
		int xx = (int) (-noob.getWidth()/2);
		int yy = (int) (-lum.getTexture().getH()/2-noob.getHeight() - 30);
		renderer.draw (lum.getName(), xx, yy);
		renderer.end3DRendering();
		renderer.flush();
	}
}
