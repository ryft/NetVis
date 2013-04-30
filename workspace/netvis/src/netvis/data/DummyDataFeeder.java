package netvis.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import netvis.ApplicationFrame;
import netvis.data.model.Packet;

public class DummyDataFeeder implements DataFeeder {

	public DummyDataFeeder(ApplicationFrame parent) {
		parent.setTitle("NetVis");
	}

	@Override
	public List<Packet> getNewPackets() {
		return new ArrayList<Packet>();
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	public int updateInterval() {
		return 1000;
	}

	public JPanel controlPanel() {
		return null;
	}

}
