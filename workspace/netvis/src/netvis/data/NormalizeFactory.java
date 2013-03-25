package netvis.data;

import java.util.Arrays;
import java.util.List;

import netvis.data.model.Packet;
/**
 * A helper function that creates classes which return a certain
 * normalized parameter of the packet
 */
public class NormalizeFactory {
	public static final NormalizeFactory INSTANCE = new NormalizeFactory();
	
	List<String> attrs;
	private NormalizeFactory(){
		attrs = Arrays.asList(
				"Source Port",
				"Destination Port",
				"Source IP",
				"Destination IP"
		);
	}
	
	public List<String> getAttrs() {
		return attrs;
	}
	
	public Normalizer getNormalizer(int norm_id){
		switch(norm_id){
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
	
	public interface Normalizer{
		public double normalize(Packet p);
		public String name();
	}
	
	private class SourcePortNorm implements Normalizer{
		public double normalize(Packet p) {
			return DataUtilities.normalizePort(p.sport);
		}
		public String name() {
			return attrs.get(0);
		}
	}
	private class DestinationPortNorm implements Normalizer{
		public double normalize(Packet p) {
			return DataUtilities.normalizePort(p.dport);
		}
		public String name() {
			return attrs.get(1);
		}
	}
	private class SourceIPNorm implements Normalizer{
		public double normalize(Packet p) {
			return DataUtilities.normalizeV4Ip(p.sip);
		}
		public String name() {
			return attrs.get(2);
		}
	}
	private class DestinationIPNorm implements Normalizer{
		public double normalize(Packet p) {
			return DataUtilities.normalizeV4Ip(p.dip);
		}
		public String name() {
			return attrs.get(3);
		}
	}
	
}
