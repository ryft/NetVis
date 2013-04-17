package netvis.visualizations.gameengine;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL4;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.visualizations.comets.ActivityVisualisation;
import netvis.visualizations.comets.Position;
import netvis.visualizations.comets.Comet;
import netvis.visualizations.comets.Node;
import netvis.visualizations.comets.Texture;

public class Painter {
	
	int width;
	int height;
	
	Texture hexagon;
	
	public Painter (int w, int h)
	{
		width = w;
		height = h;
		
		hexagon = new Texture (ActivityVisualisation.class.getResource("resources/hex.png"));
	}
	
	public void SetSize (int w, int h)
	{
		width = w;
		height = h;
	}


	public void DrawImage (Texture tex, double cx, double cy, double scale, double rot, GL2 gl)
	{
		int w = tex.getW();
		int h = tex.getH();
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 13);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		gl.glTexImage2D (GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, tex.getBB());

		// Draw the image
		gl.glEnable (GL2.GL_TEXTURE_2D);
		gl.glEnable (GL2.GL_TEXTURE_2D_MULTISAMPLE);
		gl.glBindTexture (GL.GL_TEXTURE_2D, 13);
		gl.glPushMatrix();
			
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
	
	public void DrawEntity (Comet lum, GL2 gl)
	{
		Position center = lum.getCenter();
		
		double xx = lum.getx() + center.x;
		double yy = lum.gety() + center.y;
		
		gl.glPushMatrix();
		//double tt = lum.gettilt();
		//gl.glTranslated(-XX(250), -YY(250), 0.0);
		//gl.glRotated (tt, 0.0, 0.0, 1.0);
		//gl.glTranslated(XX(250), YY(250), 0.0);
		
		this.DrawCircle(xx, yy, 20, gl);

		gl.glPopMatrix();
		
	}
	
	public void DrawTail (Comet lum, GL2 gl)
	{
		Position c = lum.getCenter();
		
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
	
	public void DrawTrace (Comet lum, GL2 gl)
	{
		Position center = lum.getCenter();
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
        		gl.glVertex2d (points.get(i).x, points.get(i).y);
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
		// Draw the inner part
		gl.glColor3dv(lum.getBGColor(), 0);
		this.DrawHexagon (GL2.GL_POLYGON, lum.getx(), lum.gety(), 400, gl);
		
		// Draw the usual hexagon
		gl.glLineWidth (3.0f);
		gl.glColor3d (0.0, 0.0, 0.0);
		this.DrawHexagon (GL2.GL_LINE_LOOP, lum.getx(), lum.gety(), 400, gl);

		// Draw the server image
		this.DrawImage (lum.getTexture(), lum.getx(),  lum.gety(), 1.0, 0.0, gl);
		
		// Draw the graphicall hexagon
		//this.DrawImage (hexagon, lum.getx(), lum.gety(), 800.0/512.0, Math.PI/6, gl);
		
		// Write the name of the node
		GLUT glut = new GLUT();
		gl.glRasterPos2d (lum.getx()-lum.getTexture().getW()/2, lum.gety()-lum.getTexture().getH()/2);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, lum.getName());
		
		// Draw entities
		for (Comet i : lum.getEntities())
		{
			this.DrawEntity (i, gl);
			this.DrawTail   (i, gl);
			//this.DrawTrace  (i, gl);
		}
	}


	public double XX (double x)
	{
		return x;//2.0*x/width - 1.0;
	}
	
	public double YY (double y)
	{
		return y;//2.0*y/height - 1.0;
	}
}
