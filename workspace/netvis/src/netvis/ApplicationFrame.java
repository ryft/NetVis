package netvis;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import netvis.ui.FilterPanel;

public class ApplicationFrame extends JFrame {
	
	private final FilterPanel filterPanel;

	public ApplicationFrame() {		
		super("NetVis");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		filterPanel = new FilterPanel();
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(BorderLayout.EAST, filterPanel);
		
		// Set and organise the content pane
		setContentPane(contentPane);
		setResizable(false);
		setLocation(100,100);
		pack();
	}
	
	public static void main(String[] args) {
		ApplicationFrame applicationFrame = new ApplicationFrame();
		applicationFrame.setVisible(true);
	}

}