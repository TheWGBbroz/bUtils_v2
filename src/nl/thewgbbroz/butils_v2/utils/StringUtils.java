package nl.thewgbbroz.butils_v2.utils;

public class StringUtils {
	private StringUtils() {
	}
	
	public static String prettifyEnum(Object obj, boolean firstUppercase) {
		String s = String.valueOf(obj); // String#valueOf to allow null.
		
		s = s.toLowerCase().replace("_", " ");
		
		if(firstUppercase) {
			s = s.substring(0, 1).toUpperCase() + s.substring(1);
		}
		
		return s;
	}
	
	public static String prettifyEnum(Object obj) {
		return prettifyEnum(obj, false);
	}
}
