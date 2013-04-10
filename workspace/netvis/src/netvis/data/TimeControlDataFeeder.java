package netvis.data;

import java.util.List;

import netvis.data.model.Packet;

/**
 * 
 * Feeds data to the data manager. The data manager has to make explicit
 * requests to the data feeder.
 * 
 */
public interface TimeControlDataFeeder extends DataFeeder {
	/**
	 * @param Maximum time of returned packets
	 * @return Packets that have arrived since the last request up to maxTime
	 */
	public List<Packet> getNewPackets(int maxTime);

}
