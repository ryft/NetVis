package netvis.visualisations.gameengine;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

import netvis.data.model.Packet;

public abstract class Node {

	private String name;
	public String GetName() {return name;}
	public void   SetName(String n) {name = n;}
	
	public Node(String nam) {
		name = nam;
	}

	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}
	
	Node parent = null;
	public Node GetParent() {
		return parent;
	}
	public void SetParent (Node par) {
		parent = par;
	}
	
	// Defaultly a node is a singular hexagon
	public int getDimenstion () {return 1;}
	public int getCapacity   () {return 1;}
	
	public Node GetNode (String name) {return null;}
	
	public Node GetClickedNode (int base, Position pixelposition) {return this;}
	
	public boolean AddNode (String name, Node n) {return false;}

	public abstract void UpdateWithData (Packet pp);

	public abstract void UpdateAnimation(long time);

	public abstract int Priority();

	public abstract void MouseClick (MouseEvent e);
}
