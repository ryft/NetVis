package netvis.visualisations.gameengine;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

public class VertexBuffer {

	int id;

	public int getId() {
		return id;
	};

	FloatBuffer vertices;
	FloatBuffer color;
	
	public VertexBuffer (float [] vert, int vnum) {
		id = -1;
		
		vertices = FloatBuffer.wrap(vert, 0, vnum*3);
	}
	
	public VertexBuffer (FloatBuffer vert) {
		id = -1;
		
		vertices = vert;
	}

	public int Bind (GL2 gl)
	{
		if (id == -1)
			Create(gl);
		
		return id;
	}
	
	public int Rebind(GL2 gl) {
		id = -1;
		return Bind(gl);
	}

	public void Discard() {
		id = -1;
	}
	
	public void Create (GL2 gl)
	{
		int [] arr = new int [1];
	
		gl.glGenBuffers(1, arr, 0);
		id = arr[0];
		
		// Bind to the new buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, id);
		
		// Upload data
		gl.glBufferData (GL2.GL_ARRAY_BUFFER, vertices.capacity()*Buffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_DRAW);
		gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_READ_ONLY);
		
	}
}
