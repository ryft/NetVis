package netvis.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class FilterPanel extends JPanel {
	
	public FilterPanel() {
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
		
		String[] comboBoxStrings = {"ComboBox 1", "ComboBox 2"};
		JComboBox dummyComboBox = new JComboBox(comboBoxStrings);
		dummyComboBox.setAlignmentX(LEFT_ALIGNMENT);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(dummyButton);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(dummyCheckBox1);
		add(dummyCheckBox2);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(dummyRadioButton1);
		add(dummyRadioButton2);
		add(new JSeparator(SwingConstants.HORIZONTAL));
		add(dummyComboBox);
		add(Box.createVerticalGlue());
	}

}
