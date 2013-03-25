package netvis.data;

/**
 * Contains some static utilities to be used with data
 *
 */
public class DataUtilities {
	public static final int MIN_PORT = 0;
	public static final int MAX_PORT = 65535;
	
	/**
	 * Transforms an IPV4 into a double value in the range [0..1]
	 */
	public static double normalizeV4Ip(String ip) {
		int i = 0;
		int c = 0;
		double f = 0;
		while (ip.length() > i){
			if (ip.charAt(i) < '0' || ip.charAt(i) > '9')
				return 0;
			c = c * 10 + (ip.charAt(i) - '0');
			i++;
			if (ip.length() == i || ip.charAt(i) == '.'){
				f = f*256 + c;
				c = 0;
				i++;
			}
		}
		f = f/(256*256);
		f = f/(256*256);
		return f;
	}

	/**
	 * Transforms a port into a double value in the range [0..1]
	 */
	public static double normalizePort(int port) {
		return (double)port / MAX_PORT;
	}
	
	
}
