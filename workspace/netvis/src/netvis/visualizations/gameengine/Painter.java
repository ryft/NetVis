package netvis.visualizations.gameengine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;


import com.jogamp.opengl.util.awt.TextRenderer;

import netvis.visualizations.comets.ActivityVisualisation;
import netvis.visualizations.comets.Position;
import netvis.visualizations.comets.Comet;
import netvis.visualizations.comets.Node;
import netvis.visualizations.comets.Texture;
import netvis.visualizations.comets.TexturePool;

public class Painter {
	
	int width;
	int height;
	
	Font mainfont;
	TextRenderer renderer;
	
	public Painter (int w, int h)
	{
		width = w;
		height = h;
		
		TexturePool.LoadTexture("hexagon1", ActivityVisualisation.class.getResource("resources/hex1.png"));
		TexturePool.LoadTexture("hexagon2", ActivityVisualisation.class.getResource("resources/hex2.png"));;
		
		mainfont = null;
		try {
			mainfont = Font.createFont (Font.TRUETYPE_FONT, Painter.class.getResource("cmr17.ttf").openStream());
			
			// Scale it up
			mainfont = mainfont.deriveFont (70.0f);
		} catch (FontFormatException | IOException e) {
			
			//Failsafe font
			mainfont = new Font("SansSerif", Font.BOLD, 70);
		}
		
		renderer = new TextRenderer(mainfont, true);
	}
	
	public void SetSize (int w, int h, GL2 gl)
	{
		width = w;
		height = h;
		renderer = new TextRenderer(mainfont, true);
	}

	/*
	 * Rotation is in degrees
	 */
	public void DrawImage (Texture tex, double cx, double cy, double scale, double rot, GL2 gl)
	{
		int w = tex.getW();
		int h = tex.getH();
		
		// Bind to the texture
		int id = tex.Bind (gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, id);

		// Draw the image
		gl.glEnable (GL2.GL_TEXTURE_2D);
		gl.glEnable (GL2.GL_TEXTURE_2D_MULTISAMPLE);
		gl.glPushMatrix();
			
			// Translate and rotate (rotation in degrees)
			gl.glTranslated(cx, cy, 0.0);
			gl.glRotated (rot, 0.0, 0.0, 1.0);
			
			gl.glBegin (GL2.GL_POLYGON);
				gl.glTexCoord2d (1, 0);
				gl.glVertex2d (-scale*w/2.0, scale*h/2.0);
				gl.glTexCoord2d(0,0);
				gl.glVertex2d (+scale*w/2.0, scale*h/2.0);
				gl.glTexCoord2d(0,1);
				gl.glVertex2d (+scale*w/2.0, -scale*h/2.0);
				gl.glTexCoord2d(1,1);
				gl.glVertex2d (-scale*w/2.0, -scale*h/2.0);
			gl.glEnd ();
			
		gl.glPopMatrix();
		gl.glDisable (GL2.GL_TEXTURE_2D_MULTISAMPLE);
		gl.glDisable (GL.GL_TEXTURE_2D);
	}
	
	public void TraceCurve (double[] arr, int size, GL2 gl)
	{
		DoubleBuffer ctrlPointBuffer = DoubleBuffer.allocate(3*size);

		for (int i=0; i<3*size; i++)
        	ctrlPointBuffer.put (arr[i]);

        ctrlPointBuffer.rewind();
        
        for (int i=0; i<size-10; i+=9)
        {
        	gl.glMap1d (GL2.GL_MAP1_VERTEX_3, 0.0, 1.0, 3, 10, arr, 3*i);
        	//gl.glMap2d (GL2.GL_MAP2_VERTEX_3, 0.0, 1.0, 3, size, 0.0, 1.0, 3, 1, ctrlPointBuffer);
        	gl.glEnable(GL2.GL_MAP1_VERTEX_3);
        		gl.glLineWidth(2.0f);
        		gl.glPointSize(3.0f);
	        	gl.glColor3d (0.0, 0.0, 0.0);
	        	gl.glMapGrid1d (3, 0.0, 1.0);
	        	gl.glEvalMesh1(GL2.GL_LINE, 0, 3);
	        	if (i/2 % 2 == 0)
	        		gl.glColor3d (1.0, 0.0, 0.0);
	        	else
	        		gl.glColor3d (0.0, 1.0, 0.0);
	        	gl.glEvalMesh1(GL2.GL_POINT, 0, 3);
        	gl.glDisable(GL2.GL_MAP1_VERTEX_3);
        }
        
        gl.glFlush();
	}
	
