package netvis.data;

import java.util.HashMap;
import java.util.Random;

/**
 * Contains some static utilities to be used with data
 * 
 */
public class DataUtilities {
	public static final int MIN_PORT = 0;
	public static final int MAX_PORT = 65535;
	private static HashMap<String, Double> macMap = new HashMap<String, Double>();
	private static Random doubleGen = new Random();

	/**
	 * Transforms an IPv4 into a double value in the range [0..1]
	 */
	public static double normaliseIP(String ip) {
		int i = 0;
		int c = 0;
		double f = 0;
		while (ip.length() > i) {
			if (ip.charAt(i) < '0' || ip.charAt(i) > '9')
				return 0;
			c = c * 10 + (ip.charAt(i) - '0');
			i++;
			if (ip.length() == i || ip.charAt(i) == '.') {
				f = f * 256 + c;
				c = 0;
				i++;
			}
		}
		f = f / (256 * 256);
		f = f / (256 * 256);
		return f;
	}
	
	public static String denormaliseIP(double nv){
		nv = nv*256*256*256*256;
		long nvi = (long)nv;
		if (nvi == (long)65536*65536) nvi -= 1;
		String s = new String();
		for (int i = 0; i < 4; i++){
			s =  String.valueOf(nvi % 256) + s;
			nvi = nvi / 256;
			if (i != 3) s = '.' + s;
		}
		return s;
	}

	/**
	 * Transforms a port into a double value in the range [0..1]
	 */
	public static double normalisePort(int port) {
		return (double) port / MAX_PORT;
	}
	
	public static double normaliseMAC(String address) {
		if (macMap.containsKey(address))
			return macMap.get(address);
		else {
			double value = 0.5;
			while (macMap.containsValue(value))
				value = doubleGen.nextDouble();
			macMap.put(address, value);
			return value;
		}
	}

	public static String denormalisePort(double v) {
		return String.valueOf((int)(v * MAX_PORT));
	}

}
