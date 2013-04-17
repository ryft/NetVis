package netvis.visualizations;

import java.util.ArrayList;
import java.util.List;

import netvis.data.DataController;
import netvis.ui.OpenGLPanel;
import netvis.ui.VisControlsContainer;
import netvis.visualizations.comets.ActivityVisualisation;

public class VisualizationsController {
	
	// Singleton pattern
	private static VisualizationsController INSTANCE = new VisualizationsController();
	public static VisualizationsController GetInstance() {return INSTANCE;}
	
	protected List<Visualization> visList;
	protected List<String>        visNameList;
	public List<Visualization> 	getVList () {return visList;};
	public List<String>			getNList () {return visNameList;};
	
	// Old visualization id
	int oldVisId;
	
	private VisualizationsController ()
	{
		// Set up references to all visualizations
		visList = new ArrayList<Visualization>();
		visNameList = new ArrayList<String> ();
	}
	
	public void InitializeAll (DataController dataController, OpenGLPanel glPanel, VisControlsContainer visControlsContainer)
	{
		visList.add (new ActivityVisualisation		(dataController, glPanel, visControlsContainer));
		visList.add (new TimePortVisualization		(dataController, glPanel, visControlsContainer));
		visList.add (new DummyVisualization			(dataController, glPanel, visControlsContainer));
		visList.add (new MulticubeVisualization		(dataController, glPanel, visControlsContainer));
		visList.add (new DataflowVisualization		(dataController, glPanel, visControlsContainer));
		visList.add (new TrafficVolumeVisualization	(dataController, glPanel, visControlsContainer));
		
		oldVisId = -1;
		
		for (int i = 0; i < visList.size(); i++)
			visNameList.add (visList.get(i).name());
	}
	
	public void ActivateById (int selectedIndex) {
		// If we are switching away from the visualizaton - deactivate it
		if (oldVisId != -1)
			visList.get (oldVisId).deactivate();
		oldVisId = selectedIndex;
		
		visList.get(selectedIndex).activate();
		visList.get(selectedIndex).requestFocusInWindow();
	};
};
