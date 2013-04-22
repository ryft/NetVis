package netvis.visualizations.gameengine;

import javax.media.opengl.GL2;

import netvis.visualizations.comets.CometHeatNode;
import netvis.visualizations.comets.FlipNode;
import netvis.visualizations.comets.GraphNode;

public interface NodePainter {

	public void DrawNode (int base, CometHeatNode node, GL2 gl);
	
	public void DrawNode (int base, FlipNode node, GL2 gl);
	
	public void DrawNode (int base, GraphNode node, GL2 gl);

	public void DrawNode (int base, Node node, GL2 gl);
}
