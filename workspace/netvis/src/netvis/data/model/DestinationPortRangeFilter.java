package netvis.data.model;

import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import netvis.data.DataController;

public class DestinationPortRangeFilter implements PacketFilter{
	int lower_bound, upper_bound;
	final DataController dataController;
	JComponent filterPanel;
	JTextField lbField, ubField;
	public DestinationPortRangeFilter(DataController dataController){
		this.dataController = dataController;
		lower_bound = 0;
		upper_bound = 65535;	// Highest possible UDP & TCP port
		lbField = new JTextField(String.valueOf(lower_bound));
		ubField = new JTextField(String.valueOf(upper_bound));

		JLabel titleLabel = new JLabel("Destination Port");

		filterPanel = new JPanel();
		filterPanel.add(titleLabel);
		filterPanel.add(lbField);
		filterPanel.add(ubField);
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
		filterPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
	}
	
	@Override
	public boolean filter(Packet packet) {
		if (lower_bound > packet.dport || upper_bound < packet.dport)
			return false;
		else 
			return true;
	}

	@Override
	public String description() {
		return "Filter by destination port";
	}

	@Override
	public JComponent getPanel() {
		return filterPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		lower_bound = Integer.parseInt(lbField.getText());
		upper_bound = Integer.parseInt(ubField.getText());
		dataController.filterUpdated();
	}

}
