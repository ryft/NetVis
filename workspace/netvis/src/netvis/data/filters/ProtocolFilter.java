package netvis.data.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import netvis.data.DataController;
import netvis.data.model.Packet;
import netvis.data.model.PacketFilter;

public class ProtocolFilter implements PacketFilter {

	final DataController dataController;
	HashMap<String, Boolean> protocols;
	HashMap<JCheckBox, String> checkBoxes;
	Boolean includeOther;
	JComponent filterPanel;
	List<JCheckBoxMenuItem> checkBoxList;

	public ProtocolFilter(DataController dataController) {
		this.dataController = dataController;
		protocols = new HashMap<String, Boolean>();
		checkBoxList = new ArrayList<JCheckBoxMenuItem>();
		String[] allItems = buildItems();

		// Create checkboxes for each protocol and add them to nested menus
		final JPopupMenu rootMenu = new JPopupMenu();
		
		addItems(rootMenu,  allItems);

		filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
		filterPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		final JButton rootButton = new JButton("Filter by protocol");
		rootButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				rootMenu.show(rootButton, rootButton.getX(), rootButton.getY());
			}

		});

		rootButton.setSize(100, 100);
		filterPanel.add(rootButton);

		// Initialise the hashmap
		Iterator<JCheckBoxMenuItem> it = checkBoxList.iterator();
		while (it.hasNext()) {
			JCheckBoxMenuItem checkBox = it.next();
			protocols.put(checkBox.getText(), checkBox.isSelected());
		}
		
	}
	
	private String[] buildItems() {
		String[] items = {">", "TCP/IP family",
				">", "Link Layer",
					"ARP",
					"RARP",
					"CSLIP",
					"PPP",
					"PPP-MP",
					"SLIP",
				"<",
				">", "Network Layer",
					"IPv4",
					"IPv6",
					"ICMPv4",
					"ICMPv6",
					"IGMPv2",
					"BGP",
					"EGP",
					"GGP",
					"IGRP",
					"ND",
					"OSPF",
					"RIP",
					"RIPng",
					"AH",
					"ESP",
				"<",
				">", "Transport Layer",
					"DCCP",
					"SCTP",
					"UDP",
					"UDP-Lite",
					"TCP",
					"PortReference",
					"RTP",
					"RTCP",
				"<",
				">", "Session Layer",
					"NetBIOS",
					"NetDump",
					"ONC-RPC",
					"DCE/RPC",
					"HTTP",
					"SNTP",
				"<",
				">", "Presentation Layer",
					"MIME",
				"<",
				">", "Application Layer",
					"ANCP",
					"BOOTP",
					"DHCP",
					"DNS",
					"FTP",
					"IMAP",
					"iWARP-DDP",
					"iWARP-MPA",
					"iWARP-RDMAP",
					"NNTP",
					"NTP",
					"PANA",
					"POP",
					"RADIUS",
					"RLogin",
					"RSH",
					"RSIP",
					"SSH",
					"SNMP",
					"Telnet",
					"TFTP",
					"SASP",
				"<",
			"<",
			"Other"
			};
		return items;
	}
	
	private int addItems(JComponent rootMenu, String[] items) {
		int length = 0;
		int waitFor = 0;
		for(int i = 0; i < items.length; i++) {
			String name = items[i];
			if(waitFor != 0) waitFor--;
			else if(name == ">") {
				JMenu newMenu = new JMenu(items[i+1]);
				newMenu.setName(items[i+1]);
				rootMenu.add(newMenu);
				String[] newArray = new String[items.length - (i+2)];
				System.arraycopy(items, i+2, newArray, 0, items.length - (i+2));
				waitFor = addItems(newMenu, newArray) + 2;
				length += waitFor + 1;
			}
			else if(name == "<") {
				return length;
			}
			else {
				JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(name, true);
				checkBoxList.add(checkBox);
				rootMenu.add(checkBox);
				length += 1;
			}
		}
		return length;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Iterate along all checkboxes and update the hashmap accordingly
		Iterator<JCheckBoxMenuItem> it = checkBoxList.iterator();
		while (it.hasNext()) {
			JCheckBoxMenuItem checkBox = it.next();
			protocols.put(checkBox.getText(), checkBox.isSelected());
		}
		dataController.filterUpdated();
	}

	@Override
	public boolean filter(Packet packet) {
		String packetProtocol = packet.protocol;
		if (protocols.containsKey(packetProtocol))
			return protocols.get(packetProtocol);
		else
			return protocols.get("Other");
	}

	@Override
	public String description() {
		return "Filter by protocol type";
	}

	@Override
	public JComponent getPanel() {
		return filterPanel;
	}

}
