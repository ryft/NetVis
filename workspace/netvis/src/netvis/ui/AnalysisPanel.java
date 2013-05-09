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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import netvis.data.DataControllerListener;
import netvis.data.model.Packet;
import netvis.util.SpringUtilities;
import netvis.util.Utilities;

/**
 * This is the cumulative analysis panel shown in the bottom of the GUI. It
 * collects data from the data controller and updates to reflect the data we've
 * seen so far.
 */
@SuppressWarnings("serial")
public class AnalysisPanel extends JTabbedPane implements DataControllerListener, ActionListener {

	/** List of all inputs we've collected */
	protected Queue<List<Packet>> updateQueue = new LinkedBlockingDeque<List<Packet>>();
	/** List of updates received while running static analysis */
	protected Queue<List<Packet>> batchQueue = new LinkedBlockingDeque<List<Packet>>();
	/**
	 * Flag to block certain events while static analysis jobs are being
	 * created. Any outside modification of this variable is unsafe.
	 */
	public boolean batchProcessBlock = false;

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

	// Lists of the number of packets/bytes seen after each interval (must be
	// increasing)
	protected final List<Integer> packetsSeenOverTime = new ArrayList<Integer>();
	protected final List<Integer> bytesSeenOverTime = new ArrayList<Integer>();

	protected int totalPackets = 0;
	protected double totalTimePassed = 0; // Time measured in seconds
	protected int totalIntervalsPassed = 0;

	/*
	 * We often need to make a distinction between 0 (no traffic) and -1 (no
	 * records) for the purposes of updating with new data.
	 */
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

	// These controls need to be declared here so we have a reference to
	// enable/disable them
	protected final JLabel labelPacketsPerDelta;
	protected final JLabel labelBytesPerDelta;
	protected final JLabel labelMostCommonPort;
	protected final JLabel labelMostCommonProtocol;
	protected final JLabel labelPacketLength;
	protected final JButton buttonShowTable;

	// Panel to display when information is available for the user
	protected final JPanel infoTab;
	// Stores the tab we were previously viewing
	protected int prevTabIndex;
	// Stores whether or not we're now performing batch operations
	protected boolean batchProcessing = false;

	/** Timer to manage the processing of new data */
	protected final Timer dataUpdateTimer;
	/** Timer to manage the update of controls. */
	protected final Timer controlUpdateTimer;

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
	 * 
	 * @param controlUpdateInterval
	 *            The time interval between control updates, in ms. Needs to be
	 *            some multiple of the data controller update interval.
	 */
	public AnalysisPanel(int controlUpdateInterval, final ContextPanel contextPanel) {

		JPanel panel1 = new JPanel(new SpringLayout());
		this.addTab("Aggregation data", panel1);
		this.setMnemonicAt(0, KeyEvent.VK_1);

		JPanel panel2 = new JPanel(new SpringLayout());
		this.addTab("Source/Destination info", panel2);
		this.setMnemonicAt(1, KeyEvent.VK_2);

		JPanel panel3 = new JPanel(new SpringLayout());
		this.addTab("Packet details", panel3);
		this.setMnemonicAt(2, KeyEvent.VK_3);

		// Set up panel to display when loading data
		infoTab = new JPanel();
		infoTab.add(new JLabel("Nothing to report."));
		this.addTab("Notifications", infoTab);

		// PANEL 1: Add controls to the aggregation data tab
		panel1.add(new JLabel("Total packets/traffic transmitted: "));
		fieldTotals = new JTextField();
		panel1.add(fieldTotals);

		labelPacketsPerDelta = new AbstractContextLink("Min/Max/Avg packets per time interval: ") {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (batchQueue.size() == 0 && !batchProcessBlock)
					contextPanel.update("Packets transmitted over " + Math.round(totalTimePassed)
							+ "s, " + "current total: " + totalPackets, packetsSeenOverTime);
			}
		};
		panel1.add(labelPacketsPerDelta);
		fieldPacketsPerDelta = new JTextField();
		panel1.add(fieldPacketsPerDelta);

