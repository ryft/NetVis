package netvis.visualizations.helperlib;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

import netvis.visualizations.comets.Position;
import netvis.visualizations.comets.Comet;
import netvis.visualizations.comets.Node;
import netvis.visualizations.comets.Texture;

public class Helper {
	
	int width;
	int height;
	
	public Helper (int w, int h)
	{
		width = w;
		height = h;
	}
	
	public void SetSize (int w, int h)
	{
		width = w;
		height = h;
	}


	public void DrawImage (Texture tex, double right, double bottom, double scale, GL2 gl)
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

		// Recalculate from the pixels to the coordinates

		right = XX(right);
		bottom = YY(bottom);
		
		double left = right + (scale*w);
		double top = bottom + (scale*h);
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture (GL.GL_TEXTURE_2D, 13);
		gl.glPushMatrix();
		gl.glRotated (3.14, 0.0, 0.0, 0.0);
		gl.glBegin (GL2.GL_POLYGON);
			gl.glTexCoord2d (0, 0);
			gl.glVertex2d (left,top);
			gl.glTexCoord2d(1,0);
			gl.glVertex2d (right, top);
			gl.glTexCoord2d(1,1);
			gl.glVertex2d (right, bottom);
			gl.glTexCoord2d(0,1);
			gl.glVertex2d (left, bottom);
		gl.glEnd ();
		gl.glPopMatrix();
		gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
	public void DrawEntity (Comet lum, GL2 gl)
	{
		GLUT glut = new GLUT();
		
		double xx = lum.getx();
		double yy = lum.gety();
		
		double tt = lum.gettilt();
		
		gl.glPushMatrix();
		//gl.glTranslated(-XX(250), -YY(250), 0.0);
		//gl.glRotated (tt, 0.0, 0.0, 1.0);
		//gl.glTranslated(XX(250), YY(250), 0.0);
		
		gl.glBegin (GL2.GL_TRIANGLE_FAN);
			int parts = 50;
			double rad = 15;
			gl.glColor4d(0.5, 0.2, 0.0, 1.0);
			gl.glVertex2d (XX(xx), YY(yy));
			gl.glColor4d(0.7, 0.3, 0.1, 0.4);
			for (int i=0; i<=parts; i++)
			{
				// Rotate
				double x = xx + rad * Math.cos((2*Math.PI*i)/parts);
				double y = yy + rad * Math.sin((2*Math.PI*i)/parts);
				gl.glVertex2d (XX(x), YY(y));
			}
		gl.glEnd ();
		gl.glPopMatrix();
		
		double suba = 0.9;
		double subs = 4;
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_QUADS);
			for (Position i : lum.getTail())
			{
				int size = 10 - (int) Math.round(subs);
				gl.glColor4d(0.5, 0.0, 0.0, 1.0-suba);
				gl.glVertex2d (XX(i.x), YY(i.y));
				gl.glVertex2d (XX(i.x+size), YY(i.y));
				gl.glVertex2d (XX(i.x+size), YY(i.y+size));
				gl.glVertex2d (XX(i.x), YY(i.y+size));
				suba *= 0.92;
				subs *= 0.9;
			}
		gl.glEnd();
		gl.glPopMatrix();
	}
	
	public void DrawTrace (Comet lum, GL2 gl)
	{
		GLUT glut = new GLUT();
		
		double xx = lum.getx();
		double yy = lum.gety();
		
		gl.glPushMatrix();
		gl.glLineWidth((float) 1.0);
		gl.glBegin(GL2.GL_LINE_LOOP);
			for (Position i : lum.getTrace())
			{
				gl.glColor4d(0.0, 0.0, 0.0, 1.0);
				gl.glVertex2d (XX(i.x), YY(i.y));
			}
		gl.glEnd();
		gl.glPopMatrix();
	}
	
	public void DrawNode (Node lum, GL2 gl)
	{
		this.DrawImage (lum.getTexture(), -50, -50, 0.5, gl);
		
		// Draw entities
		for (Comet i : lum.getEntities())
		{
			this.DrawEntity (i, gl);
			this.DrawTrace  (i, gl);
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
