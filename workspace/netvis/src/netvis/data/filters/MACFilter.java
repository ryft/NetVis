package netvis.data.filters;

import netvis.data.DataController;
import netvis.data.model.Packet;

/**
 * <pre>
 * MAC address filter
 * Allows the user to specify a number of MAC addresses to include/exclude
 * If any 'include' MACs are entered, only packets including at least one of these MACs will be used
 * If any 'exclude' MACs are entered, only packets including none of these MACs will be used
 */
public class MACFilter extends WhiteListFilter {
	
	public MACFilter(DataController dataController) {
		super(dataController, "MAC Address");
	}
	
	@Override
	public boolean filter(Packet packet) {
		String dmac = packet.dmac;
		String smac = packet.smac;
		return ((whiteList.isEmpty() || whiteList.contains(dmac) || whiteList.contains(smac)) &&
				(!blackList.contains(dmac) && !blackList.contains(smac)));
	}

	@Override
	public String description() {
		return "Filter by MAC address";
	}
	
}