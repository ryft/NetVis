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
import netvis.data.DataFeeder;
import netvis.data.model.PacketFilter;
import netvis.visualizations.Visualization;

@SuppressWarnings("serial")
public class RightPanel extends JPanel {

	protected final List<Visualization> visList;
	protected final DataController dataController;
	
	// ID of the previous visualisation for the deactivation purpose
	protected int oldVisId = -1;

	/**
	 * Panel to be displayed on the right side of the GUI. Shows components for
	 * controlling the current visualisation and filters.
	 * 
	 * @param visList
	 *            List of available visualisations to choose from
	 * @param dataController
	 *            Reference to the data controller
	 * @param visControlContainer
	 *            Container for the relevant visualisation controls
	 */
	public RightPanel(final List<Visualization> visList, DataFeeder dataFeeder, 
			DataController dataController, VisControlsContainer visControlContainer) {
		this.visList = visList;
		this.dataController = dataController;

		JLabel visualisationsTitle = new TitleLabel("Visualizations");
		JLabel visContainerTitle = new TitleLabel("Visualization Controls");
		JLabel filtersTitle = new TitleLabel("Filters");
		JLabel dataTitle = new TitleLabel("Data Control");
		
		String[] visNameList = new String[visList.size()];
		
		for (int i = 0; i < visList.size(); i++)
			visNameList[i] = visList.get(i).name();
		final JComboBox<String> visComboBox = new JComboBox<String>(visNameList);
		
		visComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If we are switching away from the visualizaton - deactivate it
				if (oldVisId != -1)
					visList.get (oldVisId).deactivate();
				oldVisId = visComboBox.getSelectedIndex();
				
				visList.get(visComboBox.getSelectedIndex()).activate();
			}
		});
		visComboBox.setAlignmentX(LEFT_ALIGNMENT);

		/** Visualisation choice */
		add(visualisationsTitle);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(visComboBox);

		/** Visualisation controls */
		add(visContainerTitle);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		visControlContainer.setAlignmentX(LEFT_ALIGNMENT);
		add(visControlContainer);

		/** Filter controls */
		add(filtersTitle);
		add(new JSeparator(SwingConstants.HORIZONTAL));

		Iterator<PacketFilter> it = this.dataController.filterIterator();

		while (it.hasNext()) {
			this.add(it.next().getPanel());
		}

		// Add an update button that tells all filters to update
		JButton updateButton = new JButton("Filter");
		it = this.dataController.filterIterator();
		while (it.hasNext()) {
			updateButton.addActionListener(it.next());
		}
		this.add(updateButton);
		add(Box.createVerticalStrut(10));
		
		/** Data controls */
		if (dataFeeder.controlPanel() != null) {
			add(dataTitle);
			add(new JSeparator(SwingConstants.HORIZONTAL));
			add(dataFeeder.controlPanel());
		}
		

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());

	}

	/**
	 * Nicely formatted title label
	 */
	protected class TitleLabel extends JLabel {
		public TitleLabel(String text) {
			super(text);
			setFont(new Font("SansSerif", Font.BOLD, 14));
		}
	}

}
