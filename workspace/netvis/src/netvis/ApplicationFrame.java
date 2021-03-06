package netvis;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import netvis.data.DataController;
import netvis.data.DataFeeder;
import netvis.data.DummyDataFeeder;
import netvis.data.csv.CSVDataFeeder;
import netvis.data.filters.IPFilter;
import netvis.data.filters.MACFilter;
import netvis.data.filters.PortRangeFilter;
import netvis.data.filters.ProtocolFilter;
import netvis.ui.AnalysisPanel;
import netvis.ui.ContextPanel;
import netvis.ui.OpenGLPanel;
import netvis.ui.RightPanel;
import netvis.ui.VisControlsContainer;
import netvis.util.ExceptionHandler;
import netvis.util.Utilities;
import netvis.visualisations.VisualisationsController;

/**
 * The entry point to the application. Glues the whole GUI together and
 * instantiates the domain components.
 */
@SuppressWarnings("serial")
public class ApplicationFrame extends JFrame {

	protected final String versionNumber = "1.0.4";

	// Flags governing the behaviour of the application window
	/**
	 * Debug Mode disables the global exception handler and initialises a CSV
	 * data feeder on construction.
	 */
	protected final boolean DEBUG_MODE = false;
	protected boolean FULL_SCREEN = false;

	// Declare panels for use in the GUI
	protected final ApplicationFrame parent = this;
	protected final JPanel contentPane;
	protected final OpenGLPanel glPanel;
	protected final RightPanel rightPanel;
	protected final JSplitPane bottomPanel;
	protected final AnalysisPanel analysisPanel;
	protected final ContextPanel contextPanel;
	protected StatusBar statusBar;
	protected final JMenuBar menuBar;

	protected DataFeeder dataFeeder;
	protected DataController dataController;

	VisControlsContainer visControlsContainer;

