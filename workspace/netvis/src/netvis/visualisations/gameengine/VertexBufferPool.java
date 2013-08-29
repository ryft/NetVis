package netvis.visualisations.gameengine;

import java.util.HashMap;

import javax.media.opengl.GL2;

public class VertexBufferPool {

	// Singleton pattern version II - everything is static
	private VertexBufferPool() {
	};

	protected static HashMap<String, VertexBuffer> vertexbuffers;

	static {
		vertexbuffers = new HashMap<String, VertexBuffer>();
	}

	public static void PutBuffer (String name, VertexBuffer resource) {
		vertexbuffers.put (name, resource);
	}

	public static void DiscardAll() {
		for (VertexBuffer t : vertexbuffers.values()) {
			t.Discard();
		}
	}

	public static VertexBuffer get(String buffername) {
		return vertexbuffers.get (buffername);
	}

	public static void Rebind(GL2 gl) {

		// Rebind the textures
		for (VertexBuffer t : vertexbuffers.values()) {
			t.Rebind(gl);
		}
	}
}
