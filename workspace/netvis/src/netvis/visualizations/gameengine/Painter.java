package netvis.visualizations.gameengine;

import java.nio.DoubleBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class Painter {
	
	/*
	 * Rotation is in degrees
	 */
	public static void DrawImage (Texture tex, double cx, double cy, double scale, double rot, GL2 gl)
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
	
	public static void TraceCurve (double[] arr, int size, GL2 gl)
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
	
	public static void DrawCircle (double x, double y, double rad, GL2 gl)
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
	
	
	public static void DrawHexagon (int mode, double x, double y, int base, GL2 gl) {
		gl.glBegin(mode);
			for (int i=0; i<6; i++)
			{
				gl.glVertex2d (x + base * Math.cos(Math.PI/6 + i*Math.PI/3), y + base * Math.sin(Math.PI/6 + i*Math.PI/3));
			};
		gl.glEnd();
		
	}
	
	public static void DrawGrid (int base, GL2 gl)
	{
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
				DrawHexagon (GL2.GL_LINE_LOOP, base*Math.sqrt(3)*i + dx, base*1.5*j, 400, gl);
			}
		}
	}
}
