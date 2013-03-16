package netvis.data.filters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

public class ProtocolFilter implements PacketFilter{
	
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
		
		
		// Create checkboxes for each protocol and add them to nested menus
		final JPopupMenu rootMenu = new JPopupMenu();
		// TCP/IP family
		JMenu tcpipMenu = new JMenu("TCP/IP family");
		rootMenu.add(tcpipMenu);
		// Link Layer
		JMenu linkLayerMenu = new JMenu("Link Layer");
		tcpipMenu.add(linkLayerMenu);
		JCheckBoxMenuItem checkBoxARP = new JCheckBoxMenuItem("ARP", true);
		checkBoxList.add(checkBoxARP);
		linkLayerMenu.add(checkBoxARP);
		JCheckBoxMenuItem checkBoxRARP = new JCheckBoxMenuItem("RARP", true);
		checkBoxList.add(checkBoxRARP);
		linkLayerMenu.add(checkBoxRARP);
		JCheckBoxMenuItem checkBoxCSLIP = new JCheckBoxMenuItem("CSLIP", true);
		checkBoxList.add(checkBoxCSLIP);
		linkLayerMenu.add(checkBoxCSLIP);
		JCheckBoxMenuItem checkBoxPPP = new JCheckBoxMenuItem("PPP", true);
		checkBoxList.add(checkBoxPPP);
		linkLayerMenu.add(checkBoxPPP);
		JCheckBoxMenuItem checkBoxPPPMP = new JCheckBoxMenuItem("PPP-MP", true);
		checkBoxList.add(checkBoxPPPMP);
		linkLayerMenu.add(checkBoxPPPMP);
		JCheckBoxMenuItem checkBoxSLIP = new JCheckBoxMenuItem("SLIP", true);
		checkBoxList.add(checkBoxSLIP);
		linkLayerMenu.add(checkBoxSLIP);
		
		// Network Layer
		JMenu networkLayerMenu = new JMenu("Network Layer");
		tcpipMenu.add(networkLayerMenu);
		JCheckBoxMenuItem checkBoxIPv4 = new JCheckBoxMenuItem("IPv4", true);
		checkBoxList.add(checkBoxIPv4);
		networkLayerMenu.add(checkBoxIPv4);
		JCheckBoxMenuItem checkBoxIPv6 = new JCheckBoxMenuItem("IPv6", true);
		checkBoxList.add(checkBoxIPv6);
		networkLayerMenu.add(checkBoxIPv6);
		JCheckBoxMenuItem checkBoxICMPv4 = new JCheckBoxMenuItem("ICMPv4", true);
		checkBoxList.add(checkBoxICMPv4);
		networkLayerMenu.add(checkBoxICMPv4);
		JCheckBoxMenuItem checkBoxICMPv6 = new JCheckBoxMenuItem("ICMPv6", true);
		checkBoxList.add(checkBoxICMPv6);
		networkLayerMenu.add(checkBoxICMPv6);
		JCheckBoxMenuItem checkBoxIGMPv2 = new JCheckBoxMenuItem("IGMPv2", true);
		checkBoxList.add(checkBoxIGMPv2);
		networkLayerMenu.add(checkBoxIGMPv2);
		JCheckBoxMenuItem checkBoxBGP = new JCheckBoxMenuItem("BGP", true);
		checkBoxList.add(checkBoxBGP);
		networkLayerMenu.add(checkBoxBGP);
		JCheckBoxMenuItem checkBoxEGP = new JCheckBoxMenuItem("EGP", true);
		checkBoxList.add(checkBoxEGP);
		networkLayerMenu.add(checkBoxEGP);
		JCheckBoxMenuItem checkBoxGGP = new JCheckBoxMenuItem("GGP", true);
		checkBoxList.add(checkBoxGGP);
		networkLayerMenu.add(checkBoxGGP);
		JCheckBoxMenuItem checkBoxIGRP = new JCheckBoxMenuItem("IGRP", true);
		checkBoxList.add(checkBoxIGRP);
		networkLayerMenu.add(checkBoxIGRP);
		JCheckBoxMenuItem checkBoxND = new JCheckBoxMenuItem("ND", true);
		checkBoxList.add(checkBoxND);
		networkLayerMenu.add(checkBoxND);
		JCheckBoxMenuItem checkBoxOSPF = new JCheckBoxMenuItem("OSPF", true);
		checkBoxList.add(checkBoxOSPF);
		networkLayerMenu.add(checkBoxOSPF);
		JCheckBoxMenuItem checkBoxRIP = new JCheckBoxMenuItem("RIP", true);
		checkBoxList.add(checkBoxRIP);
		networkLayerMenu.add(checkBoxRIP);
		JCheckBoxMenuItem checkBoxRIPng = new JCheckBoxMenuItem("RIPng", true);
		checkBoxList.add(checkBoxRIPng);
		networkLayerMenu.add(checkBoxRIPng);
		JCheckBoxMenuItem checkBoxAH = new JCheckBoxMenuItem("AH", true);
		checkBoxList.add(checkBoxAH);
		networkLayerMenu.add(checkBoxAH);
		JCheckBoxMenuItem checkBoxESP = new JCheckBoxMenuItem("ESP", true);
		checkBoxList.add(checkBoxESP);
		networkLayerMenu.add(checkBoxESP);
		
