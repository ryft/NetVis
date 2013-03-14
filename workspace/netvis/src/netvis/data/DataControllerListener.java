package netvis.data;

import java.util.List;

import netvis.data.model.Packet;

public interface DataControllerListener {
	/**
	 * There has been a major data change. (Eg. new filter applied)
	 * @param newPackets The new list of packets
	 */
	public void allDataChanged(List<Packet> allPackets);
	/**
	 * Some new packets are available
	 * @param newPackets The new packets that are available
	 */
	public void newPacketsArrived(List<Packet> newPackets);
}
