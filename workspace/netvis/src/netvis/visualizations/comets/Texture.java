package netvis.visualizations.comets;

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

public class Texture {

	ByteBuffer img;		public ByteBuffer getBB () {return img;}
	int width;  		public int getW () {return width;}
	int height; 		public int getH () {return height;}
	
	public Texture (URL resource)
	{
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LoadFromBuffered (bufferedImage);
	}

	public Texture (BufferedImage bufferedImage)
	{
		LoadFromBuffered (bufferedImage);
	}
	
	private void LoadFromBuffered(BufferedImage bufferedImage) {
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		
		WritableRaster raster = 
				Raster.createInterleavedRaster (DataBuffer.TYPE_BYTE,
						width,
						height,
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
		img = ByteBuffer.wrap(dukeRGBA);
		
		img.position(0);
		img.mark();
	}
	
}
