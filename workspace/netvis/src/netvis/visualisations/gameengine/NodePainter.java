package netvis.visualisations.gameengine;

import javax.media.opengl.GL2;

import netvis.visualisations.comets.CometHeatNode;
import netvis.visualisations.comets.FlipNode;
import netvis.visualisations.comets.GraphNode;
import netvis.visualisations.comets.HeatNode;

public interface NodePainter {

	public void DrawNode(int base, HeatNode node, GL2 gl);
	
	public void DrawNode(int base, CometHeatNode node, GL2 gl);

	public void DrawNode(int base, FlipNode node, GL2 gl);

	public void DrawNode(int base, GraphNode node, GL2 gl);

	public void DrawNode(int base, Node node, GL2 gl);
}
