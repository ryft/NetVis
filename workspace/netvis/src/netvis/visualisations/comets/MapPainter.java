package netvis.visualisations.comets;

import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import netvis.visualisations.gameengine.Framebuffer;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;
import netvis.visualisations.gameengine.Painter;
import netvis.visualisations.gameengine.Position;
import netvis.visualisations.gameengine.TextRendererPool;
import netvis.visualisations.gameengine.TexturePool;

import com.jogamp.opengl.util.awt.TextRenderer;

public class MapPainter implements NodePainter {

	static {
		// Load all the necessary textures
		TexturePool.LoadTexture("hexagon1",	ActivityVisualisation.class.getResource("resources/hex1.png"));
		TexturePool.LoadTexture("hexagon2",	ActivityVisualisation.class.getResource("resources/hex2.png"));
		
		TexturePool.LoadTexture("server", Map.class.getResource("resources/server.png"));
		TexturePool.LoadTexture("basic", Map.class.getResource("resources/basic.png"));
	}
	
	public void DrawNode(int base, HeatNode lum, GL2 gl) {
		
		double [] color = lum.getBGColor();
		double opacity = lum.getOpacity();

		// Draw the background
		gl.glPushMatrix();
		gl.glTranslated(0.0, 0.0, -1.0);
		gl.glColor4d (color[0], color[1], color[2], opacity);
		Painter.DrawHexagon(GL2.GL_POLYGON, 0, 0, base, gl);
		gl.glPopMatrix();

		// Draw the usual hexagon
		gl.glLineWidth(3.0f);
		gl.glColor3d(0.0, 0.0, 0.0);
		// this.DrawHexagon (GL2.GL_LINE_LOOP, x, y, 400, gl);
		
		// Draw the graphical hexagon
		if (lum.getSelected() == true)
			Painter.DrawImage(TexturePool.get("hexagon2"), 0.0, 0.0, 2 * base / 512.0, 0, opacity, gl);
		else
			Painter.DrawImage(TexturePool.get("hexagon1"), 0.0, 0.0, 2 * base / 512.0, 0, opacity, gl);

		// Draw the server image
		int imageSize = 200;
		if (lum.getSelected() == true)
			Painter.DrawImage(TexturePool.get(lum.getTexture()), 0.0, 0.0, imageSize / 512.0, 90.0, opacity, gl);
		else
			Painter.DrawImage(TexturePool.get(lum.getTexture()), 0.0, 0.0, imageSize / 512.0, 0.0, opacity, gl);

		// Write the name of the node
		TextRenderer renderer = TextRendererPool.get("basic");
		renderer.begin3DRendering();
		renderer.setSmoothing(true);
		renderer.setUseVertexArrays(true);
		renderer.setColor (0.2f, 0.2f, 0.2f, (float) opacity);
		Rectangle2D noob = renderer.getBounds(lum.getName());
		int xx = (int) (-noob.getWidth() / 2);
		int yy = (int) (-imageSize / 2 - noob.getHeight() - 30);
		renderer.draw(lum.getName(), xx, yy);
		renderer.end3DRendering();
		// Big slow down if this is uncommented
		// renderer.flush();

	}

	public void DrawNode(int base, CometHeatNode lum, GL2 gl) {
		// Draw everything in the centre - this is all drawn to the texture that
		// is later on clipped onto the hex

		// Draw entities
		for (Comet i : lum.getEntities()) {
			DrawEntity(i, gl);
			DrawTail(i, gl);
			// DrawTrace (i, gl);
		}
	}

