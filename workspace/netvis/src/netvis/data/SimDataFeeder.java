package netvis.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import netvis.ApplicationFrame;
import netvis.data.model.Packet;

public class SimDataFeeder implements DataFeeder {
	protected long initialTime;
	protected double firstPacketTime, timeScale;
	protected CSVReader csvReader;
	protected Iterator<String[]> it;

	/**
	 * 
	 * @param fileName
	 *            CSV file that contains the capture. Must be in folder
	 *            ./csv/captures
	 * @param timeScale
	 *            Slow down / speed up the time of the Data Feeder
	 */
	public SimDataFeeder(String fileName, double timeScale,
			ApplicationFrame parent) {
		try {
			File dir = new File("./../../csv/captures/");
			File file = new File(dir, fileName);

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
		this.timeScale = timeScale;
		initialTime = System.currentTimeMillis();
	}

	@Override
	public List<Packet> getNewPackets() {
		double newRequestTime = (System.currentTimeMillis() - initialTime) / 1000;
		List<Packet> list = new ArrayList<Packet>();
		boolean ok = true;

		while (ok && it.hasNext()) {
			list.add(lineToPacket(it.next()));
			if (list.get(list.size() - 1).time > (newRequestTime * timeScale))
				ok = false;
		}
		return list;
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
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
		if (str.length() == 0) return 0;
		else return Integer.parseInt(str);
	}
	protected static Float parseFloat(String str) {
		if (str.length() == 0) return 0f;
		else return Float.parseFloat(str);
	}

}
