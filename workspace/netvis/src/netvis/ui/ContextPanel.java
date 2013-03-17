package netvis.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import netvis.data.model.Packet;

/**
 * Context panel to display data in the bottom-right of the GUI and be updated
 * by the Analysis panel with relevant contextual data. Can be passed specific
 * data (e.g. a single packet) or a whole component (e.g. a table, chart or
 * graph) to show directly.
 * 
 * Contextual views won't update automatically by design; this would be a huge
 * performance drain. They can all be manually refreshed by clicking on the
 * relevant control again.
 */
@SuppressWarnings("serial")
public class ContextPanel extends JScrollPane {

	/**
	 * Empty context panel, containing only default user instructions
	 */
	public ContextPanel() {
		super(new JTextArea("Click on a button or blue element on the left while data is\n"
				+"available to see more details.\n\n"
				+"Contextual views don't update automatically for performance\n"
				+"reasons. They can all be manually refreshed by clicking on\n"
				+"the relevant control again."));
	}
	
	/**
	 * Update the context panel with a simple line graph, with the provided data points plotted
	 * @param title	graph title, displayed at the top
	 * @param dataPoints	uniformly distributed data points to plot on the y-axis
	 */
	public void update(String title, List<Integer> dataPoints) {
		Box graphWrapper = Box.createVerticalBox();
		graphWrapper.add(new JLabel(title));
		graphWrapper.add(new SimpleLineGraph(dataPoints));
		update(graphWrapper);
	}

	/**
	 * Update the context panel to display a comparison between the shortest- and longest-
	 * length packets received
	 * @param shortest	the shortest packet received so far
	 * @param longest	the longest packet received so far
	 */
	public void update(Packet shortest, Packet longest) {
		
		String description = "Shortest packet: \n" + 
		"Packet #" + shortest.no + " sent at " + shortest.time + "s over protocol " + shortest.protocol + "\n" +
		"Sender: " + shortest.sip + " [" + shortest.smac + "] on port " + shortest.sport + "\n" +
		"Recipient " + shortest.dip + " [" + shortest.dmac + "] on port " + shortest.dport + "\n" +
		"Packet consisted of " + shortest.length + " bytes" + "\n" +
		"Detected info: " + shortest.info + "\n\n" +
		"Longest packet: " + "\n" +
		"Packet #" + longest.no + " sent at " + longest.time + "s over protocol " + longest.protocol + "\n" +
		"Sender: " + longest.sip + " [" + longest.smac + "] on port " + longest.sport + "\n" +
		"Recipient " + longest.dip + " [" + longest.dmac + "] on port " + longest.dport + "\n" +
		"Packet consisted of " + longest.length + " bytes" + "\n" +
		"Detected info: " + longest.info;
		
		update(description);
	}
	
	/**
	 * Update the context panel with a string which is displayed in a JTextArea
	 * @param text	string to display
	 */
	public void update(String text) {
		JTextArea descriptionBox = new JTextArea(text);
		setViewportView(descriptionBox);
	}
	
	/**
	 * Update the context panel to display a traffic map, sorted by value
	 * @param title	title to show above the the sorted results
	 * @param unsortedMap	unsorted map of results to display
	 */
	public <T> void update(String title, Map<T, Integer> unsortedMap) {
		
		MapComparator<T> comparator = new MapComparator<T>(unsortedMap);
		TreeMap<T, Integer> sortedMap = new TreeMap<T, Integer>(comparator);
		sortedMap.putAll(unsortedMap);
		
		StringBuilder text = new StringBuilder(title);
		for (T entry : sortedMap.keySet())
			text.append("\n" + entry + ": \t" + String.valueOf(unsortedMap.get(entry)));
		update(text.toString());
	}

	/**
	 * Update the context panel to simply show a single JComponent
	 * @param component	the component to display
	 */
	public void update(JComponent component) {
		setViewportView(component);
	}
	
	/**
	 * Comparator for <T, Integer> maps which compares the integer values while ignoring the
	 * generic type T entries. Useful for sorting traffic data maps (traffic per port, protocol)
	 * @param <T>	map entry type
	 */
	protected class MapComparator<T> implements Comparator<T> {
		
		Map<T, Integer> base;
		
	    public MapComparator(Map<T, Integer> base) {
	        this.base = base;
	    }

		@Override
	    public int compare(T a, T b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        }
	    }
	}

}
