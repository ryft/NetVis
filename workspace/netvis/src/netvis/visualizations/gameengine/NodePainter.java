package netvis.visualizations.gameengine;

import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

import netvis.visualizations.comets.ActivityVisualisation;
import netvis.visualizations.comets.Comet;
import netvis.visualizations.comets.Node;

public class NodePainter {
	
	static {
		TexturePool.LoadTexture("hexagon1", ActivityVisualisation.class.getResource("resources/hex1.png"));
		TexturePool.LoadTexture("hexagon2", ActivityVisualisation.class.getResource("resources/hex2.png"));;
	}
	
	public static void DrawNode (Node lum, GL2 gl)
	{
		int x = lum.getCenter().x;
		int y = lum.getCenter().y;

		// Draw the inner part
		gl.glColor3dv(lum.getBGColor(), 0);
		Painter.DrawHexagon (GL2.GL_POLYGON, x, y, 400, gl);
		
		// Draw the usual hexagon
		gl.glLineWidth (3.0f);
		gl.glColor3d (0.0, 0.0, 0.0);
		//this.DrawHexagon (GL2.GL_LINE_LOOP, x, y, 400, gl);
		
		// Draw the graphical hexagon
		if (lum.getSelected() == true)
			Painter.DrawImage (TexturePool.get("hexagon2"), x, y, 800.0/512.0, 0, gl);
		else
			Painter.DrawImage (TexturePool.get("hexagon1"), x, y, 800.0/512.0, 0, gl);
		
		// Draw the server image
		if (lum.getSelected() == true)
			Painter.DrawImage (lum.getTexture(), x, y, 1.0, 90.0, gl);
		else
			Painter.DrawImage (lum.getTexture(), x, y, 1.0, 0.0, gl);
	
		// Write the name of the node
		TextRenderer renderer = TextRendererPool.get("basic");
		renderer.begin3DRendering();
		renderer.setSmoothing(true);
		renderer.setUseVertexArrays(true);
		renderer.setColor (0.2f, 0.2f, 0.2f, 1.0f);
		Rectangle2D noob = renderer.getBounds(lum.getName());
		int xx = (int) (x-noob.getWidth()/2);
		int yy = (int) (y-lum.getTexture().getH()/2-noob.getHeight() - 30);
		renderer.draw (lum.getName(), xx, yy);
		renderer.end3DRendering();
		renderer.flush();
		
		// Draw entities
		for (Comet i : lum.getEntities())
		{
			if (lum.getSelected())
			{
				DrawEntity (i, lum.getCenter(), gl);
				DrawTail   (i, lum.getCenter(), gl);
				//DrawTrace  (i, lum.getCenter(), gl);
			}
		}
	}
	
	
	public static void DrawEntity (Comet lum, Position center, GL2 gl)
	{
		
		double xx = lum.getx() + center.x;
		double yy = lum.gety() + center.y;
		
		Painter.DrawCircle(xx, yy, 20, gl);
		
	}
	
	public static void DrawTail (Comet lum, Position c, GL2 gl)
	{
		
		double suba = 0.9;
		double subs = 10;
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
			for (Position i : lum.getTail())
			{
				int size = 15 - (int) Math.round(subs);
				gl.glColor4d(0.5, 0.0, 0.0, 1.0-suba);
				gl.glVertex2d (c.x+i.x-size/2, c.y+i.y-size/2);
				gl.glVertex2d (c.x+i.x+size/2, c.y+i.y-size/2);
				gl.glVertex2d (c.x+i.x+size/2, c.y+i.y+size/2);
				gl.glVertex2d (c.x+i.x-size/2, c.y+i.y+size/2);
				suba *= 0.92;
				subs *= 0.8;
			}
		gl.glEnd();
		gl.glPopMatrix();
	}
	
	public static void DrawTrace (Comet lum, Position center, GL2 gl)
	{

		List<Position> points = lum.getTrace();
		if (points.size() < 10)
			return;
		
		double[] arr = new double[points.size()*3 + 3];

		for (int i=0; i<points.size(); i++)
        {
			Position p = points.get(i);
			arr[3*i + 0] = p.x + center.x;
			arr[3*i + 1] = p.y + center.y;
			arr[3*i + 2] = 0.5;
        }
		arr[3*points.size() + 0] = arr[0];
		arr[3*points.size() + 1] = arr[1];
		arr[3*points.size() + 2] = arr[2];
		
		Painter.TraceCurve(arr, points.size()+1, gl);
        
		// Just draw the lines
		/*
		gl.glPushMatrix();
		gl.glLineWidth (0.0f);
		gl.glColor4d   (1.0, 0.0, 0.0, 1.0);
		gl.glBegin(GL2.GL_LINE_LOOP);
			for (Position i : lum.getTrace())
			{
				gl.glVertex2d (XX(i.x), YY(i.y));
			}
		gl.glEnd();
		gl.glPopMatrix();
		*/
        
        // Set point size larger to make more visible
        gl.glPointSize (3.0f);
        gl.glColor4d (0, 0, 0, 0.4);
        gl.glBegin(GL2.GL_POINTS);
        	for (int i=0; i<points.size(); i++)
        		gl.glVertex2d (points.get(i).x + center.x, points.get(i).y + center.y);
    	gl.glEnd();

    	
    	// Number the vertices
    	/*
		GLUT glut = new GLUT();
		for (int i=0; i<lum.getTrace().size(); i++)
		{
			Position p = lum.getTrace().get(i);
			gl.glRasterPos2d (p.x, p.y);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "" + i);
		}
		*/
	}
}
