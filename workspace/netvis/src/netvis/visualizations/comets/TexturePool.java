package netvis.visualizations.comets;

import java.net.URL;
import java.util.HashMap;

import javax.media.opengl.GL2;

public class TexturePool {

	// Singleton pattern version II - everything is static
	private TexturePool () {};
	
	protected static HashMap<String, Texture> textures;
	
	static
	{
		textures = new HashMap<String, Texture> ();
	}
	
	public static void LoadTexture (String name, URL resource) {
		textures.put(name, new Texture (resource));
	}
	
	public static void DiscardTextures ()
	{
		for (Texture t : textures.values())
		{
			t.Discard();
		}
	}

	public static Texture get(String textureName) {
		return textures.get(textureName);
	}

	public static void Rebind(GL2 gl) {

		// Rebind the textures
		for (Texture t : textures.values())
		{
			t.Rebind(gl);
		}
	}
}
