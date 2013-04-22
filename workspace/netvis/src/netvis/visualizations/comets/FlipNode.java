package netvis.visualizations.comets;

import javax.media.opengl.GL2;

import netvis.visualizations.gameengine.Framebuffer;
import netvis.visualizations.gameengine.FramebufferPool;
import netvis.visualizations.gameengine.Node;
import netvis.visualizations.gameengine.NodePainter;
import netvis.visualizations.gameengine.ValueAnimator;

public class FlipNode extends Node{

	private int framebufferid;
	public Framebuffer GetFramebuffer() {return FramebufferPool.get(framebufferid);}
	
	Node front;
	Node back;
	public Node getFrontNode () {return front;}
	public Node getBackNode  () {return back;}
	
	boolean frontSeen = true;

	ValueAnimator rotation;
	public double getRotation () {return rotation.toDouble();};
	
	public FlipNode (Node f, Node b)
	{
		super ();

		framebufferid = FramebufferPool.Generate ();
		rotation = new ValueAnimator (0.0);
		front = f;
		back  = b;
	}
	
	// Visitor double dispatch
	public void Draw (int base, NodePainter painter, GL2 gl)
	{
		painter.DrawNode(base, this, gl);
	}
	
	public void UpdateWithData (String sip)
	{
		front.UpdateWithData(sip);
		back.UpdateWithData(sip);
	}
	
	public void UpdateAnimation (long time)
	{
		front.UpdateAnimation(time);
		back.UpdateAnimation(time);
	}
	
	public int Priority ()
	{
		// Return the bigger one of the two
		return Math.max(front.Priority(), back.Priority());
	}
	
	@Override
	public void DoubleClick() {
		Flip();
	};
	
	public void Flip() {
		frontSeen = !frontSeen;
		if (frontSeen)
			rotation.MoveTo (rotation.getGoal() + 180.0, 1000);
		else
			rotation.MoveTo (rotation.getGoal() - 180.0, 1000);
	}
}
