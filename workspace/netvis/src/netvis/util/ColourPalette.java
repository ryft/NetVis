package netvis.util;

import java.awt.Color;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.media.opengl.GL2;

public class ColourPalette {

	// Static constants to be used on initiation
	public final static int SCHEME_QUALITATIVE = 0;
	public final static int SCHEME_SEQUENTIAL = 1;
	public final static int SCHEME_DIVERGENT = 2;

	protected final int SCHEME;
	protected final Set<Integer> usedIndices = new HashSet<Integer>();
	protected final Random random = new Random();

	// Define colours to be picked from (Qualitative, Sequential, Divergent)
	protected Color[][] colours = {
			{ new Color(141, 211, 199), new Color(255, 255, 179), new Color(190, 186, 218),
					new Color(251, 128, 114), new Color(128, 177, 211), new Color(253, 180, 98),
					new Color(179, 222, 105), new Color(252, 205, 229), new Color(217, 217, 217),
					new Color(188, 128, 189), new Color(204, 235, 197), new Color(255, 237, 111) },
			{ new Color(255, 255, 204), new Color(255, 237, 160), new Color(254, 217, 118),
					new Color(254, 178, 76), new Color(253, 141, 60), new Color(252, 78, 42),
					new Color(227, 26, 28), new Color(189, 0, 38), new Color(128, 0, 38) },
			{ new Color(178, 24, 43), new Color(214, 96, 77), new Color(244, 165, 130),
					new Color(253, 219, 199), new Color(247, 247, 247), new Color(209, 229, 240),
					new Color(146, 197, 222), new Color(67, 147, 195), new Color(33, 102, 172) } };

	/**
	 * A colour palette to provide colours for data visualisation.
	 * 
	 * @param colourScheme
	 *            The colour scheme to use. Please provide a ColourPalette
	 *            constant.
	 */
	public ColourPalette(int colourScheme) {
		SCHEME = colourScheme;
	}

	public Color getNextColour() {

		int colourCount = colours[SCHEME].length;
		int currentIndex = random.nextInt(colourCount);

		// Reset used indices if all colours have been used
		if (usedIndices.size() == colourCount)
			usedIndices.clear();

		// If we've used this colour before, pick another
		while (usedIndices.contains(currentIndex))
			currentIndex = random.nextInt(colourCount);

		return colours[SCHEME][currentIndex];
	}

	public void setColour(GL2 gl, Color colour) {

		gl.glColor3d(normalise(colour.getRed()), normalise(colour.getGreen()),
				normalise(colour.getBlue()));

	}

	public void setNextColour(GL2 gl) {
		setColour(gl, getNextColour());

	}

	protected double normalise(int x) {

		return (x / 256.0);
	}

}
