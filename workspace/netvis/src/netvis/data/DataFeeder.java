package netvis.data;

import java.util.List;

import netvis.data.model.Packet;

/**
 * 
 * Feeds data to the data manager. The data manager has to make explicit
 * requests to the data feeder.
 * 
 */
public interface DataFeeder {
	/**
	 * 
	 * @return Packets that have arrived since the last request
	 */
	public List<Packet> getNewPackets();

	/**
	 * @return True if the packet source has been exhausted (only when using a
	 *         static feed)
	 */
	public boolean hasNext();
}
