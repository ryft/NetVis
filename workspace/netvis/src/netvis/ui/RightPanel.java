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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import netvis.data.DataController;
import netvis.data.DataControllerListener;
import netvis.data.UndoController;
import netvis.data.model.Packet;
import netvis.data.model.PacketFilter;
import netvis.visualisations.Visualisation;
import netvis.visualisations.VisualisationsController;

@SuppressWarnings("serial")
public class RightPanel extends JPanel implements DataControllerListener {

	protected final DataController dataController;
	protected DataControlsContainer dataControls;

	// ID of the previous visualisation for deactivation
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
	 * @param contextPanel
	 *            Context panel for displaying each visualisation description
	 */
	public RightPanel(DataController dataController,
			VisControlsContainer visControlContainer, final ContextPanel contextPanel) {
		this.dataController = dataController;
		dataController.addListener(this);

		JLabel visualisationsTitle = new TitleLabel("Visualisations");
		JLabel visContainerTitle = new TitleLabel("Visualisation Controls");
		JLabel filtersTitle = new TitleLabel("Filters");
		JLabel dataTitle = new TitleLabel("Data Control");

		List<String> visNameList = VisualisationsController.GetInstance().getNList();
		final JComboBox<String> visComboBox = new JComboBox<String>(
				visNameList.toArray(new String[visNameList.size()]));

		visComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Visualisation newVis = 
					VisualisationsController.GetInstance().ActivateById(visComboBox.getSelectedIndex());
				contextPanel.update(newVis.getDescription());
				UndoController.INSTANCE.clearUndoStack();
			}
		});
		
		/* Set up undo controller */
		addThis(UndoController.INSTANCE.getPanel());
		
		/* Visualisation choice */
		addThis(visualisationsTitle);
		addThis(new JSeparator(SwingConstants.HORIZONTAL));
		addThis(visComboBox);

		/* Description button */
		JButton showDescription = new JButton("Show description");
		showDescription.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				contextPanel.update(VisualisationsController.GetInstance().getVList()
						.get(visComboBox.getSelectedIndex()).getDescription());
			}
		});
		addThis(showDescription);

		/** Visualisation controls */
		addThis(visContainerTitle);
		addThis(new JSeparator(SwingConstants.HORIZONTAL));
		addThis(visControlContainer);

		/** Filter controls */
		addThis(filtersTitle);
		addThis(new JSeparator(SwingConstants.HORIZONTAL));

		Iterator<PacketFilter> it = this.dataController.filterIterator();

		while (it.hasNext()) {
			addThis(it.next().getPanel());
		}
		

		// Add an update button that tells all filters to update
		JButton updateButton = new JButton("Apply filters");		
		it = this.dataController.filterIterator();
		while (it.hasNext()) {
			updateButton.addActionListener(it.next());
		}
		addThis(updateButton);
		add(Box.createVerticalStrut(10));

		/** Fixed filters from data controller */
		addThis(dataController.fixedFiltersPanel());
		
		/** Data controls */
		addThis(dataTitle);
		addThis(new JSeparator(SwingConstants.HORIZONTAL));
		dataControls = new DataControlsContainer();
		addThis(dataControls);
		dataControls.setPanel(dataController.getDataFeeder().controlPanel());

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalGlue());

	}
	
	protected void addThis(JComponent comp) {
		comp.setAlignmentX(LEFT_ALIGNMENT);
		add(comp);
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

	@Override
	public void allDataChanged(List<Packet> allPackets, int updateInterval,
			int intervalsComplete) {
		// this is fired when a new Data Feeder gets active, so update panel
		dataControls.setPanel(dataController.getDataFeeder().controlPanel());
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
	}

	@Override
	public void everythingEnds() {
	}

}
