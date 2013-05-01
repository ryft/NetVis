package netvis.visualisations.gameengine;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.gl2.GLUgl2;

public class Texture {

	int id;

	public int getId() {
		return id;
	};

	ByteBuffer img;

	public ByteBuffer getBB() {
		return img;
	}

	int width;

	public int getW() {
		return width;
	}

	int height;

	public int getH() {
		return height;
	}

	public Texture(URL resource) {
		id = -1;

		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LoadFromBuffered(bufferedImage);
	}

	public Texture(BufferedImage bufferedImage) {
		id = -1;
		LoadFromBuffered(bufferedImage);
	}

	public int Bind(GL2 gl) {
		if (id == -1)
			Create (gl);

		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		return id;
	}
	
	public void Create (GL2 gl)
	{
		int[] arr = new int[1];
		gl.glGenTextures(1, arr, 0);
		id = arr[0];
		GLUgl2 glu = new GLUgl2();

		gl.glBindTexture(GL.GL_TEXTURE_2D, id);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);
		//gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

		/*
		 * New fancy way of doing it - might not work
		 * gl.glTexStorage2D(GL.GL_TEXTURE_2D, 10, GL.GL_RGBA, width, height);
		 * gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, width, height,
		 * GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, img); gl.glEnable(GL.GL_TEXTURE_2D);
		 * gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		 * gl.glDisable(GL.GL_TEXTURE_2D);
		 */

		// Old ugly way of doing it
		glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGBA, width, height, GL.GL_RGBA,
				GL.GL_UNSIGNED_BYTE, img);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
	}

	public int Rebind(GL2 gl) {
		id = -1;
		return Bind(gl);
	}

	public void Discard() {
		id = -1;
	}

	private void LoadFromBuffered(BufferedImage bufferedImage) {
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();

		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height,
				4, null);
		ComponentColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true, false,
				ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		BufferedImage dukeImg = new BufferedImage(colorModel, raster, false, null);

		Graphics2D g = dukeImg.createGraphics();
		g.drawImage(bufferedImage, null, null);
		DataBufferByte dukeBuf = (DataBufferByte) raster.getDataBuffer();
		byte[] dukeRGBA = dukeBuf.getData();
		img = ByteBuffer.wrap(dukeRGBA);

		img.position(0);
		img.mark();
	}

}
