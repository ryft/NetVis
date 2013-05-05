package netvis.ui;

import javax.swing.JPanel;

public class VisControlsContainer extends JPanel {
	JPanel lastCtrl;

	public VisControlsContainer() {
		super();
		lastCtrl = null;
	}

	private static final long serialVersionUID = 1L;

	public void setVisControl(JPanel newPanel) {
		if (lastCtrl != null)
			this.remove(lastCtrl);

		if (newPanel != null)
			this.add(newPanel);
		lastCtrl = newPanel;
		this.repaint();
	}

}