	/**
	 * Construct a default application frame.
	 */
	public ApplicationFrame() {
		super("Network Visualiser by the Clockwork Dragon team");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				// System.out.println(event);

				// Do whatever you can to stop everything
				dataController.FinishEverything();
				System.exit(0);
				Runtime.getRuntime().exit(0);
			}
		});

		if (DEBUG_MODE)
			dataFeeder = new CSVDataFeeder(new File("../../csv/captures/combined.csv"),
					ApplicationFrame.this);
		else
			dataFeeder = new DummyDataFeeder(ApplicationFrame.this);

		dataController = new DataController(dataFeeder, 1000);
		dataController.addFilter(new ProtocolFilter(dataController));
		dataController.addFilter(new PortRangeFilter(dataController));
		dataController.addFilter(new IPFilter(dataController, this));
		dataController.addFilter(new MACFilter(dataController, this));

		contentPane = new JPanel(new GridBagLayout());

		// Just a frivolity -- let's make it look pretty
		this.setIconImage(new ImageIcon("img/icon.png").getImage());

		// Set up OpenGL window panel
		glPanel = new OpenGLPanel();
		final GridBagConstraints glConstraints = new GridBagConstraints();
		glConstraints.anchor = GridBagConstraints.CENTER;
		glConstraints.fill = GridBagConstraints.BOTH;
		glConstraints.gridx = 0;
		glConstraints.gridy = 0;
		glConstraints.weightx = 1.0;
		glConstraints.weighty = 1.0;
		contentPane.add(glPanel, glConstraints);

		visControlsContainer = new VisControlsContainer();

		// Set up all the visualisations
		VisualisationsController.GetInstance().InitializeAll(dataController, glPanel,
				visControlsContainer);

		// Set up an bottom panel for analysis and context panels
		bottomPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		contextPanel = new ContextPanel();
		analysisPanel = new AnalysisPanel(100, contextPanel);
		bottomPanel.setLeftComponent(analysisPanel);
		bottomPanel.setRightComponent(contextPanel);
		bottomPanel.setResizeWeight(0.85);

		final GridBagConstraints bottomConstraints = new GridBagConstraints();
		bottomConstraints.anchor = GridBagConstraints.NORTH;
		bottomConstraints.fill = GridBagConstraints.BOTH;
		bottomConstraints.insets = new Insets(5, 10, 0, 10);
		bottomConstraints.gridx = 0;
		bottomConstraints.gridy = 1;
		bottomConstraints.gridwidth = 2;
		bottomConstraints.weightx = 0.0;
		bottomConstraints.weighty = 0.0;
		contentPane.add(bottomPanel, bottomConstraints);

		// Set up filter control panel
		rightPanel = new RightPanel(dataController, visControlsContainer, contextPanel);
		final GridBagConstraints rightConstraints = new GridBagConstraints();
		rightConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		rightConstraints.fill = GridBagConstraints.NONE;
		rightConstraints.insets = new Insets(10, 5, 0, 10);
		rightConstraints.gridx = 1;
		rightConstraints.gridy = 0;
		rightConstraints.weightx = 0.0;
		rightConstraints.weighty = 1.0;
		contentPane.add(rightPanel, rightConstraints);

		// Set up a status bar panel
		if (DEBUG_MODE) {
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
		}

		// Link the model together and set the content pane
		dataController.addListener(analysisPanel);
		setContentPane(contentPane);
		menuBar = createMenuBar();
		setJMenuBar(menuBar);
		pack();

		// Focus on the simulation
		visControlsContainer.setFocusable(true);
		visControlsContainer.requestFocusInWindow();

		// Please don't change this to anything other than 0 as the drop-down
		// list doesn't update correctly. To change the default visualisation,
		// reorder them in VisualisationController.
		VisualisationsController.GetInstance().ActivateById(0);

		// Reset the initial message in the context panel
		contextPanel.revert();

		// Add a resize listener
		this.addComponentListener(new ResizeListener());
		this.setPreferredSize(new Dimension(1080, 720));
		this.setLocationByPlatform(true);

		// Register a nice exception handler
		if (!DEBUG_MODE)
			Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(parent));

	}

	public JMenuBar createMenuBar() {

		// Set up menu bar + helper objects
		JMenuBar menuBar = new JMenuBar();
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("CSV packet capture file", "csv"));

		// File menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription(
				"File menu for managing the application state");

		// Open a CSV file
		JMenuItem openCSVItem = new JMenuItem("Open CSV...", KeyEvent.VK_O);
		openCSVItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openCSVItem.getAccessibleContext().setAccessibleDescription("Open a CSV data source");

		// Listen for open CSV events
		openCSVItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					openCSV(fileChooser.getSelectedFile());
				}
			}
		});

		// Exit the application
		JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		exitItem.getAccessibleContext().setAccessibleDescription("Exit the application");

		// Listen for exit events
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// View menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		viewMenu.getAccessibleContext().setAccessibleDescription(
				"View menu for controlling the view parameters of the application");

		// Reset the application size
		JMenuItem resetViewItem = new JMenuItem("Reset Size", KeyEvent.VK_R);
		resetViewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		resetViewItem.getAccessibleContext().setAccessibleDescription(
				"Reset the size of the application window");

		// Listen for reset events
		resetViewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (FULL_SCREEN) {
					ApplicationFrame.this.toggleFullScreen();
				}
			}
		});

		// Make the application full-screen
		JMenuItem fullScreenItem = new JMenuItem("Full Screen", KeyEvent.VK_F);
		fullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		fullScreenItem.getAccessibleContext().setAccessibleDescription("Toggle full-screen window");

		// Listen for full-screen events
		fullScreenItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationFrame.this.toggleFullScreen();
			}
		});

		// Toggle the right panel
		JMenuItem toggleRightItem = new JMenuItem("Toggle Right Panel", KeyEvent.VK_RIGHT);
		toggleRightItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
				ActionEvent.CTRL_MASK));
		toggleRightItem.getAccessibleContext().setAccessibleDescription("Toggle right panel");

		// Listen for toggle events
		toggleRightItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Dimension d = ApplicationFrame.this.glPanel.getSize();
				int deltaW = 0;

				if (ApplicationFrame.this.rightPanel.isVisible()) {
					ApplicationFrame.this.rightPanel.setVisible(false);
					deltaW += ApplicationFrame.this.rightPanel.getWidth();
				} else {
					ApplicationFrame.this.rightPanel.setVisible(true);
					deltaW -= ApplicationFrame.this.rightPanel.getWidth();
				}

				ApplicationFrame.this.glPanel.setSize(d.width + deltaW, d.height);
				ApplicationFrame.this.componentResized();
			}
		});

		// Toggle the right panel
		JMenuItem toggleBottomItem = new JMenuItem("Toggle Bottom Panel", KeyEvent.VK_DOWN);
		toggleBottomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
				ActionEvent.CTRL_MASK));
		toggleBottomItem.getAccessibleContext().setAccessibleDescription("Toggle bottom panel");

		// Listen for toggle events
		toggleBottomItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Dimension d = ApplicationFrame.this.glPanel.getSize();
				int deltaH = 0;

				if (ApplicationFrame.this.bottomPanel.isVisible()) {
					ApplicationFrame.this.bottomPanel.setVisible(false);
					deltaH += ApplicationFrame.this.bottomPanel.getHeight();
				} else {
					ApplicationFrame.this.bottomPanel.setVisible(true);
					deltaH -= ApplicationFrame.this.bottomPanel.getHeight();
				}

				ApplicationFrame.this.glPanel.setSize(d.width, d.height + deltaH);
				ApplicationFrame.this.componentResized();
			}
		});

		// Help menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.getAccessibleContext().setAccessibleDescription(
				"Help menu for viewing information about the application");

		// View the 'about' box
		JMenuItem aboutItem = new JMenuItem("About...", KeyEvent.VK_A);
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		aboutItem.getAccessibleContext().setAccessibleDescription(
				"View information about the application");
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(ApplicationFrame.this, "NetVis Version "
						+ versionNumber + "\n" + "\n" + "Authors:" + "\n" + "James Nicholls" + "\n"
						+ "Dominik Peters" + "\n" + "Albert Slawinski" + "\n" + "Thomas Spoor"
						+ "\n" + "Sergiu Vicol" + "\n\n" + "Copyright 2013 Clockwork Dragon",
						"About", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("img/icon.png"));
			}
		});

		// Put it together
		fileMenu.add(openCSVItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		viewMenu.add(resetViewItem);
		viewMenu.add(fullScreenItem);
		viewMenu.add(toggleRightItem);
		viewMenu.add(toggleBottomItem);
		menuBar.add(viewMenu);

		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);

		return menuBar;
	}

	/**
	 * Open a CSV packet trace file and play it.
	 * 
	 * @param file
	 *            The reference to the file
	 */
	public void openCSV(File file) {
		dataFeeder = new CSVDataFeeder(file, parent);
		dataController.setDataFeeder(dataFeeder);
	}

	public boolean isFullScreen() {
		return FULL_SCREEN;
	}

	public void toggleFullScreen() {

		// Get references to required system resources
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();

		if (FULL_SCREEN) // Attempt to go full-screen
			try {
				parent.dispose();
				parent.setUndecorated(false);
				parent.setResizable(true);
				device.setFullScreenWindow(null);
				parent.setExtendedState(JFrame.NORMAL);
				parent.setVisible(true);
				FULL_SCREEN = false;
			} catch (Exception ex) {
				parent.setVisible(true);
			}

		else
			// Attempt to revert to normal window
			try {
				parent.dispose();
				parent.setUndecorated(true);
				parent.setResizable(false);
				device.setFullScreenWindow(parent);
				parent.setExtendedState(JFrame.MAXIMIZED_BOTH);
				parent.validate();
				FULL_SCREEN = true;
			} catch (Exception ex) {
				parent.setVisible(true);
			}

		componentResized();

	}

	/**
	 * A simple status bar, which updates every second to display the current
	 * JVM memory usage, as well as the total memory available to the JVM (heap
	 * size limit).
	 */
	protected class StatusBar extends JPanel implements ActionListener, MouseListener {

		Timer timer = new Timer(1000, this);
		JLabel label = new JLabel("JVM memory usage statistics loading...");
		Runtime runtime = Runtime.getRuntime();
		Long prevUsage = 0l;

		/**
		 * Create a new status bar JPanel which displays JVM memory usage
		 */
		public StatusBar() {
			add(label);
			timer.start();
			addMouseListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (analysisPanel.batchProcessBlock) {
				label.setForeground(Color.darkGray);
				label.setText("Processing filtered data...");
				label.setToolTipText("Click to perform a garbage collection.");

			} else {
				// Get heap usage stats from the JVM Runtime object
				Long freeMemory = runtime.freeMemory();
				Long totalMemory = runtime.totalMemory();
				Long usedMemory = totalMemory - freeMemory;
				Long percentageUsed = Math.round(usedMemory * 100.0 / totalMemory);

				// Display the usage stats in increasingly bright red text as
				// usage approaches 100%
				if (percentageUsed >= 80)
					if (percentageUsed < 90)
						label.setForeground(Color.red.darker().darker());
					else
						label.setForeground(Color.red);
				else
					label.setForeground(Color.darkGray);

				// Show green text briefly after a garbage collection (usage
				// drops)
				if (usedMemory < prevUsage)
					label.setForeground(Color.green.darker().darker());

				label.setText("JVM memory usage statistics: " + Utilities.parseBytes(usedMemory)
						+ " / " + Utilities.parseBytes(totalMemory) + " (" + percentageUsed
						+ "%) in use");

				prevUsage = usedMemory;
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// Attempt to garbage collect when the status label is clicked
			System.gc();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			setCursor(Cursor.getDefaultCursor());
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Make GUI OS native:
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					// for (LookAndFeelInfo info :
					// UIManager.getInstalledLookAndFeels()) {
					// if ("Nimbus".equals(info.getName())) {
					// UIManager.setLookAndFeel(info.getClassName());
					// break;
					// }
					// }
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					ApplicationFrame applicationFrame = new ApplicationFrame();
					applicationFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
					applicationFrame.setSize(applicationFrame.getPreferredSize());
					applicationFrame.setVisible(true);
					applicationFrame.analysisPanel.init();
				} catch (Exception ex) {
					// We have a problem - app can't run
					ex.printStackTrace();
				}
			}
		});

	}

	protected void componentResized() {
		glPanel.resizeVisualisation();
	}

	protected class ResizeListener implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent arg0) {
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			ApplicationFrame.this.componentResized();
		}
	}

}