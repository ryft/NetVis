package netvis.visualisations.comets;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.media.opengl.GL2;

import netvis.data.model.Packet;
import netvis.visualisations.gameengine.Node;
import netvis.visualisations.gameengine.NodePainter;

public class GraphNode extends Node {

	HashMap<String, Long> protocollengths;
	long maxVal = 0;

	public GraphNode(String n) {
		super(n);
	
		protocollengths = new HashMap<String, Long> ();
	}

	public void Draw(int base, NodePainter painter, GL2 gl) {
		painter.DrawNode(base, this, gl);
	}

	public void UpdateWithData(Packet pp) {
		Long val = protocollengths.get (pp.protocol);
		if (val == null)
		{
			val = new Long(0);
			protocollengths.put(pp.protocol, val);
		};
		
		val += pp.length;
		protocollengths.put(pp.protocol, val);
		
		// Compare it with the current most common protocol
		if (val > maxVal)
			maxVal = val;
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