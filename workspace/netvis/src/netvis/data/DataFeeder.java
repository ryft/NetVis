package netvis.data;

import java.util.List;

import javax.swing.JPanel;

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
	 * @return Packets that have arrived since the last request, or null
	 * 			if the DataFeeder isn't active
	 */
	public List<Packet> getNewPackets();

	/**
	 * @return True if the packet source has been exhausted (only when using a
	 *         static feed)
	 */
	public boolean hasNext();
	
	/**
	 * @return A panel with controls for the DataFeeder 
	 * 			(e.g. Time Controls) or null if not provided.
	 */
	public JPanel controlPanel();
}
