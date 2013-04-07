package netvis.data.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
	Boolean includeOther;
	JComponent filterPanel;
	List<ListeningJCheckBoxMenuItem> checkBoxList;

	public ProtocolFilter(DataController dataController) {
		this.dataController = dataController;
		protocols = new HashMap<String, Boolean>();
		checkBoxList = new ArrayList<ListeningJCheckBoxMenuItem>();
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
		Iterator<ListeningJCheckBoxMenuItem> it = checkBoxList.iterator();
		while (it.hasNext()) {
			JCheckBoxMenuItem checkBox = it.next();
			protocols.put(checkBox.getText(), checkBox.isSelected());
		}
		
	}
	
	/** Builds the protocol menu
	 	> indicates the start of a sub-menu, takes the next string as the sub-menu name
	 	< indicates the end of a sub-menu */
	private String[] buildItems() {
		String[] items = {
			">", "AppleTalk Family",
				"AARP",
				"ADSP",
				"AFP",
				"ASP",
				"ATP",
				"DDP",
				"NBP",
				"RTMP",
				"PAP",
				"ZIP",
			"<",
			">", "Finance Industry Family",
				"ArcaDirect",
				"BOE",
				"FIX",
				"ITCH",
				"MEP",
				"MOLD64",
				"OUCH",
				"SoupBinTCP",
				"XPRS",
			"<",
			">", "Hardware Related",
				"ARP",
				"ATM",
				"Ethernet",
				"FDDI",
				"FibreChannel",
				"FR",
				"HSR",
				"I2C",
				"IEEE_802.15.4",
				"ISDN",
				"PRP",
				"TR",
				"USB",
				"Wi-Fi",
				"WLAN",
				"IEEE_802.11",
				"X.25",
			"<",
			">", "Identifier-Locator Family",
				"HIP",
				"LISP",
				"SHIM6",
			"<",
			">", "Internet (TCP/IP) Family",
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
					"SASP",
					"SNMP",
					"SSH",
					"Telnet",
					"TFTP",
				"<",
				">", "Link Layer",
					"ARP",
					"CSLIP",
					"PPP",
					"PPP-MP",
					"RARP",
					"SLIP",
				"<",
				">", "Network Layer",
					"AH",
					"BGP",
					"EGP",
					"ESP",
					"GGP",
					"ICMPv4",
					"ICMPv6",
					"IGMPv2",
					"IGRP",
					"IPv4",
					"IPv6",
					"ND",
					"OSPF",
					"RIP",
					"RIPng",
				"<",
				">", "Presentation Layer",
					"MIME",
				"<",
				">", "Session Layer",
					"DCE/RPC",
					"HTTP",
					"NetBIOS",
					"NetDump",
					"ONC-RPC",
					"SNTP",
				"<",
				">", "Transport Layer",
					"DCCP",
					"PortReference",
					"RTP",
					"RTCP",
					"SCTP",
					"TCP",
					"UDP",
					"UDP-Lite",
				"<",
			"<",
			">", "Inter-Process Commnunication Family",
				"LINX",
				"TIPC",
			"<",
			">", "Intelligent Platform Management Interface Family",
				"IPMB",
				"ICMB",
			"<",
			">", "ISO Family",
				">", "Core Services",
					"ACSE",
					"RTSE",
					"ROSE",
				"<",
				">", "Directory Services",
					"DAP",
					"DISP",
					"DOP",
					"DSP",
				"<",
				">", "Link Layer",
					"LLC",
				"<",
				">", "Messaging Services",
					"S4406",
					"X411",
					"X413",
					"X420",
				"<",
				">", "Miscellaneous",
					"CMIP",
					"FTAM",
					"MMS",
				"<",
				">", "Network Layer",
					"CLNP",
					"ES-IS",
					"IS-IS",
				"<",
				">", "Presentation Layer",
					"PRES",
				"<",
				">", "Session Layer",
					"SES",
				"<",
				">", "Transport Layer",
					"COTP",
					"CLTP",
					"TPKT",
				"<",
			"<",
			">", "LAN/MAN Family",
				">", "Link Layer",
					"Ethernet",
					"FiberDistributedDataInterface",
					"IEEE_802.11",
					"TokenRing",
				"<",
				">", "Miscellaneous",
					"GARP",
					"GMRP",
					"GVRP",
					"LLC",
					"MMRP",
					"MVRP",
					"STP",
					"TTEthernet",
					"VLAN",
				"<",
			"<",
			">", "Network Filesystem Family",
				">", "AFS Family",
					"AFS",
					"RX",
				"<",
				">", "CIFS (SMB) Family",
					">", "Core Protocols",
						"SMB",
						"SMB2",
						"ATSVC",
						"DSSETUP",
						"INITSHUTDOWN",
						"LSA",
						"NETLOGON",
						"SAMR",
						"SRVSVC",
						"WKSSVC",
						"WINREG",
						"Mailslot",
						"Pipe",
						"BrowserProtocol",
					"<",
					">", "Prerequisites",
						"MS-RAP",
						"DCE-RPC",
					"<",
				"<",
				">", "DCE/DFS",
					"DCE/RPC",
					"BUTC",
				"<",
				">", "NFS Family",
					">", "Core NFS Protocols",
						"NFS",
						"KLM",
					"<",
					">", "NFS v2/3 Helper Protocols",
						"MOUNT",
						"NSM",
						"NLM",
					"<",
					">", "Extra NFS Helper Protocols",
						"NFS-AUTH",
						"NFS-ACL",
						"RQUOTA",
					"<",
				"<",
			"<",
			">", "Novell Protocol Family",
				"IPX",
				"IPXRIP",
				"IPXSAP",
				"NBIPX",
				"NCP",
				"NLSP",
				"NMPI",
				"SPX",
			"<",
			">", "OPC Family",
				">", "DCOM Based",
					"OPC AE",
					"OPC Commands",
					"OPC DA",
					"OPC HDA",
				"<",
				">", "SOAP Based",
					"OPC XML-DA",
				"<",
				">", "TCP/IP Based",
					"OPC UA",
				"<",
			"<",
			">", "Remote Procedure Call Family",
				"CORBA",
				"DCE/RPC",
				"DCOM",
				"ONC-RPC",
				"RX",
				"SOAP",
				"XML-RPC",
			"<",
			">", "Signaling Transport Family",
				"ASAP",
				"BICC",
				"DUA",
				"ENRP",
				"H248",
				"H323",
				"ISDN",
				"ISUP",
				"IUA",
				"M2UA",
				"M2PA",
				"M2TP",
				"M3UA",
				"MTP3",
				"Q.921",
				"Q.931",
				"RUDP",
				"SCCP",
				"SCTP",
				"SUA",
				"TALI",
				"V5UA",
			"<",
			">", "Storage Family",
				">", "Block Protocols",
					"SCSI",
					"ATA",
				"<",
				">", "Transports",
					">", "Ethernet Storage Family",
						"HyperSCSI",
						"AOE",
						"FCoE",
					"<",
					">", "Fibre Channel Family",
						"FibreChannel",
					"<",
					">", "IP Storage Family",
						"FCIP",
						"iFCP",
						"iSCSI",
						"iSNS",
						"NBD",
					"<",
				"<",
				">", "Backup Protocols",
					"NDMP",
				"<",
			"<",
			">", "Unsorted",
				"ACN",
				"AMQP",
				"Bluetooth",
				"C12.22",
				"Cisco-IPSLA",
				"CIGI",
				"CMP",
				"DECT",
				"DICOM",
				"DVB-S2",
				"DVB-CI",
				"FastCGI",
				"FP Hint",
				"HART-IP",
				"HDCP",
				"Gearman",
				"ISMACryp",
				"IEEE C37.118",
				"kNet",
				"LDAP",
				"LLRP",
				"META",
				"MS-LLTD",
				"Netsync",
				"OMRON-FINS",
				"OpenVPN",
				"Oracle",
				"PCP",
				"SAMETIME",
				"SIMULCRYPT",
				"SML",
				"SSTP",
				"STANAG_5066",
				"TeamSpeak2",
				"Tor",
				"UCP",
				"Ventrilo",
				"VXI-11",
				"WOL",
			"<",
			">", "Vendor-Specific Family",
				">", "Allied Telesis",
					"EPSR",
				"<",
				">", "Cisco",
					"CDP",
					"DTP",
					"PagP",
					"STP",
					"UDLD",
					"VTP",
				"<",
				">", "Enterasys",
					"ISMP",
				"<",
				">", "Extreme Networks",
					"EAPS",
					"EDP",
					"ESRP",
				"<",
				">", "Foundry",
					"FDP",
					"VSRP",
				"<",
				">", "Huawei",
					"RRPP",
				"<",
				">", "Nortel",
					"DSMLT",
					"IST",
					"MRP",
					"MLT",
					"RSMLT",
					"SMLT",
					"SONMP",
					"VLACP",
				"<",
				">", "Raptor",
					"RAST",
				"<",
			"<",
			">", "VOIP Family",
				">", "Authentication, Authorisation and Accounting",
					"RADIUS",
					"DIAMETER",
				"<",
				">", "Call Control",
					">", "SIP Family",
						"RTSP",
						"SDP",
						"Sigcomp",
						"SIP",
					"<",
					">", "H.248 Family",
						"H248/MEGACO",
						"MGCP",
					"<",
					">", "H.323 Family",
						"H223",
						"H225",
						"H235",
						"H245",
						"H323",
						"H450",
						"Q.931",
					"<",
					">", "Proprietary Protocols",
						"IAX2",
						"SKINNY",
						"UNISTIM",
					"<",
					">", "SIGTRAN Family",
						"SIGTRAN",
					"<",
				"<",
				">", "Transport",
					"MIKEY",
					"RTCP",
					"RTP",
					"SRTP",
					"T38",
				"<",
			"<",
			">", "WAN Family",
				"ATM",
				"Cisco HDLC",
				"FrameRelay",
				"PPP",
				"X.25",
			"<",
			">", "WAP Family",
				"MMSE",
				"WBXML",
				"WDP",
				"WSP",
				"WTLS",
				"WTP",
			"<",
			">", "WiMAX Family",
				"WIMAXASNCP",
			"<",
			">", "WPAN",
				"IEEE_802.15.4",
				"ZigBee",
			"<",
			"Other" // Do not remove, will cause null pointer exception below
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
				ListeningJCheckBoxMenuItem checkBox = new ListeningJCheckBoxMenuItem(name, true);
				// Check if there's a duplicate somewhere else in the list - if so, make them listen to each other
				Iterator<ListeningJCheckBoxMenuItem> it = checkBoxList.iterator();
				while(it.hasNext()) {
					ListeningJCheckBoxMenuItem next = it.next();
					if(next.getText() == name) {
						next.addActionListener(checkBox);
						checkBox.addActionListener(next);
					}
				}
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
		Iterator<ListeningJCheckBoxMenuItem> it = checkBoxList.iterator();
		while (it.hasNext()) {
			ListeningJCheckBoxMenuItem checkBox = it.next();
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
	
	private class ListeningJCheckBoxMenuItem extends JCheckBoxMenuItem implements ActionListener {

		public ListeningJCheckBoxMenuItem(String name, boolean b) {
			super(name, b);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ListeningJCheckBoxMenuItem source = (ListeningJCheckBoxMenuItem)e.getSource();
			if(source.isSelected() != isSelected()) setSelected(source.isSelected());
		}
			
	}

}
