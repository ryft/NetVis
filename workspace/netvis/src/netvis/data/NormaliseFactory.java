package netvis.data;

import java.util.ArrayList;
import java.util.List;

import netvis.data.model.Packet;

/**
 * A helper function that creates classes which return a certain normalised
 * parameter of the packet
 */
public class NormaliseFactory {
	public static final NormaliseFactory INSTANCE = new NormaliseFactory();

	List<Normaliser> normalisers;

	private NormaliseFactory() {
		normalisers = new ArrayList<Normaliser>();
		normalisers.add(new SourcePortNorm());
		normalisers.add(new DestinationPortNorm());
		normalisers.add(new SourceIPNorm());
		normalisers.add(new DestinationIPNorm());
		normalisers.add(new SourceMACNorm());
		normalisers.add(new DestinationMACNorm());
	}

	public List<Normaliser> getNormalisers() {
		return normalisers;
	}
	public int getIndex(Normaliser n){
		return normalisers.lastIndexOf(n);
	}
	public Normaliser getNormaliser(int norm_id) {
		if (norm_id < normalisers.size())
			return normalisers.get(norm_id);
		else
			return null;
	}

	public interface Normaliser {
		public double normalise(Packet p);
		public String denormalise (double v);
		public String name();
	}

	private class SourcePortNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normalisePort(p.sport);
		}

		public String name() {
			return "Source Port";
		}

		@Override
		public String denormalise(double v) {
			return DataUtilities.denormalisePort(v);
		}
	}

	private class DestinationPortNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normalisePort(p.dport);
		}

		public String name() {
			return "Destination Port";
		}

		@Override
		public String denormalise(double v) {
			return DataUtilities.denormalisePort(v);
		}
	}

	private class SourceIPNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normaliseV4Ip(p.sip);
		}

		public String name() {
			return "Source IP";
		}

		@Override
		public String denormalise(double v) {
			return DataUtilities.denormaliseV4Ip(v);
		}
	}

	private class DestinationIPNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normaliseV4Ip(p.dip);
		}

		public String name() {
			return "Destination Port";
		}

		@Override
		public String denormalise(double v) {
			return DataUtilities.denormaliseV4Ip(v);
		}
	}
	
	private class SourceMACNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normaliseMAC(p.smac);
		}

		public String name() {
			return "Source MAC Address";
		}

		@Override
		public String denormalise(double v) {
			return "Randomised";
		}
	}
	
	private class DestinationMACNorm implements Normaliser {
		public double normalise(Packet p) {
			return DataUtilities.normaliseMAC(p.dmac);
		}

		public String name() {
			return "Destination MAC Adress";
		}

		@Override
		public String denormalise(double v) {
			return "Randomised";
		}
	}

}
