package netvis.data;

import java.util.List;

import netvis.data.model.Packet;

public interface DataControllerListener {
	/**
	 * There has been a major data change. (Eg. new filter applied)
	 * 
	 * @param allPackets
	 *            The new list of packets
	 * @param updateInterval
	 *            The interval time in ms
	 * @param intervalsComplete
	 *            The number of completed intervals so far
	 */
	public void allDataChanged(List<Packet> allPackets, int updateInterval, int intervalsComplete);

	/**
	 * Some new packets are available
	 * 
	 * @param newPackets
	 *            The new packets that are available
	 */
	public void newPacketsArrived(List<Packet> newPackets);
}
