package netvis.visualisations.comets;

import java.util.ArrayList;

import netvis.data.model.Packet;

public class Candidate {

	// How close the client is to the internal network 0-closest 10-furthest
	public int proximity;

	// How much data it send throughout the last interval
	public int datasize;

	// Source and destination
	public String sip, dip;
	
	private ArrayList<Packet> plist;

	public Candidate(int prox, int dat, String s, String d) {
		proximity = prox;
		datasize = dat;

		sip = s;
		dip = d;
		
		plist = new ArrayList<Packet>();
	}
	
	public void RegisterPacket (Packet p)
	{
		datasize += p.length;
		plist.add (p);
	}
	
	public ArrayList<Packet> GetWaitingPackets ()
	{
		return plist;
	}
	
	public void ResetWaitingPackets ()
	{
		plist = new ArrayList<Packet>();
	}
}