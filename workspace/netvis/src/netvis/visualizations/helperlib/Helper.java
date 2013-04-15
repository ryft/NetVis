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

import netvis.visualizations.comets.Entity;

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

	public ByteBuffer PrepareImage (BufferedImage bufferedImage)
	{
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		
		WritableRaster raster = 
				Raster.createInterleavedRaster (DataBuffer.TYPE_BYTE,
						w,
						h,
						4,
						null);
		ComponentColorModel colorModel=
				new ComponentColorModel (ColorSpace.getInstance(ColorSpace.CS_sRGB),
						new int[] {8,8,8,8},
						true,
						false,
						ComponentColorModel.TRANSLUCENT,
						DataBuffer.TYPE_BYTE);
		BufferedImage dukeImg = 
				new BufferedImage (colorModel,
						raster,
						false,
						null);
 
		Graphics2D g = dukeImg.createGraphics();
		g.drawImage(bufferedImage, null, null);
		DataBufferByte dukeBuf =
			(DataBufferByte)raster.getDataBuffer();
		byte[] dukeRGBA = dukeBuf.getData();
		ByteBuffer bb = ByteBuffer.wrap(dukeRGBA);
		
		bb.position(0);
		bb.mark();
		return bb;
	}
	public void DrawImage (ByteBuffer bb, double left, double top, double scale, int w, int h, GL2 gl)
	{
		gl.glBindTexture(GL.GL_TEXTURE_2D, 13);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		gl.glTexImage2D (GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, w, h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, bb);

		// Recalculate from the pixels to the coordinates
		left = 2*left/width - 1;
		top = 2*top/height - 1;
		double right = left + (scale*w)/width;
		double bottom = top + (scale*h)/height;
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture (GL.GL_TEXTURE_2D, 13);
		gl.glPushMatrix();
		gl.glRotated (3.14, 0.0, 0.0, 0.0);
		gl.glBegin (GL2.GL_POLYGON);
			gl.glTexCoord2d (1, 1);
			gl.glVertex2d (left,top);
			gl.glTexCoord2d(0,1);
			gl.glVertex2d (right, top);
			gl.glTexCoord2d(0,0);
			gl.glVertex2d (right, bottom);
			gl.glTexCoord2d(1,0);
			gl.glVertex2d (left, bottom);
		gl.glEnd ();
		gl.glPopMatrix();
	}
	
	public void DrawEntity (Entity lum, GL2 gl)
	{
		GLUT glut = new GLUT();
		
		double xx = lum.getx();
		double yy = lum.gety();
		
		double tt = lum.gettilt();
		
		gl.glPushMatrix();
		gl.glTranslated(-XX(250), -YY(250), 0.0);
		gl.glRotated (tt, 0.0, 0.0, 1.0);
		gl.glTranslated(XX(250), YY(250), 0.0);
		gl.glBegin (GL2.GL_TRIANGLE_FAN);
			int parts = 50;
			double rad = 20;
			gl.glColor3d(0.5f, 0.7f, 0.11f);
			gl.glVertex2d (2*xx/width - 1, 2*yy/height - 1);
			for (int i=0; i<=parts; i++)
			{
				//gl.glTexCoord2d(1,0);
				gl.glColor3d(0.5f, 0.7f, 0.11f);
				double x = xx + rad * Math.cos((2*Math.PI*i)/parts);
				double y = yy + rad * Math.sin((2*Math.PI*i)/parts);
				gl.glVertex2d (XX(x), YY(y));
			}
		gl.glEnd ();
		gl.glPopMatrix();
	}
	
	public double XX (double x)
	{
		return 2.0*x/width - 1.0;
	}
	
	public double YY (double y)
	{
		return 2.0*y/height - 1.0;
	}
}
