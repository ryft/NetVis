package netvis.data.filters;

import netvis.data.NormaliseFactory.Normaliser;
import netvis.data.model.Packet;

public class NormalisationFilter extends FixedFilter{

	private final Normaliser norm;
	public NormalisationFilter(Normaliser norm){
		super("<html>" + norm.name() + ":<br>" + 
				String.valueOf(norm.denormalise(0)) + "-" +
				String.valueOf(norm.denormalise(1))+ "</html>");

		this.norm = norm;
	}
	@Override
	public boolean filter(Packet packet) {
		double v = norm.normalise(packet);
		return (v <= 1 && v >= 0);
	}
	@Override
	protected void clearFilter() {
		this.norm.clearFilter();
	}
}
