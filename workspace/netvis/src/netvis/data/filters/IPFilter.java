package netvis.data.filters;

import netvis.data.DataController;
import netvis.data.model.Packet;

/**
 * <pre>
 * IP address filter
 * Allows the user to specify a number of IP addresses to include/exclude
 * If any 'include' IPs are entered, only packets including at least one of these IPs will be used
 * If any 'exclude' IPs are entered, only packets including none of these IPs will be used
 */
public class IPFilter extends WhiteListFilter {
	
	public IPFilter(DataController dataController) {
		super(dataController, "IP Address");
	}
	
	@Override
	public boolean filter(Packet packet) {
		String dip = packet.dip;
		String sip = packet.sip;
		return ((whiteList.isEmpty() || whiteList.contains(dip) || whiteList.contains(sip)) &&
				(!blackList.contains(dip) && !blackList.contains(sip)));
	}

	@Override
	public String description() {
		return "Filter by IP address";
	}
	
}
