package nl.thewgbbroz.butils_v2.utils;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

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
	
	public static <E> String join(Collection<E> list, String glue, Function<E, String> converter) {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for(E e : list) {
			if(first) {
				first = false;
			}else {
				sb.append(glue);
			}
			
			sb.append(converter.apply(e));
		}
		
		return sb.toString();
	}
	
	public static String join(Collection<String> list, String glue) {
		return join(list, glue, str -> str);
	}
	
	public static <E> String join(E[] array, String glue, Function<E, String> converter) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < array.length; i++) {
			if(i != 0) {
				sb.append(glue);
			}
			
			sb.append(converter.apply(array[i]));
		}
		
		return sb.toString();
	}
	
	public static String join(String[] array, String glue) {
		return join(array, glue, str -> str);
	}
	
	public static boolean isValidUUID(String str) {
		try {
			UUID.fromString(str);
			
			return true;
		}catch(IllegalArgumentException e) {}
		
		return false;
	}
}
