package netvis.visualisations;

import java.util.ArrayList;
import java.util.List;

import netvis.data.DataController;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.visualisations.comets.ActivityVisualisation;
import netvis.visualisations.comets.HeatmapVisualisation;

public class VisualisationsController {

	// Singleton pattern
	private static VisualisationsController INSTANCE = new VisualisationsController();

	public static VisualisationsController GetInstance() {
		return INSTANCE;
	}

	protected List<Visualisation> visList;
	protected List<String> visNameList;

	public List<Visualisation> getVList() {
		return visList;
	};

	public List<String> getNList() {
		return visNameList;
	};

	// Old visualisation id
	int oldVisId;

	private VisualisationsController() {
		// Set up references to all visualisations
		visList = new ArrayList<Visualisation>();
		visNameList = new ArrayList<String>();
	}

	public void InitializeAll(DataController dataController, OpenGLPanel glPanel,
			VisControlsContainer visControlsContainer) {
		
		// Default visualisation: attribute distribution
		visList.add(new DistributionVisualisation(dataController, glPanel, visControlsContainer));

		// Traffic volume visualisation
		visList.add(new TrafficVolumeVisualisation(dataController, glPanel, visControlsContainer));

		// Map visualisations
		visList.add(new ActivityVisualisation(dataController, glPanel, visControlsContainer));
		visList.add(new HeatmapVisualisation(dataController, glPanel, visControlsContainer));

		// Data flow visualisation
		visList.add(new DataflowVisualisation(dataController, glPanel, visControlsContainer));

		// Spinning cube of doom visualisation
		visList.add(new MulticubeVisualisation(dataController, glPanel, visControlsContainer));

		oldVisId = -1;

		for (int i = 0; i < visList.size(); i++)
			visNameList.add(visList.get(i).getName());
	}

	public Visualisation ActivateById(int selectedIndex) {
		// If we are switching away from the visualisation, deactivate it
		if (oldVisId != -1)
			visList.get(oldVisId).deactivate();
		oldVisId = selectedIndex;

		Visualisation newVis = visList.get(selectedIndex);
		// descriptionContainer.update(newVis.getDescription());
		newVis.activate();
		newVis.requestFocusInWindow();
		return newVis;
	}

	public void ActivateByReference(Visualisation visualisation) {
		ActivateById(visList.indexOf(visualisation));
	};

};
