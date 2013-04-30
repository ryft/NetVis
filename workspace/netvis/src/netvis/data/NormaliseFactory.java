package netvis.data;

import java.util.Arrays;
import java.util.List;

import netvis.data.model.Packet;

/**
 * A helper function that creates classes which return a certain normalised
 * parameter of the packet
 */
public class NormaliseFactory {
	public static final NormaliseFactory INSTANCE = new NormaliseFactory();

	List<String> attrs;

	private NormaliseFactory() {
		attrs = Arrays.asList("Source Port", "Destination Port", "Source IP", "Destination IP");
	}

	public List<String> getAttrs() {
		return attrs;
	}

	public Normaliser getNormaliser(int norm_id) {
		switch (norm_id) {
		case 0:
			return new SourcePortNorm();
		case 1:
			return new DestinationPortNorm();
		case 2:
			return new SourceIPNorm();
		case 3:
			return new DestinationIPNorm();
		default:
			return null;
		}

	}

	public interface Normaliser {
		public double normalise(Packet p);

		public String name();
	}

	private class SourcePortNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normalisePort(p.sport);
		}

		public String name() {
			return attrs.get(0);
		}
	}

	private class DestinationPortNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normalisePort(p.dport);
		}

		public String name() {
			return attrs.get(1);
		}
	}

	private class SourceIPNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normaliseV4Ip(p.sip);
		}

		public String name() {
			return attrs.get(2);
		}
	}

	private class DestinationIPNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normaliseV4Ip(p.dip);
		}

		public String name() {
			return attrs.get(3);
		}
	}

}