	public void DrawCircle (double x, double y, double rad, GL2 gl)
	{
		gl.glBegin (GL2.GL_TRIANGLE_FAN);
			int parts = 50;
			gl.glColor4d(0.5, 0.2, 0.0, 1.0);
			gl.glVertex2d (x, y);
			gl.glColor4d(0.7, 0.3, 0.1, 0.4);
			for (int i=0; i<=parts; i++)
			{
				// Rotate
				double xx = x + rad * Math.cos((2*Math.PI*i)/parts);
				double yy = y + rad * Math.sin((2*Math.PI*i)/parts);
				gl.glVertex2d (xx, yy);
			}
		gl.glEnd ();
	}
	
	
	private void DrawHexagon (int mode, double x, double y, int base, GL2 gl) {
		gl.glBegin(mode);
			for (int i=0; i<6; i++)
			{
				gl.glVertex2d (x + base * Math.cos(Math.PI/6 + i*Math.PI/3), y + base * Math.sin(Math.PI/6 + i*Math.PI/3));
			};
		gl.glEnd();
		
	}
	
	public void DrawEntity (Comet lum, Position center, GL2 gl)
	{
		
		double xx = lum.getx() + center.x;
		double yy = lum.gety() + center.y;
		
		this.DrawCircle(xx, yy, 20, gl);
		
	}
	
	public void DrawTail (Comet lum, Position c, GL2 gl)
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
	
	public void DrawTrace (Comet lum, Position center, GL2 gl)
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
		
		this.TraceCurve(arr, points.size()+1, gl);
        
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
	
	public void DrawNode (Node lum, GL2 gl)
	{
		int x = lum.getCenter().x;
		int y = lum.getCenter().y;

		// Draw the inner part
		gl.glColor3dv(lum.getBGColor(), 0);
		this.DrawHexagon (GL2.GL_POLYGON, x, y, 400, gl);
		
		// Draw the usual hexagon
		gl.glLineWidth (3.0f);
		gl.glColor3d (0.0, 0.0, 0.0);
		//this.DrawHexagon (GL2.GL_LINE_LOOP, x, y, 400, gl);
		
		// Draw the graphical hexagon
		if (lum.getSelected() == true)
			this.DrawImage (TexturePool.get("hexagon2"), x, y, 800.0/512.0, 0, gl);
		else
			this.DrawImage (TexturePool.get("hexagon1"), x, y, 800.0/512.0, 0, gl);
		
		// Draw the server image
		if (lum.getSelected() == true)
			this.DrawImage (lum.getTexture(), x, y, 1.0, 90.0, gl);
		else
			this.DrawImage (lum.getTexture(), x, y, 1.0, 0.0, gl);
	
		// Write the name of the node

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
		
		/*
		GLUT glut = new GLUT();
		gl.glRasterPos2d (x-lum.getTexture().getW()/2, y-lum.getTexture().getH()/2 - 30);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, lum.getName());
		*/
		
		// Draw entities
		for (Comet i : lum.getEntities())
		{
			//this.DrawEntity (i, lum.getCenter(), gl);
			//this.DrawTail   (i, lum.getCenter(), gl);
			//if (lum.getSelected())
			//	this.DrawTrace  (i, lum.getCenter(), gl);
		}
	}
	
	public void DrawGrid (GL2 gl)
	{
		int base = 400;
		// Draw the usual hexagon
		for (int i=-20; i<20; i++)
		{
			for (int j=-20; j<20; j++)
			{
				gl.glLineWidth (1.0f);
				gl.glColor3d (0.8, 0.8, 0.8);
				double dx = base/2 * Math.sqrt(3);
				if (j % 2 == 0)
					dx = 0;
				this.DrawHexagon (GL2.GL_LINE_LOOP, base*Math.sqrt(3)*i + dx, base*1.5*j, 400, gl);
			}
		}
	}
}
