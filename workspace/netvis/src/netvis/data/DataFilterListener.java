package netvis.data;

import netvis.data.model.PacketFilter;

/**
 * To be used if dynamic filter adding / removing is needed;
 */
public interface DataFilterListener {

	public void filterAdded(PacketFilter filter);

	public void filterRemoved(PacketFilter filter);
}
