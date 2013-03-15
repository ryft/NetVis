package netvis.visualizations;

import javax.media.opengl.GLAutoDrawable;

public interface Visualization {
	/**
	 * Render the visualization using OpenGL
	 * @param drawable
	 */
	public void render(GLAutoDrawable drawable);
	
	/**
	 * Makes this visualization the active one
	 */
	public void activate();
}
