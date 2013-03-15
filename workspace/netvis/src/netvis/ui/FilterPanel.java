package netvis.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import netvis.visualizations.Visualization;

public class FilterPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected final List<Visualization> visList;
	public FilterPanel(final List<Visualization> visList) {
		this.visList = visList;
		
		JLabel titleVisLabel = new JLabel("Visualizations");
		titleVisLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		String[] visNameList = new String[visList.size()];
		for (int i=0; i < visList.size(); i++) visNameList[i] = visList.get(i).name();
		final JComboBox<String> visComboBox = new JComboBox<String>(visNameList);
		visComboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				visList.get(visComboBox.getSelectedIndex()).activate();
			}
			
		});
		visComboBox.setAlignmentX(LEFT_ALIGNMENT);
		
		JLabel titleLabel = new JLabel("Filters");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		// Create filter options
		JButton dummyButton = new JButton("Button");
		dummyButton.setAlignmentX(LEFT_ALIGNMENT);

		JCheckBox dummyCheckBox1 = new JCheckBox("CheckBox 1");
		dummyCheckBox1.setAlignmentX(LEFT_ALIGNMENT);
		JCheckBox dummyCheckBox2 = new JCheckBox("CheckBox 2");
		dummyCheckBox2.setAlignmentX(LEFT_ALIGNMENT);

		JRadioButton dummyRadioButton1 = new JRadioButton("Radio 1");
		dummyRadioButton1.setAlignmentX(LEFT_ALIGNMENT);
		JRadioButton dummyRadioButton2 = new JRadioButton("Radio 2");
		dummyRadioButton2.setAlignmentX(LEFT_ALIGNMENT);
		ButtonGroup dummyRadioGroup = new ButtonGroup();
		dummyRadioGroup.add(dummyRadioButton1);
		dummyRadioGroup.add(dummyRadioButton2);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(titleVisLabel);
		add(new JSeparator(SwingConstants.HORIZONTAL));

		add(visComboBox);
		add(titleLabel);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(dummyButton);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(dummyCheckBox1);
		add(dummyCheckBox2);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(dummyRadioButton1);
		add(dummyRadioButton2);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(Box.createVerticalGlue());
	}

}
