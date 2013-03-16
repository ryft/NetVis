package netvis;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import netvis.data.DataController;
import netvis.data.DataFeeder;
import netvis.data.SimDataFeeder;
import netvis.data.filters.PortRangeFilter;
import netvis.ui.AnalysisPanel;
import netvis.ui.FilterPanel;
import netvis.ui.OpenGLPanel;
import netvis.util.NetUtilities;
import netvis.visualizations.CopyOfTimePortVisualization;
import netvis.visualizations.TimePortVisualization;
import netvis.visualizations.Visualization;

/**
 * The entry point to the application. Glues the whole GUI together and
 * instantiates the domain components.
 */
@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

	// Declare panels for use in the GUI
	protected final OpenGLPanel glPanel;
	protected final FilterPanel filterPanel;
	protected final AnalysisPanel analysisPanel;
	protected final StatusBar statusBar;

	/**
	 * Construct a default application frame.
	 */
	public ApplicationFrame() {
		super("NetVis");

		// Setup data feeder and data controller
		DataFeeder dataFeeder = new SimDataFeeder("eduroam.csv", 1, this);
		DataController dataController = new DataController(dataFeeder, 500);
		dataController.addFilter(new PortRangeFilter(dataController));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		final JPanel contentPane = new JPanel(new GridBagLayout());

		// Just a frivolity -- let's make it look pretty
		this.setIconImage(new ImageIcon("img/icon.png").getImage());

		// Set up OpenGL window panel
		glPanel = new OpenGLPanel();
		final GridBagConstraints glConstraints = new GridBagConstraints();
		glConstraints.anchor = GridBagConstraints.CENTER;
		glConstraints.fill = GridBagConstraints.BOTH;
		glConstraints.gridx = 0;
		glConstraints.gridy = 0;
		glConstraints.weightx = 0.0;
		glConstraints.weighty = 0.0;
		contentPane.add(glPanel, glConstraints);

		List<Visualization> visList = new ArrayList<Visualization>();
		visList.add(new TimePortVisualization(dataController, glPanel));
		visList.add(new CopyOfTimePortVisualization(dataController, glPanel));
		visList.get(0).activate();

		// Set up filter control panel
		filterPanel = new FilterPanel(visList, dataController);
		final GridBagConstraints filterConstraints = new GridBagConstraints();
		filterConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		filterConstraints.fill = GridBagConstraints.NONE;
		filterConstraints.insets = new Insets(10, 5, 0, 10);
		filterConstraints.gridx = 1;
		filterConstraints.gridy = 0;
		filterConstraints.weightx = 1.0;
		filterConstraints.weighty = 0.0;
		contentPane.add(filterPanel, filterConstraints);

		// Set up table results panel
		analysisPanel = new AnalysisPanel();
		final GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.anchor = GridBagConstraints.NORTH;
		tableConstraints.fill = GridBagConstraints.BOTH;
		tableConstraints.insets = new Insets(5, 10, 0, 10);
		tableConstraints.gridx = 0;
		tableConstraints.gridy = 1;
		tableConstraints.gridwidth = 2;
		tableConstraints.weightx = 1.0;
		tableConstraints.weighty = 1.0;
		contentPane.add(analysisPanel, tableConstraints);

		// Set up a status bar panel
		statusBar = new StatusBar();
		final GridBagConstraints statusBarConstraints = new GridBagConstraints();
		statusBarConstraints.anchor = GridBagConstraints.NORTH;
		statusBarConstraints.fill = GridBagConstraints.NONE;
		statusBarConstraints.gridx = 0;
		statusBarConstraints.gridy = 2;
		statusBarConstraints.gridwidth = 2;
		statusBarConstraints.weightx = 0.0;
		statusBarConstraints.weighty = 0.0;
		contentPane.add(statusBar, statusBarConstraints);

		// Link the model together and set the content pane
		dataController.addListener(analysisPanel);
		setContentPane(contentPane);
		pack();
	}

	/**
	 * A simple status bar, which updates every second to display the current
	 * JVM memory usage, as well as the total memory available to the JVM (heap
	 * size limit).
	 */
	protected class StatusBar extends JPanel implements ActionListener {

		Timer timer = new Timer(1000, this);
		JLabel label = new JLabel("JMV memory usage statistics");
		Runtime runtime = Runtime.getRuntime();

		/**
		 * Create a new status bar JPanel which displays JVM memory usage
		 */
		public StatusBar() {
			add(label);
			timer.start();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// Get heap usage stats from the JVM Runtime object
			Long freeMemory = runtime.freeMemory();
			Long totalMemory = runtime.totalMemory();
			Long usedMemory = totalMemory - freeMemory;
			Long percentageUsed = Math.round(usedMemory * 100.0 / totalMemory);

			// Display the usage stats in increasingly bright red text as usage
			// approaches 100%
			if (percentageUsed >= 80)
				if (percentageUsed < 90)
					label.setForeground(Color.red.darker().darker());
				else
					label.setForeground(Color.red);
			else
				label.setForeground(Color.darkGray);

			label.setText("JVM memory usage statistics: " + NetUtilities.parseBytes(usedMemory)
					+ " / " + NetUtilities.parseBytes(totalMemory) + " (" + percentageUsed
					+ "%) in use");
		}
	}

	public static void main(String[] args) {
		ApplicationFrame applicationFrame = new ApplicationFrame();
		applicationFrame.setSize(applicationFrame.getPreferredSize());
		applicationFrame.setVisible(true);
	}

}
