package netvis.util;

import java.util.Comparator;
import java.util.Map;

/**
 * A collection of useful globally-reusable functions
 */
public class Utilities {

	/**
	 * Parse a number of bytes into a human-readable string
	 * (e.g. 176483264 -> 168.3 MiB). Uses binary units (not S.I.).
	 * 
	 * @param bytes
	 *            The number of bytes to be parsed
	 * @return A human-readable representation of the input
	 */
	public static String parseBytes(Long bytes) {
		if (bytes < 1024)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(1024));
		String pre = ("KMGTPE").charAt(exp - 1) + "i";
		return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
	}

	/**
	 * @see parseBytes(Long bytes)
	 */
	public static String parseBytes(int bytes) {
		return parseBytes(new Long(bytes));
	}
	
	/**
	 * Comparator for <T, Integer> maps which compares the integer values while ignoring the
	 * generic type T entries. Useful for sorting traffic data maps (traffic per port, protocol)
	 * @param <T>	Map entry type
	 */
	public static class MapComparator<T> implements Comparator<T> {
		
		Map<T, Integer> base;
		
	    public MapComparator(Map<T, Integer> base) {
	        this.base = base;
	    }

		@Override
	    public int compare(T a, T b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        }
	    }
	}

}
