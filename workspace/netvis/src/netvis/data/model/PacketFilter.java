package netvis.data.model;

import javax.swing.JComponent;

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
	
	/**
	 * 
	 * @return The JComponent that controls this filter.
	 * This class should also act as a controller for the JComponent
	 */
	public JComponent getPanel();
}
