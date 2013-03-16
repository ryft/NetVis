package netvis.data.filters;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import netvis.data.model.Packet;
import netvis.data.model.PacketFilter;

public class DummyFilter implements PacketFilter {

	@Override
	public boolean filter(Packet packet) {
		return true;
	}

	@Override
	public String description() {
		return "No filter";
	}

	@Override
	public JComponent getPanel() {
		return new JPanel();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

}