		// Transport Layer
		JMenu transportLayerMenu = new JMenu("Transport Layer");
		tcpipMenu.add(transportLayerMenu);
		JCheckBoxMenuItem checkBoxDCCP = new JCheckBoxMenuItem("DCCP", true);
		checkBoxList.add(checkBoxDCCP);
		transportLayerMenu.add(checkBoxDCCP);
		JCheckBoxMenuItem checkBoxSCTP = new JCheckBoxMenuItem("SCTP", true);
		checkBoxList.add(checkBoxSCTP);
		transportLayerMenu.add(checkBoxSCTP);
		JCheckBoxMenuItem checkBoxUDP = new JCheckBoxMenuItem("UDP", true);
		checkBoxList.add(checkBoxUDP);
		transportLayerMenu.add(checkBoxUDP);
		JCheckBoxMenuItem checkBoxUDPLite = new JCheckBoxMenuItem("UDP-Lite", true);
		checkBoxList.add(checkBoxUDPLite);
		transportLayerMenu.add(checkBoxUDPLite);
		JCheckBoxMenuItem checkBoxTCP = new JCheckBoxMenuItem("TCP", true);
		checkBoxList.add(checkBoxTCP);
		transportLayerMenu.add(checkBoxTCP);
		JCheckBoxMenuItem checkBoxPortReference = new JCheckBoxMenuItem("PortReference", true);
		checkBoxList.add(checkBoxPortReference);
		transportLayerMenu.add(checkBoxPortReference);
		JCheckBoxMenuItem checkBoxRTP = new JCheckBoxMenuItem("RTP", true);
		checkBoxList.add(checkBoxRTP);
		transportLayerMenu.add(checkBoxRTP);
		JCheckBoxMenuItem checkBoxRTCP = new JCheckBoxMenuItem("RTCP", true);
		checkBoxList.add(checkBoxRTCP);
		transportLayerMenu.add(checkBoxRTCP);
		
