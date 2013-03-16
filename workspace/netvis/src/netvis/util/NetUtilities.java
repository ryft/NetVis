package netvis.util;

/**
 * A collection of useful reusable networking-related functions
 */
public class NetUtilities {

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

}
