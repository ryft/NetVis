package netvis.visualisations.gameengine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

public class TextRendererPool {
	// Singleton pattern version II - everything is static
	private TextRendererPool() {
	};

	protected static HashMap<String, Font> fonts;
	protected static HashMap<String, TextRenderer> renderers;

	static {
		renderers = new HashMap<String, TextRenderer>();
		fonts = new HashMap<String, Font>();

		LoadFont("default", 15, Painter.class.getResource("Ubuntu-L.ttf"));
		LoadFont("basic", 70, Painter.class.getResource("cmr17.ttf"));
	}

	public static void LoadFont(String name, int size, URL resource) {

		Font font;

		try {
			font = Font.createFont(Font.TRUETYPE_FONT, resource.openStream());

			// Scale it up
			font = font.deriveFont((float) size);
		} catch (FontFormatException | IOException e) {

			// Failsafe font
			font = new Font("SansSerif", Font.BOLD, size);
		}

		TextRenderer renderer = new TextRenderer(font, true, true,
				new TextRenderer.DefaultRenderDelegate(), true);

		fonts.put(name, font);
		renderers.put(name, renderer);
	}

	public static void Recreate() {
		for (String name : renderers.keySet()) {
			TextRenderer renderer = new TextRenderer(fonts.get(name), true, true,
					new TextRenderer.DefaultRenderDelegate(), true);
			renderers.put(name, renderer);
		}
	}

	public static TextRenderer get(String fontName) {
		return renderers.get(fontName);
	}

	public static Font getFont(String fontName) {
		return fonts.get(fontName);
	}

	public static void StartRend(String name, GL2 gl) {
		TextRenderer renderer = renderers.get(name);
		renderer.begin3DRendering();
		renderer.setSmoothing(true);
		renderer.setUseVertexArrays(true);
	}

	public static void FinishRend(String name, GL2 gl) {
		TextRenderer renderer = renderers.get(name);
		renderer.end3DRendering();
		renderer.flush();
	}

}
