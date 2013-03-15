package netvis.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;

import netvis.data.model.Packet;
import netvis.data.model.PacketFilter;

public class DataController implements ActionListener {
	DataFeeder dataFeeder;
	Timer timer;
	final List<DataControllerListener> listeners;
	final List<PacketFilter> filters;
	final List<Packet> allPackets;
	final List<Packet> filteredPackets;
	
	/**
	 * @param dataFeeder 
	 * @param updateInterval The time interval at which the data is updating.
	 */
	public DataController(DataFeeder dataFeeder, int updateInterval){
		this.dataFeeder = dataFeeder;
		listeners = new ArrayList<DataControllerListener>();
		filters = new ArrayList<PacketFilter>();
		filteredPackets = new ArrayList<Packet>();

		allPackets = new ArrayList<Packet>();
		timer = new Timer(updateInterval, this);
		timer.start();
	}
	
	public void addListener(DataControllerListener listener){
		listeners.add(listener);
	}
	public void removeListener(DataControllerListener listener){
		listeners.remove(listener);
	}
	
	public void addFilter(PacketFilter packetFilter){
		filters.add(packetFilter);
		allDataChanged();
	}
	
	public void removeFilter(PacketFilter packetFilter){
		filters.remove(packetFilter);
		allDataChanged();
	}
	public Iterator<PacketFilter> filterIterator(){
		return filters.iterator();
	}
	
	/**
	 * Returns all the packets with the filters applied
	 */
	public List<Packet> getPackets(){
		return filteredPackets;
	}

	/**
	 * The action of the timer.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		List<Packet> newPackets = dataFeeder.getNewPackets();
		allPackets.addAll(newPackets); // First add the new packets to the controller
		
		applyFilters(newPackets); // Then apply the filters to them
		filteredPackets.addAll(newPackets);

		for (DataControllerListener l : listeners)
			l.newPacketsArrived(newPackets);
		
		// If we've reached the end of the capture, stop the timer
		if (!dataFeeder.hasNext()) timer.stop();
	}
	
	public void filterUpdated(){
		allDataChanged();
	}
	
	/**
	 * 
	 * @param list List to be filtered
	 * @return New list containing only the packets that pass the test
	 */
	private void applyFilters(List<Packet> list){
		List<Packet> toBeRemoved = new ArrayList<Packet>();
		for (PacketFilter f:filters)
			for(Packet p:list)
				if (!f.filter(p))
					toBeRemoved.add(p);
		list.removeAll(toBeRemoved);
	}
	
	/**
	 * Informs listeners that all the data has changed.
	 */
	private void allDataChanged() {
		filteredPackets.clear();
		filteredPackets.addAll(allPackets);
		applyFilters(filteredPackets);
		
		for (DataControllerListener l : listeners)
			l.allDataChanged(filteredPackets);
	}

}