	public void DrawNode(int base, GraphNode lum, GL2 gl) {
		// TODO : Draw some graphs

		gl.glColor3d(0.0, 0.0, 0.0);
		gl.glLineWidth(15.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(-30, -30);
		gl.glVertex2d(30, 30);
		gl.glVertex2d(-30, 30);
		gl.glVertex2d(30, -30);
		gl.glEnd();

		// Write the name of the node
		TextRenderer renderer = TextRendererPool.get("basic");
		renderer.begin3DRendering();
		renderer.setSmoothing(true);
		renderer.setUseVertexArrays(true);
		renderer.setColor(0.2f, 0.2f, 0.2f, 1.0f);
		Rectangle2D noob = renderer.getBounds(lum.getName());
		int xx = (int) (-noob.getWidth() / 2);
		int yy = (int) (-noob.getHeight() - 40.0);
		renderer.draw(lum.getName(), xx, yy);
		renderer.end3DRendering();
		renderer.flush();
	}

	public void DrawNode(int base, FlipNode lum, GL2 gl) {
		double rotation = lum.getRotation();

		// Normalize the rotation
		while (rotation > 90.0)	rotation -= 180.0;
		while (rotation <-90.0)	rotation += 180.0;

		Node todraw = lum.GetSide();

		// Texture id and Framebuffer id
		int[] texfb = DrawNodeToTheTexture(base, todraw, lum.GetFramebuffer(), gl);

		// Now display the front texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, texfb[0]);
		gl.glPushMatrix();
		gl.glTranslated(0.0, 0.0, -500.0);
		gl.glRotated(rotation, 1.0, 0.0, 0.0);

		Painter.DrawSquare(2 * base, 2 * base, 0, 0, 1.0, 180.0, gl);

		gl.glPopMatrix();
	}

	public int[] DrawNodeToTheTexture(int base, Node lum, Framebuffer fb, GL2 gl) {
		// Find out the viewport size
		int[] viewport = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		// Think of rounding up the base to the power of two

		int textureid = fb.BindTexture(gl);
		int fbufferid = fb.BindFBuffer(gl);
		int dbufferid = fb.BindDBuffer(gl);

		// Switch rendering to the framebuffer
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbufferid);
		gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, fbufferid);

		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		fb.SetupView(gl);

		lum.Draw(base, this, gl);

		gl.glPopMatrix();

		// Switch back to the back buffer
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, 0);

		// and the correct viewport
		gl.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

		gl.glBindTexture(GL.GL_TEXTURE_2D, textureid);
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);

		return new int[] { textureid, fbufferid, dbufferid };
	}

	public void DrawNode(int base, Node lum, GL2 gl) {
		// Do nothing
	}

	public static void DrawEntity(Comet lum, GL2 gl) {
		Painter.DrawCircle(lum.getx(), lum.gety(), 20, gl);
	}

	public static void DrawTail(Comet lum, GL2 gl) {

		double suba = 0.9;
		double subs = 10;
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
		for (final Position i : lum.getTail()) {
			int size = 15 - (int) Math.round(subs);
			gl.glColor4d(0.5, 0.0, 0.0, 1.0 - suba);
			gl.glVertex2d(i.x - size / 2, i.y - size / 2);
			gl.glVertex2d(i.x + size / 2, i.y - size / 2);
			gl.glVertex2d(i.x + size / 2, i.y + size / 2);
			gl.glVertex2d(i.x - size / 2, i.y + size / 2);
			suba *= 0.92;
			subs *= 0.8;
		}
		gl.glEnd();
		gl.glPopMatrix();
	}

	public static void DrawTrace(Comet lum, GL2 gl) {

		List<Position> points = lum.getTrace();
		if (points.size() < 10)
			return;

		double[] arr = new double[points.size() * 3 + 3];

		for (int i = 0; i < points.size(); i++) {
			Position p = points.get(i);
			arr[3 * i + 0] = p.x;
			arr[3 * i + 1] = p.y;
			arr[3 * i + 2] = 0.5;
		}
		arr[3 * points.size() + 0] = arr[0];
		arr[3 * points.size() + 1] = arr[1];
		arr[3 * points.size() + 2] = arr[2];

		Painter.TraceCurve(arr, points.size() + 1, gl);

		// Just draw the lines
		/*
		 * gl.glPushMatrix(); gl.glLineWidth (0.0f); gl.glColor4d (1.0, 0.0,
		 * 0.0, 1.0); gl.glBegin(GL2.GL_LINE_LOOP); for (Position i :
		 * lum.getTrace()) { gl.glVertex2d (XX(i.x), YY(i.y)); } gl.glEnd();
		 * gl.glPopMatrix();
		 */

		// Set point size larger to make more visible
		gl.glPointSize(3.0f);
		gl.glColor4d(0, 0, 0, 0.4);
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0; i < points.size(); i++)
			gl.glVertex2d(points.get(i).x, points.get(i).y);
		gl.glEnd();

		// Number the vertices
		/*
		 * GLUT glut = new GLUT(); for (int i=0; i<lum.getTrace().size(); i++) {
		 * Position p = lum.getTrace().get(i); gl.glRasterPos2d (p.x, p.y);
		 * glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "" + i); }
		 */
	}

}
