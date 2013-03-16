package netvis.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import netvis.util.NetUtilities;
import netvis.util.SpringUtilities;

/**
 * This is the cumulative analysis panel shown in the bottom of the GUI. It
 * collects data from the data controller and updates to reflect the data we've
 * seen so far.
 */
@SuppressWarnings("serial")
public class AnalysisPanel extends JSplitPane implements DataControllerListener {

	/** Context panel to deliver extra data to */
	protected final ContextPanel contextPanel = new ContextPanel();

	/** Set up a separate thread for processing new data so the GUI stays
		responsive on extreme input */
	protected final Updater updateThread = new Updater();

	// Declare fields to be updated dynamically
	protected final JTextField fieldTotals;
	protected final JTextField fieldPacketsPerDelta;
	protected final JTextField fieldPacketLength;
	protected final JTextField fieldBytesPerDelta;
	protected final JTextField fieldUniqueSenderIPs;
	protected final JTextField fieldUniqueReceiverIPs;
	protected final JTextField fieldMostCommonPort;
	protected final JTextField fieldMostCommonProtocol;

	// Set up constants for Spring Layouts
	protected final int INITIAL_X = 0;
	protected final int INITIAL_Y = 0;
	protected final int PADDING_X = 5;
	protected final int PADDING_Y = 5;

	// Data to be updated by the data controller, and displayed by the UI
	protected final List<String> ipAddressesSeen = new ArrayList<String>();
	protected final Map<String, IPTraffic> ipTrafficTotals = new HashMap<String, IPTraffic>();
	protected final List<String> senderIPs = new ArrayList<String>();
	protected final List<String> receiverIPs = new ArrayList<String>();

	// Set up a map for the number of times each port and protocol is used
	protected Map<Integer, Integer> portTrafficTotals = new HashMap<Integer, Integer>();
	protected Map<String, Integer> protocolTrafficTotals = new HashMap<String, Integer>();
	
	protected Integer mostCommonPortCount = -1;
	protected Integer mostCommonPort = null;
	protected Integer mostCommonProtocolCount = -1;
	protected String mostCommonProtocol = null;

	// Lists of the number of packets/bytes seen after each interval (must be increasing)
	protected final List<Integer> packetsSeenOverTime = new ArrayList<Integer>();
	protected final List<Integer> bytesSeenOverTime = new ArrayList<Integer>();

	protected int totalPackets = 0;
	protected double totalTimePassed = 0; // Time measured in seconds
	protected int totalIntervalsPassed = 0;

	protected int minPacketsPerInterval = -1;
	protected double avgPacketsPerInterval = -1;
	protected int maxPacketsPerInterval = -1;

	protected int minPacketLength = -1; // All packet lengths measured in bytes
	protected double avgPacketLength = -1;
	protected int maxPacketLength = -1;

	protected Packet shortestPacket = null;
	protected Packet longestPacket = null;

	protected int totalBytes = 0;
	protected int avgBytes = -1;

	protected int minBytesPerInterval = -1;
	protected double avgBytesPerInterval = -1;
	protected int maxBytesPerInterval = -1;

	/** Tiny class to hold traffic data about a specific IP */
	protected class IPTraffic {
		public int sent = 0;
		public int received = 0;

		/** Create an empty traffic accumulator */
		public IPTraffic() {
		}
	}

	/**
	 * Create a new Analysis Panel, with controls which autonomously listen to
	 * data from the data controller and send data to a Context Panel.
	 */
	public AnalysisPanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		// Set up tab panes to encapsulate cumulative data under separate categories
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

		// PANEL 1: Add controls to the aggregation data tab
		panel1.add(new JLabel("Total packets/traffic transmitted: "));
		fieldTotals = new JTextField();
		panel1.add(fieldTotals);

