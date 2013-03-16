package netvis.data.filters;

import java.awt.event.ActionEvent;

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

public class PortRangeFilter implements PacketFilter {
	int source_lower_bound, source_upper_bound, dest_lower_bound, dest_upper_bound;
	final DataController dataController;
	JComponent filterPanel;
	JTextField slbField, subField, dlbField, dubField;
	JButton updateButton;
	public PortRangeFilter(DataController dataController){
		this.dataController = dataController;
		source_lower_bound = 0;
		source_upper_bound = 65535;	// Highest possible UDP & TCP port
		dest_lower_bound = 0;
		dest_upper_bound = DataUtilities.MAX_PORT; 
		slbField = new JTextField(String.valueOf(source_lower_bound));
		subField = new JTextField(String.valueOf(source_upper_bound));

		dlbField = new JTextField(String.valueOf(dest_lower_bound));
		dubField = new JTextField(String.valueOf(dest_upper_bound));

		JLabel stitleLabel = new JLabel("Source Port");
		JLabel dtitleLabel = new JLabel("Destination Port");

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

		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
		filterPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
	}
	
	@Override
	public boolean filter(Packet packet) {
		if (source_lower_bound > packet.sport || source_upper_bound < packet.sport ||
			dest_lower_bound > packet.dport || dest_upper_bound < packet.dport)
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
