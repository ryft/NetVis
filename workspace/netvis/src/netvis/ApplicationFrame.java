package netvis;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import netvis.ui.FilterPanel;
import netvis.ui.OpenGLPanel;
import netvis.ui.TablePanel;

public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final OpenGLPanel glPanel;
	private final FilterPanel filterPanel;
	private final TablePanel tablePanel;

	public ApplicationFrame() {

		super("NetVis");
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

		// Set up filter control panel
		filterPanel = new FilterPanel();
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
		tablePanel = new TablePanel();
		final GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.anchor = GridBagConstraints.NORTH;
		tableConstraints.fill = GridBagConstraints.BOTH;
		tableConstraints.insets = new Insets(5, 10, 10, 10);
		tableConstraints.gridx = 0;
		tableConstraints.gridy = 1;
		tableConstraints.gridwidth = 2;
		tableConstraints.weightx = 1.0;
		tableConstraints.weighty = 1.0;
		contentPane.add(tablePanel, tableConstraints);

		// Set the content pane
		setContentPane(contentPane);
		pack();
	}

	public static void main(String[] args) {
		ApplicationFrame applicationFrame = new ApplicationFrame();
		
		// Dirty trick to set a reasonable size
		Dimension frameSize = applicationFrame.getMinimumSize();
		frameSize.setSize(frameSize.getWidth(), frameSize.getHeight() + 100);
		applicationFrame.setSize(frameSize);
		applicationFrame.setVisible(true);
	}

}