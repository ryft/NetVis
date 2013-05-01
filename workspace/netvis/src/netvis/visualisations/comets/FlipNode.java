package netvis.visualisations.comets;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

import netvis.visualisations.gameengine.Framebuffer;
import netvis.visualisations.gameengine.FramebufferPool;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;
import netvis.visualisations.gameengine.ValueAnimator;

public class FlipNode extends Node {

	private int framebufferid;

	public Framebuffer GetFramebuffer() {
		return FramebufferPool.get(framebufferid);
	}

	Node front;
	Node back;

	public Node getFrontNode() {
		return front;
	}

	public Node getBackNode() {
		return back;
	}

	boolean frontSeen = true;

	ValueAnimator rotation;

	public double getRotation() {
		return rotation.toDouble();
	};

	public FlipNode(Node f, Node b) {
		super();

		framebufferid = FramebufferPool.Generate();
		rotation = new ValueAnimator(0.0);
		front = f;
		back = b;
	}
	
	public boolean IsFlat ()
	{
		double rot = rotation.toDouble();
		while (rot > 359.0)
			rot -= 360.0;
		while (rot < -1.0)
			rot += 360.0;
		
		double epsilon = 0.01;
		if (rot < 180.0 + epsilon && rot > 180.0 - epsilon) {
			return true;
		}
		if (rot < 0.0 + epsilon && rot > 0.0 - epsilon) {
			return true;
		}
		
		return false;
	}
	
	public Node GetSide ()
	{
		double rot = rotation.toDouble();
		while (rot > 180.0)
			rot -= 360.0;

		if (rot > -90.0 && rot < 90.0) 
			return front;
		else
			return back;
		
	}

	// Visitor double dispatch
	public void Draw(int base, NodePainter painter, GL2 gl) {


		// If one side is visible flat - just redirect rendering to this face
		// rendering procedures
		if (IsFlat())
		{
			Node visside = GetSide();
			visside.Draw(base, painter, gl);
		} else {
			// Otherwise draw the properly transformed face
			painter.DrawNode(base, this, gl);
		}

	}

	public void UpdateWithData(String sip) {
		front.UpdateWithData(sip);
		back.UpdateWithData(sip);
	}

	public void UpdateAnimation(long time) {
		front.UpdateAnimation(time);
		back.UpdateAnimation(time);
	}

	public int Priority() {
		// Return the bigger one of the two
		return Math.max(front.Priority(), back.Priority());
	}

	@Override
	public void MouseClick (MouseEvent e) {
		if (e.getClickCount() == 2) {
			Flip();
		} else {
			GetSide().MouseClick(e);
		}
	};

	public void Flip() {
		frontSeen = !frontSeen;
		if (frontSeen)
			rotation.MoveTo(rotation.getGoal() + 180.0, 1000);
		else
			rotation.MoveTo(rotation.getGoal() - 180.0, 1000);
	}
}
