package netvis.visualisations;

import java.util.ArrayList;
import java.util.List;

import netvis.data.DataController;
import netvis.ui.ContextPanel;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.visualisations.comets.ActivityVisualisation;

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
		visList.add(new ActivityVisualisation(dataController, glPanel, visControlsContainer));
		visList.add(new TimePortVisualisation(dataController, glPanel, visControlsContainer));
		visList.add(new MulticubeVisualisation(dataController, glPanel, visControlsContainer));
		visList.add(new DataflowVisualisation(dataController, glPanel, visControlsContainer));
		visList.add(new TrafficVolumeVisualisation(dataController, glPanel, visControlsContainer));

		oldVisId = -1;

		for (int i = 0; i < visList.size(); i++)
			visNameList.add(visList.get(i).getName());
	}

	public void ActivateById(int selectedIndex, ContextPanel descriptionContainer) {
		// If we are switching away from the visualisation, deactivate it
		if (oldVisId != -1)
			visList.get(oldVisId).deactivate();
		oldVisId = selectedIndex;

		Visualisation newVis = visList.get(selectedIndex);
		descriptionContainer.update(newVis.getDescription());
		newVis.activate();
		newVis.requestFocusInWindow();
	};
};