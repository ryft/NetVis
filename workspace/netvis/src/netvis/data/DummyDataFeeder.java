package netvis.data;

import java.util.ArrayList;
import java.util.List;

import netvis.ApplicationFrame;
import netvis.data.model.Packet;

public class DummyDataFeeder implements DataFeeder {

	public DummyDataFeeder(ApplicationFrame parent) {
	}

	@Override
	public List<Packet> getNewPackets() {
		return new ArrayList<Packet>();
	}

	@Override
	public boolean hasNext() {
		return false;
	}

}
