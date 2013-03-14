package netvis.data.model;

public interface PacketFilter {
	
	/**
	 * Returns TRUE if packet passes the filter test
	 */
	public boolean filter(Packet packet);
	/**
	 * @return Description of this filter. May be used in the GUI
	 * Eg. Source Port: Between 8000 and 9000 or Protocols: HTTP/HTTPS
	 */
	public String description();
}
