	package netvis.data;

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
import netvis.data.model.Packet;
import netvis.ui.TimeControlPanel;

public class CSVDataFeeder implements DataFeeder {
	protected double firstPacketTime, timeScale;
	protected CSVReader csvReader;
	protected Iterator<String[]> it;
	protected Timer secondsTimer;
	protected int seconds;
	protected JPanel timeControlPanel;

	/**
	 * @param fileName
	 *            CSV file that contains the capture.
	 */
	public CSVDataFeeder(File file,	ApplicationFrame parent) {
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
		      }
		};
		secondsTimer = new Timer(1000, secondCounter);
		secondsTimer.start();
		
		timeControlPanel = new TimeControlPanel(this);
		
		parent.setTitle("NetVis - " + fileName);
	}
	
	public List<Packet> getNewPackets() {
		if (!secondsTimer.isRunning()) {
			return null;
		} else {
			List<Packet> list = new ArrayList<Packet>();
			while (it.hasNext()) {
				list.add(lineToPacket(it.next()));
				if (list.get(list.size() - 1).time > seconds)
					break;
			}
			return list;
		}
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}
	
	/**
	 * Time Controls
	 */
	
	public void play() {
		secondsTimer.start();
	}
	
	public void pause() {
		secondsTimer.stop();
	}
	
	public void faster() {
		secondsTimer.setDelay((int) (secondsTimer.getDelay() * 0.5));
	}
	
	public void slower() {
		secondsTimer.setDelay((int) (secondsTimer.getDelay() * 2));
	}
	
	public void skipToEnd() {
		seconds = 14 * 24 * 60 * 60; // assume CSVs don't capture more than 14 days
		// TODO this might break some statistics, in particular averages over time
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
