package netvis.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.util.SpringUtilities;

/**
 * This is the cumulative analysis panel shown in the bottom of the GUI. It
 * collects data from the data controller and updates to reflect the data we've
 * seen so far.
 */
public class AnalysisPanel extends JSplitPane implements DataControllerListener {

	private static final long serialVersionUID = 1L;

	// Context panel to deliver extra data to
	protected final ContextPanel contextPanel = new ContextPanel();

	// Set up a separate thread for processing new data so the GUI stays
	// responsive on extreme input
	protected final Updater updateThread = new Updater();

	// Declare fields to be updated dynamically
	final JTextField fieldTotalPackets;
	final JTextField fieldPacketsPerDelta;
	final JTextField fieldPacketLength;
	final JTextField fieldBytesPerDelta;
	final JTextField fieldUniqueIPs;
	final JTextField fieldMostCommonPort;
	final JTextField fieldMostCommonProtocol;

	// Set up constants for Spring Layouts
	final int INITIAL_X = 0;
	final int INITIAL_Y = 0;
	final int PADDING_X = 5;
	final int PADDING_Y = 5;

	// Data to be updated by the data controller, and displayed by the UI
	final List<String> ipAddressesSeen = new ArrayList<String>();
	final Map<String, IPTraffic> ipTrafficTotals = new HashMap<String, IPTraffic>();

	final List<String> senders = new ArrayList<String>();
	final List<String> receivers = new ArrayList<String>();

	protected Map<Integer, Integer> portTrafficTotals = new HashMap<Integer, Integer>();
	protected Map<String, Integer> protocolTrafficTotals = new HashMap<String, Integer>();
	protected Integer mostCommonPortCount = -1;
	protected Integer mostCommonPort = null;
	protected Integer mostCommonProtocolCount = -1;
	protected String mostCommonProtocol = null;

	protected int totalPackets = 0;
	protected double totalTimePassed = 0; // Time measured in seconds
	protected int timeIncrementsPassed = 0;

	protected int minPacketsPerDelta = -1;
	protected double avgPacketsPerDelta = -1;
	protected int maxPacketsPerDelta = -1;

	protected int minPacketLength = -1; // All packet lengths measured in bytes
	protected double avgPacketLength = -1;
	protected int maxPacketLength = -1;

	protected Packet shortestPacket;
	protected Packet longestPacket;

	protected int totalBytes = 0;
	protected int avgBytes = -1;

	protected int minBytesPerDelta = -1;
	protected double avgBytesPerDelta = -1;
	protected int maxBytesPerDelta = -1;

	/**
	 * Tiny class to hold traffic data about a specific IP
	 */
	protected class IPTraffic {
		public int sent = 0;
		public int received = 0;

		public IPTraffic() {
		}
	}

	@Override
	public void allDataChanged(List<Packet> allPackets) {

		// Reset all collected data
		ipAddressesSeen.clear();
		ipTrafficTotals.clear();
		senders.clear();
		receivers.clear();

		portTrafficTotals.clear();
		protocolTrafficTotals.clear();
		mostCommonPortCount = -1;
		mostCommonPort = null;
		mostCommonProtocolCount = -1;
		mostCommonProtocol = null;

		totalPackets = 0;
		totalTimePassed = 0;
		timeIncrementsPassed = 0;

		minPacketsPerDelta = -1;
		avgPacketsPerDelta = -1;
		maxPacketsPerDelta = -1;

		minPacketLength = -1;
		avgPacketLength = -1;
		maxPacketLength = -1;
	}

	/**
	 * Thread to perform all data handling operations after we receive a new set
	 * of packets from the data controller
	 */
	protected class Updater extends Thread {

