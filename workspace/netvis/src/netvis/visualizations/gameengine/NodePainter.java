package netvis.visualizations.gameengine;

import javax.media.opengl.GL2;

import netvis.visualizations.comets.Node;

public interface NodePainter {

	public void DrawNode (int base, Node lum, GL2 gl);
}
