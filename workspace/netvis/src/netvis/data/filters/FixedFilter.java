package netvis.data.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import netvis.data.model.PacketFilter;

public abstract class FixedFilter implements PacketFilter{

	protected final JPanel panel;
	protected final JLabel filterText;
	protected final JButton closeButton;
	protected FixedFilter(String text){
		panel = new JPanel();
		filterText = new JLabel(text);
		
		closeButton = new JButton("X");
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clearFilter();
			}
		});
		
		Box box = Box.createHorizontalBox();
		box.add(filterText);
		box.add(closeButton);
		panel.add(box);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
	}
	protected abstract void clearFilter();
	@Override
	public void actionPerformed(ActionEvent arg0) {}
	@Override
	public String description() {
		return null;
	}

	@Override
	public JComponent getPanel() {
		return panel;
	}

}
