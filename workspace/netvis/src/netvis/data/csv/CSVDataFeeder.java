package netvis.data.csv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import netvis.ApplicationFrame;
import netvis.data.DataFeeder;
import netvis.data.model.Packet;
import netvis.ui.TimeControlPanel;

public class CSVDataFeeder implements DataFeeder {
	protected double firstPacketTime, timeScale;
	protected File file;
	protected ApplicationFrame parent;
	protected CSVReader csvReader;
	protected Iterator<String[]> it;
	protected Timer secondsTimer;
	protected int seconds;
	protected TimeControlPanel timeControlPanel;
	protected boolean goToEnd = false;

	/**
	 * @param fileName
	 *            CSV file that contains the capture.
	 */
	public CSVDataFeeder(File file,	ApplicationFrame parent) {
		this.file = file;
		this.parent = parent;
		String fileName = file.getAbsolutePath();
		try {
			Reader reader = new FileReader(file);
			csvReader = new CSVReader(reader);

			it = csvReader.readAll().iterator();
			it.next();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(parent, "File " + fileName
					+ " doesn't exist", "File not found",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, "File " + fileName
					+ " cannot be read", "File not found",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		// this counts a user-controlled simulation time
		seconds = 0;
		ActionListener secondCounter = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		          seconds++;
		          timeControlPanel.setStatusLabel("Current time: "+Integer.toString(seconds)+"s");
		      }
		};
		secondsTimer = new Timer(1000, secondCounter);
		secondsTimer.start();

		timeControlPanel = new TimeControlPanel(this);
		
		parent.setTitle("NetVis - " + fileName);
	}
	
	public List<Packet> getNewPackets() {
		if (!it.hasNext()) {
			secondsTimer.stop();
			timeControlPanel.feedEnded();
			return null;
		} else if (!secondsTimer.isRunning()) {
			return null;
		} else {
			List<Packet> list = new ArrayList<Packet>();
			while (it.hasNext()) {
				list.add(lineToPacket(it.next()));
				if (!goToEnd && list.get(list.size() - 1).time > seconds)
					break;
			}
			if (goToEnd) {
				timeControlPanel.setStatusLabel("Current time: "
							+Long.toString(Math.round(list.get(list.size() - 1).time))+"s");
				secondsTimer.stop();
				timeControlPanel.feedEnded();
			}
			return list;
		}
	}
	
	@Override
	public boolean hasNext() {
		if (!it.hasNext()) {
			secondsTimer.stop();
			timeControlPanel.feedEnded();
		}
		return it.hasNext();
	}
	
	public int updateInterval() {
		return secondsTimer.getDelay();
	}
	
	/**
	 * Time Controls
	 */
	
	public void play() {
		secondsTimer.start();
		timeControlPanel.setPlayStatus(true);
	}
	
	public void pause() {
		secondsTimer.stop();
		timeControlPanel.setPlayStatus(false);
	}
	
	public void togglePlay() {
		if (isPlaying())
			pause();
		else
			play();
	}
	
	public void faster() {
		secondsTimer.setDelay((int) (secondsTimer.getDelay() * 0.5));
	}
	
	public void slower() {
		secondsTimer.setDelay((int) (secondsTimer.getDelay() * 2));
	}
	
	public void skipToStart() {
		parent.openCSV(file);
	}
	
	public void skipToEnd() {
		goToEnd = true;
	}
	
	public boolean isPlaying() {
		return secondsTimer.isRunning();
	}
	
	public JPanel controlPanel() {
		return timeControlPanel;
	}

	/**
	 * Converts a line of csv from an array of strings to a packet
	 * 
	 * @param line
	 *            CSV line as array of strings
	 * @return Packet
	 */
	public static Packet lineToPacket(String[] line) {
		int no = parseInt(line[0]);
		double time = parseFloat(line[1]);
		int sport = parseInt(line[4]);
		int dport = parseInt(line[7]);
		int length = parseInt(line[9]);
		return new Packet(no, time, line[2], line[3], sport, line[5], line[6],
				dport, line[8], length, line[10]);
	}

	// Protect ourselves from NumberFormatException errors
	protected static Integer parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	protected static Float parseFloat(String str) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException nfe) {
			return 0f;
		}
	}

}
