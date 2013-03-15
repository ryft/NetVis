package netvis.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import netvis.data.DataController;
import netvis.data.model.PacketFilter;
import netvis.visualizations.Visualization;

public class FilterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected final List<Visualization> visList;
	protected final DataController dataController;
	public FilterPanel(final List<Visualization> visList, DataController dataController) {
		this.visList = visList;
		this.dataController = dataController;
		
		
		JLabel titleVisLabel = new JLabel("Visualizations");
		titleVisLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		String[] visNameList = new String[visList.size()];
		for (int i=0; i < visList.size(); i++) visNameList[i] = visList.get(i).name();
		final JComboBox<String> visComboBox = new JComboBox<String>(visNameList);
		visComboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				visList.get(visComboBox.getSelectedIndex()).activate();
			}
		});
		visComboBox.setAlignmentX(LEFT_ALIGNMENT);
		
		JLabel titleLabel = new JLabel("Filters");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		
		
		add(titleVisLabel);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(visComboBox);
		add(titleLabel);
		add(new JSeparator(SwingConstants.HORIZONTAL));

		Iterator<PacketFilter> it = this.dataController.filterIterator(); 

		while (it.hasNext()){
			this.add(it.next().getPanel());
		}
		
		// Add an update button that tells all filters to update
		JButton updateButton = new JButton("Filter");
		it = this.dataController.filterIterator();
		while (it.hasNext()) {
			updateButton.addActionListener(it.next());
		}
		this.add(updateButton);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());


	}

}
