package netvis.visualisations.comets;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;

import javax.media.opengl.GL2;

import netvis.data.model.Packet;
import netvis.visualisations.gameengine.NodePainter;

public class CometHeatNode extends HeatNode {

	HashMap<String, Comet> entities;

	public Collection<Comet> getEntities() {
		return entities.values();
	}


	boolean changed = true;

	public CometHeatNode(String texturename, String nodename) {
		super (texturename, nodename);

		entities = new HashMap<String, Comet>();
	}

	public void Draw(int base, NodePainter painter, GL2 gl) {
		// Draw one on top of the other
		painter.DrawNode(base, (HeatNode) this, gl);
		painter.DrawNode(base, (CometHeatNode) this, gl);
	}

	public void UpdateWithData(Packet pp) {
		// Make their tilts nicely shifted
		AddSatelite(pp.sip, 100, entities.size() * Math.PI / 10);
		
		super.UpdateWithData (pp);
	}

	public void UpdateAnimation(long time) {
		for (Comet i : entities.values())
			i.Step(time);
	}

	public void AddSatelite(String sip, int amp, double tilt) {
		// Look for the entity with the same name
		Comet find = entities.get(sip);

		if (find == null) {
			entities.put(sip, new Comet(amp, tilt));
		}
	}

	public void RemoveSatelite(String sip) {
		// Look for the entity
		Comet find = entities.get(sip);

		if (find != null) {
			entities.remove(sip);
			DecreaseWarning();
		}
	}

	@Override
	public void MouseClick (MouseEvent e) {
		super.MouseClick (e);
	}

}