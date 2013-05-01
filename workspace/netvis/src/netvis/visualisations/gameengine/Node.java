package netvis.visualisations.gameengine;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

public abstract class Node {

	public Node() {
	}

	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}

	public abstract void UpdateWithData(String sip);

	public abstract void UpdateAnimation(long time);

	public abstract int Priority();

	public abstract void MouseClick (MouseEvent e);
}