		/**
		 * @param newPackets
		 *            Packets received during the previous time interval
		 */
		public void crunchNewData(List<Packet> newPackets) {

			int totalNewPackets = newPackets.size();
			int totalNewBytes = 0;
			timeIncrementsPassed++;

			for (Packet p : newPackets) {
				totalPackets++; // Do this incrementally so we can do operations
								// per # packets seen
				totalNewBytes += p.length;

				// Update per-IP traffic data
				for (String ip : new String[] { p.sip, p.dip })
					if (!ipAddressesSeen.contains(ip)) {
						ipAddressesSeen.add(ip);
						ipTrafficTotals.put(ip, new IPTraffic());
					}
				ipTrafficTotals.get(p.sip).sent++;
				ipTrafficTotals.get(p.dip).received++;

				// Add IPs to unique senders/receivers lists
				if (!senders.contains(p.sip))
					senders.add(p.sip);
				if (!receivers.contains(p.dip))
					receivers.add(p.dip);

				// Update port and protocol traffic tallies
				for (Integer port : new Integer[] { p.sport, p.dport }) {
					if (!portTrafficTotals.containsKey(port))
						portTrafficTotals.put(port, 0);
					portTrafficTotals.put(port, portTrafficTotals.get(port) + 1);
				}
				if (!protocolTrafficTotals.containsKey(p.protocol))
					protocolTrafficTotals.put(p.protocol, 0);
				protocolTrafficTotals.put(p.protocol, protocolTrafficTotals.get(p.protocol) + 1);

				// Update packet length min/max/avg counts
				if (minPacketLength == -1) {
					minPacketLength = maxPacketLength = p.length;
					avgPacketLength = p.length;
					shortestPacket = longestPacket = p;
				} else {
					if (p.length < minPacketLength) {
						minPacketLength = p.length;
						shortestPacket = p;
					}
					if (p.length > maxPacketLength) {
						maxPacketLength = p.length;
						longestPacket = p;
					}
					avgPacketLength = (avgPacketLength * (totalPackets - 1) + p.length)
							/ totalPackets;
				}

				// Get most common port and protocol
				for (Integer port : portTrafficTotals.keySet()) {
					if (portTrafficTotals.get(port) > mostCommonPortCount) {
						mostCommonPortCount = portTrafficTotals.get(port);
						mostCommonPort = port;
					}
				}
				for (String protocol : protocolTrafficTotals.keySet()) {
					if (protocolTrafficTotals.get(protocol) > mostCommonProtocolCount) {
						mostCommonProtocolCount = protocolTrafficTotals.get(protocol);
						mostCommonProtocol = protocol;
					}
				}
			}

			totalBytes += totalNewBytes;

			// Update min/max/avg counts for aggregator - packets
			if (minPacketsPerDelta == -1) {
				minPacketsPerDelta = maxPacketsPerDelta = totalNewPackets;
				avgPacketsPerDelta = totalNewPackets;
			} else {
				minPacketsPerDelta = Math.min(minPacketsPerDelta, totalNewPackets);
				maxPacketsPerDelta = Math.max(maxPacketsPerDelta, totalNewPackets);
				avgPacketsPerDelta = (avgPacketsPerDelta * (timeIncrementsPassed - 1) + totalNewPackets)
						/ timeIncrementsPassed;
			}

			// Update min/max/avg counts for aggregator - traffic
			if (minBytesPerDelta == -1) {
				minBytesPerDelta = maxBytesPerDelta = totalNewBytes;
				avgBytesPerDelta = totalNewBytes;
			} else {
				minBytesPerDelta = Math.min(minBytesPerDelta, totalNewBytes);
				maxBytesPerDelta = Math.max(maxBytesPerDelta, totalNewBytes);
				avgBytesPerDelta = (avgBytesPerDelta * (timeIncrementsPassed - 1) + totalNewBytes)
						/ timeIncrementsPassed;
			}

			// Tell the components to update to reflect the new data
			updateControls();
		}
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {

		// Process the new packets in a separate thread to the JPanel thread so
		// that the GUI stays responsive on extreme input (although the data
		// will lag behind)
		updateThread.crunchNewData(newPackets);
	}

