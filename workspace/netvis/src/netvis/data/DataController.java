package netvis.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
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
	private int noUpdated = 0;
	protected int intervalsComplete = 0;
	
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
	
	public void FinishEverything () {
		this.timer.stop();
		for (DataControllerListener l : listeners)
			l.everythingEnds();
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
		return Collections.unmodifiableList(filteredPackets);
	}

	/**
	 * The action of the timer.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		List<Packet> newPackets = dataFeeder.getNewPackets();
		
		if (newPackets != null) {
			allPackets.addAll(newPackets); // First add the new packets to the controller
			intervalsComplete++;
			
			applyFilters(newPackets); // Then apply the filters to them
			filteredPackets.addAll(newPackets);
	
			for (DataControllerListener l : listeners)
				l.newPacketsArrived(newPackets);
		}
		
		// If we've reached the end of the capture, stop the timer
		if (!dataFeeder.hasNext()) timer.stop();
	}
	
	// Redraw only when all the filters have applied their changes
	public void filterUpdated(){
		noUpdated++;
		if(noUpdated == filters.size()){
			allDataChanged();
			noUpdated = 0;
		}
	}
	
	/**
	 * 
	 * @param list List to be filtered
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
			l.allDataChanged(filteredPackets, timer.getDelay(), intervalsComplete);
		
		timer.start();
	}
	
	/**
	 * Sets a new data feeder and resets the state of the controller and listeners
	 * @param newDataFeeder	New data feeder
	 */
	public void setDataFeeder(DataFeeder newDataFeeder) {
		this.dataFeeder = newDataFeeder;
		this.allPackets.clear();
		allDataChanged();
	}

}
