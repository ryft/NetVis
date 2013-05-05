package netvis.ui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import netvis.data.csv.CSVDataFeeder;

@SuppressWarnings("serial")
public class TimeControlPanel extends JPanel {

	private final JButton playButton, restartButton, endButton, slowerButton, fasterButton;
	private final JLabel statusLabel, endOfFeedLabel;
	final ImageIcon playIcon = new ImageIcon("img/media-playback-start.png");
	final ImageIcon pauseIcon = new ImageIcon("img/media-playback-pause.png");

	public TimeControlPanel(final CSVDataFeeder dataFeeder) {
		playButton = pictureButton("img/media-playback-pause.png", "Pause");
		restartButton = pictureButton("img/media-skip-backward.png", "Restart");
		endButton = pictureButton("img/media-skip-forward.png", "Skip to End of Data Feed");
		slowerButton = pictureButton("img/media-seek-backward.png", "Slower");
		fasterButton = pictureButton("img/media-seek-forward.png", "Faster");

		if (!dataFeeder.isPlaying()) {
			playButton.setIcon(playIcon); // note initialised to pauseIcon
		}

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataFeeder.togglePlay();
			}
		});

		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataFeeder.skipToStart();
			}
		});

		slowerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataFeeder.slower();
			}
		});

		fasterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataFeeder.faster();
			}
		});

		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataFeeder.skipToEnd();
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.add(playButton);
		buttonPanel.add(restartButton);
		buttonPanel.add(endButton);
		buttonPanel.add(slowerButton);
		buttonPanel.add(fasterButton);
		add(buttonPanel);		
		
		statusLabel = new JLabel("");
		add(statusLabel);
		endOfFeedLabel = new JLabel("");
		add(endOfFeedLabel);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
	}

	private JButton pictureButton(String imageFile, String tooltip) {
		JButton button = new JButton();
		button.setIcon(new ImageIcon(imageFile));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setToolTipText(tooltip);
		return button;
	}

	public void setPlayStatus(boolean status) {
		if (status) {
			playButton.setIcon(pauseIcon);
			playButton.setToolTipText("Pause");
		} else {
			playButton.setIcon(playIcon);
			playButton.setToolTipText("Play");
		}
	}
	
	public void setStatusLabel(String message) {
		statusLabel.setText(message);
	}
	
	/**
	 * Gets called by Data Feeder when no more records are available.
	 * Disables buttons and displays a message.
	 */
	public void feedEnded() {
		playButton.setEnabled(false);
		endButton.setEnabled(false);
		slowerButton.setEnabled(false);
		fasterButton.setEnabled(false);
		endOfFeedLabel.setText("No more data available.");
	}

}
