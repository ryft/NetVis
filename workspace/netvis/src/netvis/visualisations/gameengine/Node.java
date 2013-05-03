package netvis.visualisations.gameengine;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

public abstract class Node {

	public Node() {
	}

	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}
	
	// Defaultly a node is a singular hexagon
	public int getDimenstion () {return 1;}
	public int getCapacity   () {return 1;}
	
	public Node GetNode (String name) {return null;}
	
	public Node GetClickedNode (int base, Position pixelposition) {return this;}
	
	public boolean AddNode (String name, Node n) {return false;}

	public abstract void UpdateWithData(String sip);

	public abstract void UpdateAnimation(long time);

	public abstract int Priority();

	public abstract void MouseClick (MouseEvent e);
}
