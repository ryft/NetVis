package netvis.data.model;

public class Packet {
	public final int no, sport, dport, length;
	public final double time;
	public final String smac, dmac, info, protocol, sip, dip;

	/**
	 * 
	 * @param no
	 *            Packet number
	 * @param time
	 *            Time elapsed since first packet (seconds)
	 * @param sip
	 *            Source IPv4/6 address
	 * @param smac
	 *            Source hardware (MAC) address
	 * @param sport
	 *            Source Port
	 * @param dip
	 *            Destination IPv4/6 address
	 * @param dmac
	 *            Destination hardware (MAC) address
	 * @param dport
	 *            Destination Port
	 * @param protocol
	 *            Communication protocol
	 * @param length
	 *            Packet Length(Bytes)
	 * @param info
	 *            Detected description of packet purpose
	 */
	public Packet(int no, double time, String sip, String smac, int sport, String dip, String dmac,
			int dport, String protocol, int length, String info) {
		this.no = no;
		this.time = time;
		this.sip = sip;
		this.smac = smac;
		this.sport = sport;
		this.dip = dip;
		this.dmac = dmac;
		this.dport = dport;
		this.protocol = protocol;
		this.length = length;
		this.info = info;
	}

}