		// Session Layer
		JMenu sessionLayerMenu = new JMenu("Session Layer");
		tcpipMenu.add(sessionLayerMenu);
		JCheckBoxMenuItem checkBoxNetBIOS = new JCheckBoxMenuItem("NetBIOS", true);
		checkBoxList.add(checkBoxNetBIOS);
		sessionLayerMenu.add(checkBoxNetBIOS);
		JCheckBoxMenuItem checkBoxNetDump = new JCheckBoxMenuItem("NetDump", true);
		checkBoxList.add(checkBoxNetDump);
		sessionLayerMenu.add(checkBoxNetDump);
		JCheckBoxMenuItem checkBoxONCRPC = new JCheckBoxMenuItem("ONC-RPC", true);
		checkBoxList.add(checkBoxONCRPC);
		sessionLayerMenu.add(checkBoxONCRPC);
		JCheckBoxMenuItem checkBoxDCERPC = new JCheckBoxMenuItem("DCE/RPC", true);
		checkBoxList.add(checkBoxDCERPC);
		sessionLayerMenu.add(checkBoxDCERPC);
		JCheckBoxMenuItem checkBoxHTTP = new JCheckBoxMenuItem("HTTP", true);
		checkBoxList.add(checkBoxHTTP);
		sessionLayerMenu.add(checkBoxHTTP);
		JCheckBoxMenuItem checkBoxSNTP = new JCheckBoxMenuItem("SNTP", true);
		checkBoxList.add(checkBoxSNTP);
		sessionLayerMenu.add(checkBoxSNTP);
		
		// Presentation Layer
		JMenu presentationLayerMenu = new JMenu("Presentation Layer");
		tcpipMenu.add(presentationLayerMenu);
		JCheckBoxMenuItem checkBoxMIME = new JCheckBoxMenuItem("MIME", true);
		checkBoxList.add(checkBoxMIME);
		presentationLayerMenu.add(checkBoxMIME);
		
