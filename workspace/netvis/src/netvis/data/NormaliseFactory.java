package netvis.data;

import java.util.ArrayList;
import java.util.List;

import netvis.data.filters.NormalisationFilter;
import netvis.data.model.Packet;

/**
 * A helper function that creates classes which return a certain normalised
 * parameter of the packet
 */
public class NormaliseFactory {
	public static final NormaliseFactory INSTANCE = new NormaliseFactory();

	List<Normaliser> normalisers;

	private DataController dataController;

	private NormaliseFactory() {
		normalisers = new ArrayList<Normaliser>();
		normalisers.add(new SourceMACNorm());
		normalisers.add(new SourceIPNorm());
		normalisers.add(new SourcePortNorm());
		normalisers.add(new DestinationPortNorm());
		normalisers.add(new DestinationIPNorm());
		normalisers.add(new DestinationMACNorm());	
		normalisers.add(new ProtocolNorm());

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

	public abstract class Normaliser {
		double lowerBound = 0, upperBound = 1;
		NormalisationFilter myFilter = null;
		protected abstract double normaliseFunction(Packet p);
		protected abstract String denormaliseFunction (double v);
		public abstract String name();
		public double normalise(Packet p){
			double v = this.normaliseFunction(p);
			v = (v - lowerBound)/(upperBound - lowerBound);
			//System.out.println(lowerBound);
			//System.out.println(v);
	
			return v;
		}
		public String denormalise(double v){
			v = v * (upperBound - lowerBound);
			v += lowerBound;
			return denormaliseFunction(v);
		}
		public void filter(double lowerBound, double upperBound){		
			double interval = this.upperBound - this.lowerBound;
			this.upperBound = this.lowerBound + upperBound*interval;
				if (this.upperBound > 1) this.upperBound = 1;
			this.lowerBound += lowerBound*interval;
				if (this.lowerBound < 0) this.lowerBound = 0;
			if (myFilter != null)
				dataController.removeFilter(myFilter);
			myFilter = new NormalisationFilter(this);
			dataController.addFilter(myFilter);
		}
		public void clearFilter() {
			this.lowerBound = 0;
			this.upperBound = 1;
			dataController.removeFilter(myFilter);
			myFilter = null;
		}
	}

	private class SourcePortNorm extends Normaliser {
		public double normaliseFunction(Packet p) {
			return DataUtilities.normalisePort(p.sport);
		}

		public String name() {
			return "Src Port";
		}

		@Override
		public String denormaliseFunction(double v) {
			return DataUtilities.denormalisePort(v);
		}
	}

	private class DestinationPortNorm extends Normaliser {
		public double normaliseFunction(Packet p) {
			return DataUtilities.normalisePort(p.dport);
		}

		public String name() {
			return "Dest Port";
		}

		@Override
		public String denormaliseFunction(double v) {
			return DataUtilities.denormalisePort(v);
		}
	}

	private class SourceIPNorm extends Normaliser {
		public double normaliseFunction(Packet p) {
			return DataUtilities.normaliseIP(p.sip);
		}

		public String name() {
			return "Src IP";
		}

		@Override
		public String denormaliseFunction(double v) {
			return DataUtilities.denormaliseIP(v);
		}
	}

	private class DestinationIPNorm extends Normaliser {
		public double normaliseFunction(Packet p) {
			return DataUtilities.normaliseIP(p.dip);
		}

		public String name() {
			return "Dest IP";
		}

		@Override
		public String denormaliseFunction(double v) {
			return DataUtilities.denormaliseIP(v);
		}
	}
	
	private class SourceMACNorm extends Normaliser {
		public double normaliseFunction(Packet p) {
			if (DataUtilities.macMap.containsKey(p.smac))
				return DataUtilities.macMap.get(p.smac);
			else {
				double value = DataUtilities.normaliseIP(p.sip);
				value += (Math.random()/100); 
				if (value < 0) value = -value;
				if (value > 1) value = 2 - value;
				DataUtilities.macMap.put(p.smac, value);
				return value;
			}

		}

		public String name() {
			return "Src MAC Address";
		}

		@Override
		public String denormaliseFunction(double v) {
			return String.valueOf(v);
		}
	}
	private class ProtocolNorm extends Normaliser {

		@Override
		protected double normaliseFunction(Packet p) {
			return DataUtilities.normaliseProtocol(p.protocol);
		}

		@Override
		protected String denormaliseFunction(double v) {
			return String.valueOf(v);
		}

		@Override
		public String name() {
			// TODO Auto-generated method stub
			return "Protocol";
		}
		
	}
	private class DestinationMACNorm extends Normaliser {
		public double normaliseFunction(Packet p) {
			return DataUtilities.normaliseMAC(p.dmac);
		}

		public String name() {
			return "Dest MAC Adress";
		}

		@Override
		public String denormaliseFunction(double v) {
			return String.valueOf(v);
		}
	}

	public void setDataController(DataController dataController) {
		this.dataController = dataController;
	}

}
