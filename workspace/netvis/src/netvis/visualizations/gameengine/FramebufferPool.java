package netvis.visualizations.gameengine;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

public class FramebufferPool {
	// Singleton pattern version II - everything is static
	private FramebufferPool () {};
	
	protected static List<Framebuffer> buffers;
	
	static
	{
		buffers = new ArrayList<Framebuffer> ();
	}

	public static int Generate() {
		Framebuffer fb = new Framebuffer (400);
		
		buffers.add(fb);
		return buffers.size() - 1;
	}

	public static Framebuffer get(int fbid) {
		if (fbid < buffers.size())
			return buffers.get(fbid);
		
		return null;
	}
	
	public static void RegenerateAll (GL2 gl)
	{
		for (Framebuffer i : buffers)
		{
			i.Create(gl);
		}
	}
}
