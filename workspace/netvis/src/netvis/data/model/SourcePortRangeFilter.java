package netvis.data.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import netvis.data.DataController;

public class SourcePortRangeFilter implements PacketFilter, ActionListener{
	int lower_bound, upper_bound;
	final DataController dataController;
	JComponent filterPanel;
	JTextField lbField, ubField;
	JButton updateButton;
	public SourcePortRangeFilter(DataController dataController){
		this.dataController = dataController;
		lower_bound = 0;
		upper_bound = 70000;
		lbField = new JTextField(String.valueOf(lower_bound));
		ubField = new JTextField(String.valueOf(upper_bound));
		updateButton = new JButton("Filter");
		
		updateButton.addActionListener(this);
		JLabel titleLabel = new JLabel("Source Port");

		filterPanel = new JPanel();
		filterPanel.add(titleLabel);
		filterPanel.add(lbField);
		filterPanel.add(ubField);
		filterPanel.add(updateButton);
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
		filterPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
	}
	
	@Override
	public boolean filter(Packet packet) {
		if (lower_bound > packet.sport || upper_bound < packet.sport)
			return false;
		else 
			return true;
	}

	@Override
	public String description() {
		return "Filter by source port";
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
