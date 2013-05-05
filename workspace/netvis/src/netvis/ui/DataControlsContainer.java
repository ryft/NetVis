package netvis.ui;

import javax.swing.JPanel;

public class DataControlsContainer extends JPanel {
	JPanel lastCtrl;

	public DataControlsContainer() {
		super();
		lastCtrl = null;
	}

	private static final long serialVersionUID = 1L;

	public void setPanel(JPanel newPanel) {
		if (lastCtrl != null)
			this.remove(lastCtrl);
		if (newPanel != null)
			this.add(newPanel);
		lastCtrl = newPanel;
		this.repaint();
	}

}
