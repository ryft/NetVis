package netvis.data.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import netvis.data.DataController;
import netvis.data.DataUtilities;
import netvis.data.model.Packet;
import netvis.data.model.PacketFilter;

/**
 * <pre>
 * Port filter
 * Allows the user to specify a range of source/destination ports
 * Packets with source ports outside the source port range will not be shown
 * Packets with destination ports outside the destination port range will not be shown
 */
public class PortRangeFilter implements PacketFilter {
	int source_lower_bound, source_upper_bound, dest_lower_bound, dest_upper_bound;
	final DataController dataController;
	JComponent filterPanel;
	JTextField slbField, subField, dlbField, dubField;
	JButton updateButton;

	public PortRangeFilter(DataController dataController) {
		this.dataController = dataController;
		source_lower_bound = DataUtilities.MIN_PORT;
		source_upper_bound = DataUtilities.MAX_PORT; // Highest possible UDP & TCP port
		dest_lower_bound = DataUtilities.MIN_PORT;
		dest_upper_bound = DataUtilities.MAX_PORT;

		slbField = new JTextField(String.valueOf(source_lower_bound));
		slbField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		subField = new JTextField(String.valueOf(source_upper_bound));
		subField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		dlbField = new JTextField(String.valueOf(dest_lower_bound));
		dlbField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		dubField = new JTextField(String.valueOf(dest_upper_bound));
		dubField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		JLabel stitleLabel = new JLabel("Source Port");
		stitleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		JLabel dtitleLabel = new JLabel("Destination Port");
		dtitleLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		filterPanel = new JPanel();
		Box box = Box.createHorizontalBox();
		filterPanel.add(stitleLabel);
		box.add(slbField);
		box.add(subField);

		filterPanel.add(box);
		box = Box.createHorizontalBox();
		filterPanel.add(dtitleLabel);
		box.add(dlbField);
		box.add(dubField);
		filterPanel.add(box);

		// Add a button to reset port range
		JButton resetButton = new JButton("Reset port range");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				slbField.setText(Integer.toString(DataUtilities.MIN_PORT));
				subField.setText(Integer.toString(DataUtilities.MAX_PORT));
				dlbField.setText(Integer.toString(DataUtilities.MIN_PORT));
				dubField.setText(Integer.toString(DataUtilities.MAX_PORT));
			}
		});
		resetButton.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		filterPanel.add(resetButton);

		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
	}

	@Override
	public boolean filter(Packet packet) {
		if (source_lower_bound > packet.sport || source_upper_bound < packet.sport
				|| dest_lower_bound > packet.dport || dest_upper_bound < packet.dport)
			return false;
		else
			return true;
	}

	@Override
	public String description() {
		return "Filter by ports";
	}

	@Override
	public JComponent getPanel() {
		return filterPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		source_lower_bound = Integer.parseInt(slbField.getText());
		source_upper_bound = Integer.parseInt(subField.getText());
		dest_lower_bound = Integer.parseInt(dlbField.getText());
		dest_upper_bound = Integer.parseInt(dubField.getText());
		dataController.filterUpdated();
	}

}
