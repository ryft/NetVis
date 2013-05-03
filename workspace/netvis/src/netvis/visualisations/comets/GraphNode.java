package netvis.visualisations.comets;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

import netvis.data.model.Packet;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;

public class GraphNode extends Node {

	String name;

	public String getName() {
		return name;
	}

	public GraphNode(String n) {
		super();
		name = n;
	}

	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}

	public void UpdateWithData(Packet pp) {
	}

	public void UpdateAnimation(long time) {
	}

	public int Priority() {
		// Those nodes don't want to be in the middle
		return -1;
	}

	@Override
	public void MouseClick (MouseEvent e) {}

}