		JLabel labelPacketsPerDelta = new AbstractContextLink("Min/Max/Avg packets per time interval: "){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				contextPanel.update("Packets transmitted over "+Math.round(totalTimePassed)+"s, "+
						"current total: "+totalPackets, packetsSeenOverTime);
			}
		};
		panel1.add(labelPacketsPerDelta);
		fieldPacketsPerDelta = new JTextField();
		panel1.add(fieldPacketsPerDelta);

		JLabel labelBytesPerDelta = new AbstractContextLink("Min/Max/Avg traffic per time interval: "){
			@Override
			public void mouseClicked(MouseEvent e) {
				contextPanel.update("Bytes transmitted over "+Math.round(totalTimePassed)+"s, "+
						"current total: "+totalBytes+" bytes", bytesSeenOverTime);
			}
		};
		panel1.add(labelBytesPerDelta);
		fieldBytesPerDelta = new JTextField();
		panel1.add(fieldBytesPerDelta);

		SpringUtilities.makeCompactGrid(panel1, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);

		// PANEL 2: Add controls to the src/dest data tab
		panel2.add(new JLabel("Unique sender IPs: "));
		fieldUniqueSenderIPs = new JTextField();
		panel2.add(fieldUniqueSenderIPs);

		panel2.add(new JLabel("Unique receiver IPs: "));
		fieldUniqueReceiverIPs = new JTextField();
		panel2.add(fieldUniqueReceiverIPs);

		panel2.add(new JLabel("IP traffic totals: "));
		JButton showTableButton = new JButton("Show/Update IP packet traffic");
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

		SpringUtilities.makeCompactGrid(panel2, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);
		
		// PANEL 3: Add controls to the packet details tab
		JLabel labelMostCommonPort = new AbstractContextLink("Most commonly used port: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				contextPanel.update("Total traffic, grouped and sorted by port", portTrafficTotals);
			}
		};
		panel3.add(labelMostCommonPort);
		fieldMostCommonPort = new JTextField();
		panel3.add(fieldMostCommonPort);

		JLabel labelMostCommonProtocol = new AbstractContextLink("Most commonly used protocol: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				contextPanel.update("Total traffic, grouped and sorted by protocol", protocolTrafficTotals);
			}
		};
		panel3.add(labelMostCommonProtocol);
		fieldMostCommonProtocol = new JTextField();
		panel3.add(fieldMostCommonProtocol);

		JLabel labelPacketLength = new AbstractContextLink("Min/Max/Avg packet length: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				contextPanel.update(shortestPacket, longestPacket);
			}
		};
		panel3.add(labelPacketLength);
		fieldPacketLength = new JTextField();
		panel3.add(fieldPacketLength);

		SpringUtilities.makeCompactGrid(panel3, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);
		
		// Put together the tab pane and set it up next to the context panel
		setLeftComponent(tabbedPane);
		setResizeWeight(0.85);
		setRightComponent(contextPanel);

		updateThread.run();	// Initiate the data handling thread
	}

	/**
	 * Custom JLabel subclass to act as a 'hyperlink' -- the mouse clicked function is abstract
	 * because the behaviour differs for each link.
	 */
	protected abstract class AbstractContextLink extends JLabel implements MouseListener {

		/**
		 * Construct a link label which displays the specified text
		 * @param labelText	the text to show on the label
		 */
		public AbstractContextLink(String labelText) {
			super(labelText);
			addMouseListener(this);
			setForeground(Color.blue.darker().darker());
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setCursor(Cursor.getDefaultCursor());
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	@Override
	public void allDataChanged(List<Packet> allPackets) {

		// Reset all collected data
		ipAddressesSeen.clear();
		ipTrafficTotals.clear();
		senderIPs.clear();
		receiverIPs.clear();

		portTrafficTotals.clear();
		protocolTrafficTotals.clear();
		mostCommonPortCount = -1;
		mostCommonPort = null;
		mostCommonProtocolCount = -1;
		mostCommonProtocol = null;
		
		packetsSeenOverTime.clear();
		bytesSeenOverTime.clear();

		totalPackets = 0;
		totalTimePassed = 0;
		totalIntervalsPassed = 0;

		minPacketsPerInterval = -1;
		avgPacketsPerInterval = -1;
		maxPacketsPerInterval = -1;

		minPacketLength = -1;
		avgPacketLength = -1;
		maxPacketLength = -1;
		
		shortestPacket = null;
		longestPacket = null;

		totalBytes = 0;
		avgBytes = -1;

		minBytesPerInterval = -1;
		avgBytesPerInterval = -1;
		maxBytesPerInterval = -1;
		
		// Clear all fields
		updateControls();
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {

		// Process the new packets in a separate thread to the JPanel thread so
		// that the GUI stays responsive on extreme input (although the data
		// may lag behind)
		updateThread.crunchNewData(newPackets);
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
			totalIntervalsPassed++;

			for (Packet p : newPackets) {
				totalPackets++; // Do this incrementally so we can do operations
								// per # packets seen
				totalNewBytes += p.length;
				// Make sure the total time passed always increases
				totalTimePassed = Math.max(totalTimePassed, p.time);

				// Update per-IP traffic data
				for (String ip : new String[] { p.sip, p.dip })
					if (!ipAddressesSeen.contains(ip)) {
						ipAddressesSeen.add(ip);
						ipTrafficTotals.put(ip, new IPTraffic());
					}
				ipTrafficTotals.get(p.sip).sent++;
				ipTrafficTotals.get(p.dip).received++;

				// Add IPs to unique senders/receivers lists
				if (!senderIPs.contains(p.sip))
					senderIPs.add(p.sip);
				if (!receiverIPs.contains(p.dip))
					receiverIPs.add(p.dip);

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
			bytesSeenOverTime.add(totalBytes);
			packetsSeenOverTime.add(totalPackets);

			// Update min/max/avg counts for aggregator - packets
			if (minPacketsPerInterval == -1) {
				minPacketsPerInterval = maxPacketsPerInterval = totalNewPackets;
				avgPacketsPerInterval = totalNewPackets;
			} else {
				minPacketsPerInterval = Math.min(minPacketsPerInterval, totalNewPackets);
				maxPacketsPerInterval = Math.max(maxPacketsPerInterval, totalNewPackets);
				avgPacketsPerInterval = (avgPacketsPerInterval * (totalIntervalsPassed - 1) + totalNewPackets)
						/ totalIntervalsPassed;
			}

			// Update min/max/avg counts for aggregator - traffic
			if (minBytesPerInterval == -1) {
				minBytesPerInterval = maxBytesPerInterval = totalNewBytes;
				avgBytesPerInterval = totalNewBytes;
			} else {
				minBytesPerInterval = Math.min(minBytesPerInterval, totalNewBytes);
				maxBytesPerInterval = Math.max(maxBytesPerInterval, totalNewBytes);
				avgBytesPerInterval = (avgBytesPerInterval * (totalIntervalsPassed - 1) + totalNewBytes)
						/ totalIntervalsPassed;
			}

			// Tell the components to update to reflect the new data
			updateControls();
		}
	}

	/**
	 * Internal function to refresh all necessary fields with new updated information when new
	 * data arrives
	 */
	protected void updateControls() {

		// Simply put values into text fields.
		fieldTotals.setText(String.valueOf(totalPackets) + " / "
				+ NetUtilities.parseBytes(totalBytes));
		fieldPacketsPerDelta.setText(String.valueOf(minPacketsPerInterval) + " / "
				+ String.valueOf(maxPacketsPerInterval) + " / "
				+ String.valueOf(Math.round(avgPacketsPerInterval)));
		fieldBytesPerDelta.setText(NetUtilities.parseBytes(minBytesPerInterval) + " / "
				+ NetUtilities.parseBytes(maxBytesPerInterval) + " / "
				+ NetUtilities.parseBytes(Math.round(avgBytesPerInterval)));
		fieldUniqueSenderIPs.setText(String.valueOf(senderIPs.size()));
		fieldUniqueReceiverIPs.setText(String.valueOf(receiverIPs.size()));
		fieldMostCommonPort.setText(String.valueOf(mostCommonPort));
		fieldMostCommonProtocol.setText(mostCommonProtocol);
		fieldPacketLength.setText(NetUtilities.parseBytes(minPacketLength) + " / "
				+ NetUtilities.parseBytes(maxPacketLength) + " / "
				+ NetUtilities.parseBytes(Math.round(avgPacketLength)));
	}

	/**
	 * IP traffic table -- automatically updates from ipAddressesSeen array and
	 * ipTrafficTotals map
	 */
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