		// Application Layer
		JMenu applicationLayerMenu = new JMenu("Application Layer");
		tcpipMenu.add(applicationLayerMenu);
		JCheckBoxMenuItem checkBoxANCP = new JCheckBoxMenuItem("ANCP", true);
		checkBoxList.add(checkBoxANCP);
		applicationLayerMenu.add(checkBoxANCP);
		JCheckBoxMenuItem checkBoxBOOTP = new JCheckBoxMenuItem("BOOTP", true);
		checkBoxList.add(checkBoxBOOTP);
		applicationLayerMenu.add(checkBoxBOOTP);
		JCheckBoxMenuItem checkBoxDHCP = new JCheckBoxMenuItem("DHCP", true);
		checkBoxList.add(checkBoxDHCP);
		applicationLayerMenu.add(checkBoxDHCP);
		JCheckBoxMenuItem checkBoxDNS = new JCheckBoxMenuItem("DNS", true);
		checkBoxList.add(checkBoxDNS);
		applicationLayerMenu.add(checkBoxDNS);
		JCheckBoxMenuItem checkBoxFTP = new JCheckBoxMenuItem("FTP", true);
		checkBoxList.add(checkBoxFTP);
		applicationLayerMenu.add(checkBoxFTP);
		JCheckBoxMenuItem checkBoxIMAP = new JCheckBoxMenuItem("IMAP", true);
		checkBoxList.add(checkBoxIMAP);
		applicationLayerMenu.add(checkBoxIMAP);
		JCheckBoxMenuItem checkBoxiWARPDDP = new JCheckBoxMenuItem("iWARP-DDP", true);
		checkBoxList.add(checkBoxiWARPDDP);
		applicationLayerMenu.add(checkBoxiWARPDDP);
		JCheckBoxMenuItem checkBoxiWARPMPA = new JCheckBoxMenuItem("iWARP-MPA", true);
		checkBoxList.add(checkBoxiWARPMPA);
		applicationLayerMenu.add(checkBoxiWARPMPA);
		JCheckBoxMenuItem checkBoxiWARPRDMAP = new JCheckBoxMenuItem("iWARP-RDMAP", true);
		checkBoxList.add(checkBoxiWARPRDMAP);
		applicationLayerMenu.add(checkBoxiWARPRDMAP);
		JCheckBoxMenuItem checkBoxNNTP = new JCheckBoxMenuItem("NNTP", true);
		checkBoxList.add(checkBoxNNTP);
		applicationLayerMenu.add(checkBoxNNTP);
		JCheckBoxMenuItem checkBoxNTP = new JCheckBoxMenuItem("NTP", true);
		checkBoxList.add(checkBoxNTP);
		applicationLayerMenu.add(checkBoxNTP);
		JCheckBoxMenuItem checkBoxPANA= new JCheckBoxMenuItem("PANA", true);
		checkBoxList.add(checkBoxPANA);
		applicationLayerMenu.add(checkBoxPANA);
		JCheckBoxMenuItem checkBoxPOP = new JCheckBoxMenuItem("POP", true);
		checkBoxList.add(checkBoxPOP);
		applicationLayerMenu.add(checkBoxPOP);
		JCheckBoxMenuItem checkBoxRADIUS = new JCheckBoxMenuItem("RADIUS", true);
		checkBoxList.add(checkBoxRADIUS);
		applicationLayerMenu.add(checkBoxRADIUS);
		JCheckBoxMenuItem checkBoxRLogin = new JCheckBoxMenuItem("RLogin", true);
		checkBoxList.add(checkBoxRLogin);
		applicationLayerMenu.add(checkBoxRLogin);
		JCheckBoxMenuItem checkBoxRSH = new JCheckBoxMenuItem("RSH", true);
		checkBoxList.add(checkBoxRSH);
		applicationLayerMenu.add(checkBoxRSH);
		JCheckBoxMenuItem checkBoxRSIP = new JCheckBoxMenuItem("RSIP", true);
		checkBoxList.add(checkBoxRSIP);
		applicationLayerMenu.add(checkBoxRSIP);
		JCheckBoxMenuItem checkBoxSSH = new JCheckBoxMenuItem("SSH", true);
		checkBoxList.add(checkBoxSSH);
		applicationLayerMenu.add(checkBoxSSH);
		JCheckBoxMenuItem checkBoxSNMP = new JCheckBoxMenuItem("SNMP", true);
		checkBoxList.add(checkBoxSNMP);
		applicationLayerMenu.add(checkBoxSNMP);
		JCheckBoxMenuItem checkBoxTelnet = new JCheckBoxMenuItem("Telnet", true);
		checkBoxList.add(checkBoxTelnet);
		applicationLayerMenu.add(checkBoxTelnet);
		JCheckBoxMenuItem checkBoxTFTP = new JCheckBoxMenuItem("TFTP", true);
		checkBoxList.add(checkBoxTFTP);
		applicationLayerMenu.add(checkBoxTFTP);
		JCheckBoxMenuItem checkBoxSASP = new JCheckBoxMenuItem("SASP", true);
		checkBoxList.add(checkBoxSASP);
		applicationLayerMenu.add(checkBoxSASP);
		
		// All protocols not in the list
		JCheckBoxMenuItem checkBoxOther = new JCheckBoxMenuItem("Other", true);
		checkBoxList.add(checkBoxOther);
		rootMenu.add(checkBoxOther);
		
		filterPanel = new JPanel();
		final JButton rootButton = new JButton("Filter by protocol");
		rootButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				rootMenu.show(rootButton, rootButton.getX(), rootButton.getY());
			}
			
		});
		
		filterPanel.add(rootButton);
		
		// Initialise the hashmap
		Iterator<JCheckBoxMenuItem> it = checkBoxList.iterator();
		while(it.hasNext()) {
			JCheckBoxMenuItem checkBox = it.next();
			protocols.put(checkBox.getText(), checkBox.isSelected());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Iterate along all checkboxes and update the hashmap accordingly
		Iterator<JCheckBoxMenuItem> it = checkBoxList.iterator();
		while(it.hasNext()) {
			JCheckBoxMenuItem checkBox = it.next();
			protocols.put(checkBox.getText(), checkBox.isSelected());
		}
		dataController.filterUpdated();
	}

	@Override
	public boolean filter(Packet packet) {
		String packetProtocol = packet.protocol;
		if(protocols.containsKey(packetProtocol)) return protocols.get(packetProtocol);
		else return protocols.get("Other");
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
