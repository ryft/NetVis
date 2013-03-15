package netvis.ui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import netvis.data.model.Packet;

/**
 * Context panel to display data in the bottom-right of the GUI and be updated
 * by the Analysis panel with relevant contextual data. Can be passed specific
 * data (e.g. a single packet) or a whole component (e.g. a table, chart or
 * graph) to show directly.
 */
public class ContextPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JPanel wrapper;
	JScrollPane scrollWrapper = new JScrollPane(new JLabel("Click on a data element on the left to see more details."));

	public ContextPanel() {
		add(scrollWrapper);
		scrollWrapper.setSize(getSize());
	}

	public void update(Packet shortest, Packet longest) {
		removeAll();
		wrapper = new JPanel(new GridLayout(13, 1));
		
		wrapper.add(new JLabel("Example shortest packet: "));
		wrapper.add(new JLabel("Packet #" + shortest.no));
		wrapper.add(new JLabel("Sent by " + shortest.sip + " [" + shortest.smac + "] on port " + shortest.sport));
		wrapper.add(new JLabel("To recipient " + shortest.dip + " [" + shortest.dmac + "] on port " + shortest.dport));
		wrapper.add(new JLabel("At " + shortest.time + "s over protocol " + shortest.protocol + " (" + shortest.length + " bytes)"));
		wrapper.add(new JLabel("Detected info: " + shortest.info));
		wrapper.add(new JLabel());
		wrapper.add(new JLabel("Example longest packet: "));
		wrapper.add(new JLabel("Packet #" + longest.no));
		wrapper.add(new JLabel("Sent by " + longest.sip + " [" + longest.smac + "] on port " + longest.sport));
		wrapper.add(new JLabel("To recipient " + longest.dip + " [" + longest.dmac + "] on port " + longest.dport));
		wrapper.add(new JLabel("At " + longest.time + "s over protocol " + longest.protocol + " (" + longest.length + " bytes)"));
		wrapper.add(new JLabel("Detected info: " + longest.info));
		
		scrollWrapper = new JScrollPane(wrapper);
		add(scrollWrapper);
	}

	public void update(JComponent component) {
		removeAll();
		scrollWrapper = new JScrollPane(component);
		add(scrollWrapper);
	}

}
