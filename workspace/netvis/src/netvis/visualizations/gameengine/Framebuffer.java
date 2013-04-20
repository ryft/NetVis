package netvis.visualizations.gameengine;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class Framebuffer {

	int textureid;
	int bufferid;
	
	int base;
	
	public Framebuffer (int b)
	{
		textureid = -1;
		bufferid  = -1;
		
		base = b;
	}
	
	public int BindBuffer (GL2 gl)
	{
		if (bufferid != -1)
			return bufferid;
		else
			Create(gl);
		
		return bufferid;
	}
	
	public int BindTexture (GL2 gl)
	{
		if (textureid != -1)
			return textureid;
		else
			Create(gl);
		
		return textureid;
	}
	
	public void Create (GL2 gl)
	{
		int [] texture = new int [1];
		int [] fbuffer = new int [1];
		
		// Generate the texture to render to
		gl.glGenTextures(1, texture, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0]);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR );
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR );
		
		// Reserve space for the texture - make its dimensions twice as big as necessary
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, 2*base, 2*base, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, null);
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		
		// Generate the framebuffer
		gl.glGenFramebuffers (1, fbuffer, 0);
		gl.glBindFramebuffer (GL.GL_FRAMEBUFFER, fbuffer[0]);
		
		// Attach the texture to the framebuffer
		gl.glFramebufferTexture2D (GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, texture[0], 0);
		
		textureid = texture[0];
		bufferid  = fbuffer[0];
	}
	
	public void Delete (GL2 gl)
	{
		// Cleanup
		gl.glDeleteTextures (1, new int[] {textureid}, 0);
		gl.glDeleteFramebuffers (1, new int[] {bufferid}, 1);
		
		textureid = -1;
		bufferid = -1;
	}
}
