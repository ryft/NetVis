package netvis.data.filters;

import netvis.data.DataController;
import netvis.data.NormaliseFactory.Normaliser;
import netvis.data.model.Packet;

public class NormalisationFilter extends FixedFilter{

	private final Normaliser norm;
	private final double lowerBound, upperBound;
	public NormalisationFilter(Normaliser norm, double lowerBound, 
			double upperBound, DataController dc){
		super(dc,"<html>" + norm.name() + ":<br>" + 
				String.valueOf(norm.denormalise(lowerBound)) + "-" +
				String.valueOf(norm.denormalise(upperBound))+ "</html>");

		this.norm = norm;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	@Override
	public boolean filter(Packet packet) {
		double v = norm.normalise(packet);
		return (v <= upperBound && v >= lowerBound);
	}
}