		labelBytesPerDelta = new AbstractContextLink("Min/Max/Avg traffic per time interval: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (batchQueue.size() == 0 && !batchProcessBlock)
					contextPanel.update("Bytes transmitted over " + Math.round(totalTimePassed)
							+ "s, " + "current total: " + Utilities.parseBytes(totalBytes),
							bytesSeenOverTime);
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
		buttonShowTable = new JButton("Show/Update IP packet traffic table");
		panel2.add(buttonShowTable);

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

		buttonShowTable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (batchQueue.size() == 0 && !batchProcessBlock) {
					contextPanel.update(ipTable);
					repaint();
				}
			}
		});

		SpringUtilities.makeCompactGrid(panel2, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);

		// PANEL 3: Add controls to the packet details tab
		labelMostCommonPort = new AbstractContextLink("Most commonly used port: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (batchQueue.size() == 0 && !batchProcessBlock)
					contextPanel.update("Total traffic, grouped and sorted by port",
							portTrafficTotals);
			}
		};
		panel3.add(labelMostCommonPort);
		fieldMostCommonPort = new JTextField();
		panel3.add(fieldMostCommonPort);

		labelMostCommonProtocol = new AbstractContextLink("Most commonly used protocol: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (batchQueue.size() == 0 && !batchProcessBlock)
					contextPanel.update("Total traffic, grouped and sorted by protocol",
							protocolTrafficTotals);
			}
		};
		panel3.add(labelMostCommonProtocol);
		fieldMostCommonProtocol = new JTextField();
		panel3.add(fieldMostCommonProtocol);

		labelPacketLength = new AbstractContextLink("Min/Max/Avg packet length: ") {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (batchQueue.size() == 0 && !batchProcessBlock)
					contextPanel.update(shortestPacket, longestPacket);
			}
		};
		panel3.add(labelPacketLength);
		fieldPacketLength = new JTextField();
		panel3.add(fieldPacketLength);

		SpringUtilities.makeCompactGrid(panel3, 3, 2, INITIAL_X, INITIAL_Y, PADDING_X, PADDING_Y);

		dataUpdateTimer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runUpdates();
			}
		});
		controlUpdateTimer = new Timer(controlUpdateInterval, this);
	}

	public void init() {

		dataUpdateTimer.start();
		controlUpdateTimer.start();
	}

	protected void dataStateChanged(boolean loading) {
		infoTab.removeAll();
		if (loading) {
			infoTab.add(new JLabel("Analysing new data, please wait..."));
			prevTabIndex = this.getSelectedIndex();
			this.setSelectedComponent(infoTab);
		} else {
			infoTab.add(new JLabel("Completed analysis. Nothing to report."));
			this.setSelectedIndex(prevTabIndex);
		}
	}

	@Override
	public void allDataChanged(List<Packet> allPackets, int updateIntervalms, int intervalsComplete) {

		// Essentially reset the state of the panel, re-process all provided
		// packets, then (and only then) start processing new packets from the
		// controller again.

		// Convert ms to seconds
		double updateInterval = ((double) updateIntervalms) / 1000;

		// Block anything which relies on uninterrupted data flow
		batchProcessBlock = true;
		// Stop the controls updating and clear the current data
		controlUpdateTimer.stop();

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

		// Split up all the previous data into blocks as large as one update
		// interval, so we can simulate their arrival by putting them in the
		// batch queue to be processed with high priority.

		int currIndex = 0;
		int packetCount = allPackets.size();
		List<Packet> currentBlock = new ArrayList<Packet>();

		// Split packets into blocks of size updateInterval
		// Loop invariant:
		// updateInterval*i <= allPackets[i].time < updateInterval*(i+1)
		// for 0 <= i < intervalsComplete
		for (int i = 0; i < intervalsComplete; i++) {
			while (currIndex < packetCount
					&& allPackets.get(currIndex).time < (i + 1) * updateInterval) {
				currentBlock.add(allPackets.get(currIndex));
				currIndex++;
			}

			// Send the current block to the batch queue
			batchQueue.add(new ArrayList<Packet>(currentBlock));
			currentBlock.clear();
		}

		// All data is processed, allow controls to be updated again
		controlUpdateTimer.start();
		// Release all controls
		batchProcessBlock = false;
	}

	@Override
	public void newPacketsArrived(List<Packet> newPackets) {
		// Add update to the job queue for processing.
		updateQueue.add(newPackets);
	}

	/**
	 * Synchronised block which runs all queued updates. Most of the time there
	 * will be only one job in the update queue (when the data controller gives
	 * us some new packets) but whenever the previous data is changed, these
	 * jobs are put in the batch queue, and need to be run before we process any
	 * new packets from the controller.
	 */
	protected synchronized void runUpdates() {

		// Run analysis on all batch updates first, and (importantly) leave the
		// update queue alone, as the batch queue may still be being filled and
		// we may break the ordering by processing any new packets.
		if (batchQueue.size() > 0 || batchProcessBlock) {

			if (!batchProcessing) {
				// Display a message to the user
				dataStateChanged(true);
				batchProcessing = true;
			}

			// Take a snapshot of the current state of the queue
			Queue<List<Packet>> batchQueueClone = new LinkedBlockingDeque<List<Packet>>();
			for (int job = 0; job < batchQueue.size(); job++)
				batchQueueClone.add(batchQueue.remove());

			// Run all batch jobs
			while (batchQueueClone.size() > 0) {
				doUpdate(batchQueueClone.remove());
			}

			// Similarly for update jobs
		} else if (updateQueue.size() > 0) {

			if (batchProcessing) {
				// Display a message to the user
				dataStateChanged(false);
				batchProcessing = false;
			}

			Queue<List<Packet>> updateQueueClone = new LinkedBlockingDeque<List<Packet>>();
			for (int job = 0; job < updateQueue.size(); job++)
				updateQueueClone.add(updateQueue.remove());

			while (updateQueueClone.size() > 0) {
				doUpdate(updateQueueClone.remove());
			}

		}
	}

	/**
	 * @param newPackets
	 *            Packets received during exactly one time interval
	 */
	protected void doUpdate(List<Packet> newPackets) {

		// Process new data
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
				avgPacketLength = (avgPacketLength * (totalPackets - 1) + p.length) / totalPackets;
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
	}

	@Override
	// This is the action of the control updater
	public void actionPerformed(ActionEvent arg0) {

		// Tell the components to update to reflect the new data
		if (batchQueue.size() == 0 && !batchProcessBlock) // Suppress output if
															// necessary
			updateControls();
	}

	/**
	 * Internal function to refresh all necessary fields with new updated
	 * information when new data arrives
	 */
	protected void updateControls() {

		// Enable/disable labels as required by the state of the data and put
		// values into text fields.
		// PANEL 1
		fieldTotals
				.setText(String.valueOf(totalPackets) + " / " + Utilities.parseBytes(totalBytes));

		labelPacketsPerDelta.setEnabled(packetsSeenOverTime.size() > 0);
		if (minPacketsPerInterval < 0)
			fieldPacketsPerDelta.setText("0 / 0 / 0");
		else
			fieldPacketsPerDelta.setText(String.valueOf(minPacketsPerInterval) + " / "
					+ String.valueOf(maxPacketsPerInterval) + " / "
					+ String.valueOf(Math.round(avgPacketsPerInterval)));

		labelBytesPerDelta.setEnabled(bytesSeenOverTime.size() > 0);
		if (minBytesPerInterval < 0)
			fieldBytesPerDelta.setText("0 B / 0 B / 0 B");
		else
			fieldBytesPerDelta.setText(Utilities.parseBytes(minBytesPerInterval) + " / "
					+ Utilities.parseBytes(maxBytesPerInterval) + " / "
					+ Utilities.parseBytes(Math.round(avgBytesPerInterval)));

		// PANEL 2
		fieldUniqueSenderIPs.setText(String.valueOf(senderIPs.size()));
		fieldUniqueReceiverIPs.setText(String.valueOf(receiverIPs.size()));
		buttonShowTable.setEnabled(ipTrafficTotals.size() > 0);

		// PANEL 3
		labelMostCommonPort.setEnabled(mostCommonPortCount > -1);
		if (mostCommonPort == null)
			fieldMostCommonPort.setText("");
		else
			fieldMostCommonPort.setText(String.valueOf(mostCommonPort));

		labelMostCommonProtocol.setEnabled(mostCommonProtocolCount > -1);
		fieldMostCommonProtocol.setText(mostCommonProtocol);

		labelPacketLength.setEnabled(minPacketLength > -1);
		if (minPacketLength < 0)
			fieldPacketLength.setText("0 B / 0 B / 0 B");
		else
			fieldPacketLength.setText(Utilities.parseBytes(minPacketLength) + " / "
					+ Utilities.parseBytes(maxPacketLength) + " / "
					+ Utilities.parseBytes(Math.round(avgPacketLength)));
	}

	/**
	 * Custom JLabel subclass to act as a 'hyperlink' -- the mouse clicked
	 * function is abstract because the behaviour differs for each link.
	 * Supports enable/disabling correctly. isEnabled() is called by too many
	 * internal functions, so is not over-ridden. Therefore officially, the
	 * JLabel is always enabled.
	 */
	protected abstract class AbstractContextLink extends JLabel implements MouseListener {
		protected final Color activeColour = Color.blue.darker().darker();
		protected final Color inactiveColour = Color.darkGray.darker();
		protected boolean isEnabled = true;
		protected Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

		/**
		 * Construct a link label which displays the specified text
		 * 
		 * @param labelText
		 *            the text to show on the label
		 */
		public AbstractContextLink(String labelText) {
			super(labelText);
			setForeground(activeColour);
			addMouseListener(this);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setCursor(cursor);
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

		@Override
		public void setEnabled(boolean enable) {
			if (enable && !isEnabled) {
				setForeground(activeColour);
				cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				addMouseListener(this);
				isEnabled = true;
			} else if (!enable && isEnabled) {
				setForeground(inactiveColour);
				cursor = Cursor.getDefaultCursor();
				removeMouseListener(this);
				isEnabled = false;
			}
		}
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

		@SuppressWarnings("rawtypes")
		Class[] types = { String.class, Integer.class, Integer.class, Integer.class };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int columnIndex) {
			return this.types[columnIndex];
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

	@Override
	public void everythingEnds() {
	}

}
