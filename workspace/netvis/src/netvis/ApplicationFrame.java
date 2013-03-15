package netvis;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import netvis.data.DataController;
import netvis.data.DataFeeder;
import netvis.data.SimDataFeeder;
import netvis.data.model.SourcePortRangeFilter;
import netvis.ui.FilterPanel;
import netvis.ui.OpenGLPanel;
import netvis.ui.AnalysisPanel;
import netvis.visualizations.CopyOfTimePortVisualization;
import netvis.visualizations.TimePortVisualization;
import netvis.visualizations.Visualization;

public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final OpenGLPanel glPanel;
	private final FilterPanel filterPanel;
	private final AnalysisPanel analysisPanel;

	public ApplicationFrame() {
		super("NetVis");
		
		// Setup data feeder and data controller
		DataFeeder dataFeeder = new SimDataFeeder("eduroam.csv", 1, this);
		DataController dataController = new DataController(dataFeeder, 500);
		dataController.addFilter(new SourcePortRangeFilter(dataController));
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
		tableConstraints.insets = new Insets(5, 10, 10, 10);
		tableConstraints.gridx = 0;
		tableConstraints.gridy = 1;
		tableConstraints.gridwidth = 2;
		tableConstraints.weightx = 1.0;
		tableConstraints.weighty = 1.0;
		contentPane.add(analysisPanel, tableConstraints);

		// Link the model together and set the content pane
		dataController.addListener(analysisPanel);
		setContentPane(contentPane);
		pack();
	}

	public static void main(String[] args) {
		ApplicationFrame applicationFrame = new ApplicationFrame();
		applicationFrame.setSize(applicationFrame.getMinimumSize());
		applicationFrame.setVisible(true);
	}

}
