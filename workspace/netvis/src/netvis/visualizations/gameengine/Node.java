package netvis.visualizations.gameengine;

import javax.media.opengl.GL2;

public abstract class Node {

	Position center; public Position getCenter() {return center;}; public void setCenter(Position nc) {center = nc;};
	
	public Node (int posx, int posy)
	{
		center = new Position (posx, posy);
	}
	
	public void Draw (int base, NodePainter painter, GL2 gl)
	{
		painter.DrawNode(base, this, gl);
	}
	
	public abstract void UpdateWithData (String sip);
	
	public abstract void UpdateAnimation (long time);
	
	public abstract int Priority ();
	
	public abstract void DoubleClick ();
}
