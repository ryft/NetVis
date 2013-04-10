package netvis.ui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import netvis.data.TimeControlDataController;

@SuppressWarnings("serial")
public class TimeControlPanel extends JPanel {

	public TimeControlPanel(final TimeControlDataController dataController) {
		final JButton playButton = pictureButton("img/media-playback-pause.png");
		JButton restartButton = pictureButton("img/media-skip-backward.png");
		JButton endButton = pictureButton("img/media-skip-forward.png");
		JButton slowerButton = pictureButton("img/media-seek-backward.png");
		JButton fasterButton = pictureButton("img/media-seek-forward.png");
		
		final ImageIcon playIcon = new ImageIcon("img/media-playback-start.png");
		final ImageIcon pauseIcon = new ImageIcon("img/media-playback-pause.png");
		
		if (!dataController.isPlaying()) {
			playButton.setIcon(playIcon); // note initialised to pauseIcon
		}
		// TODO playButton should listen to the DataController, not pull
		
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dataController.isPlaying()) {
					dataController.pause();
					playButton.setIcon(playIcon);
				} else {
					dataController.play();
					playButton.setIcon(pauseIcon);
				}
			}
		});
		
		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataController.pause();
			}
		});
		
		slowerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataController.slower();
			}
		});
		
		fasterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataController.faster();
			}
		});
		
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataController.skipToEnd();
			}
		});
		
		add(playButton);
		// add(restartButton);
		add(endButton);
		add(slowerButton);
		add(fasterButton);
		
		// TODO add a label showing the seconds field of the DataController
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setAlignmentX(LEFT_ALIGNMENT);
	}
	
	private JButton pictureButton(String imageFile) {
		JButton button = new JButton();
		button.setIcon(new ImageIcon(imageFile));
		button.setMargin(new Insets(0, 0, 0, 0));
		return button;
	}
	
}