	protected void updateControls() {

		// Simply put values into text fields.
		fieldTotalPackets.setText(String.valueOf(totalPackets));
		fieldPacketsPerDelta.setText(String.valueOf(minPacketsPerDelta) + " / "
				+ String.valueOf(maxPacketsPerDelta) + " / "
				+ String.valueOf(Math.round(avgPacketsPerDelta)));
		fieldBytesPerDelta.setText(String.valueOf(minBytesPerDelta) + " / "
				+ String.valueOf(maxBytesPerDelta) + " / "
				+ String.valueOf(Math.round(avgBytesPerDelta)));
		fieldUniqueIPs.setText(String.valueOf(senders.size() + " / " + receivers.size()));
		fieldMostCommonPort.setText(String.valueOf(mostCommonPort));
		fieldMostCommonProtocol.setText(mostCommonProtocol);
		fieldPacketLength.setText(String.valueOf(minPacketLength) + " / "
				+ String.valueOf(maxPacketLength) + " / "
				+ String.valueOf(Math.round(avgPacketLength)));
	}

	public AnalysisPanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		// Set up tabbed panes to encapsulate cumulative data under separate categories
		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel panel1 = new JPanel(new SpringLayout());
		tabbedPane.addTab("Aggregation data", panel1);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JPanel panel2 = new JPanel(new SpringLayout());
		tabbedPane.addTab("Source/Destination info", panel2);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JPanel panel3 = new JPanel(new SpringLayout());
		tabbedPane.addTab("Packet details", panel3);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		// Add controls to the aggregation data tab
		panel1.add(new JLabel("Total packets seen: "));
		fieldTotalPackets = new JTextField();
		panel1.add(fieldTotalPackets);

		panel1.add(new JLabel("Min/Max/Avg packets per time increment: "));
		fieldPacketsPerDelta = new JTextField();
		panel1.add(fieldPacketsPerDelta);

		panel1.add(new JLabel("Min/Max/Avg bytes per time increment: "));
		fieldBytesPerDelta = new JTextField();
		panel1.add(fieldBytesPerDelta);

		SpringUtilities.makeCompactGrid(panel1, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);

		// Add controls to the src/dest data tab
		panel2.add(new JLabel("Unique sender/receiver IPs: "));
		fieldUniqueIPs = new JTextField();
		panel2.add(fieldUniqueIPs);

		panel2.add(new JLabel("IP traffic totals: "));
		JButton showTableButton = new JButton("Show table");
		panel2.add(showTableButton);

		IPTableModel ipTableData = new IPTableModel();
		final JTable ipTable = new JTable(ipTableData);
		ipTable.setAutoCreateRowSorter(true);

		// Refresh the table, from experience this doesn't happen automatically
		ipTableData.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				ipTable.repaint();
			}
		});

		showTableButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				contextPanel.update(ipTable);
				repaint();
			}
		});

		SpringUtilities.makeCompactGrid(panel2, 2, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);

		// Add controls to the packet details tab
		panel3.add(new JLabel("Most commonly used port: "));
		fieldMostCommonPort = new JTextField();
		panel3.add(fieldMostCommonPort);

		panel3.add(new JLabel("Most commonly used protocol: "));
		fieldMostCommonProtocol = new JTextField();
		panel3.add(fieldMostCommonProtocol);

		panel3.add(new JLabel("Min/Max/Avg packet length (bytes): "));
		fieldPacketLength = new JTextField();
		panel3.add(fieldPacketLength);
		
		fieldPacketLength.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				contextPanel.update(shortestPacket, longestPacket);
			}
		});

		SpringUtilities.makeCompactGrid(panel3, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);

		setLeftComponent(tabbedPane);
		setResizeWeight(0.85);
		setRightComponent(contextPanel);

		updateThread.run();
	}

	/**
	 * IP traffic table -- automatically updates from ipAddressesSeen array and
	 * ipTrafficTotals map
	 */
	@SuppressWarnings("serial")
	protected class IPTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return ipAddressesSeen.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			// Return empty string if we're outside the array bounds
			if (col >= ipAddressesSeen.size())
				return new String();
			assert (ipAddressesSeen.size() == ipTrafficTotals.size());

			String IP = ipAddressesSeen.get(row);
			IPTraffic traffic = ipTrafficTotals.get(IP);

			switch (col) {
			case 0:
				return IP;
			case 1:
				return traffic.sent;
			case 2:
				return traffic.received;
			case 3:
				return traffic.sent + traffic.received;
			default:
				return new String();
			}
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "IP Address";
			case 1:
				return "Sent";
			case 2:
				return "Received";
			case 3:
				return "Total";
			default:
				return "";
			}
		}
	}

}
