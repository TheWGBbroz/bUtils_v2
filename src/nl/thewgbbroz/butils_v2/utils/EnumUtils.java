package nl.thewgbbroz.butils_v2.utils;

public class EnumUtils {
	private EnumUtils() {
	}
	
	public static <E extends Enum<E>> E getEnumOrDefault(Class<E> enumClass, String name, E defaultValue) {
		if(name == null) {
			return defaultValue;
		}
		
		try {
			return Enum.valueOf(enumClass, name.toUpperCase());
		}catch(IllegalArgumentException e) {}
		
		return defaultValue;
	}
	
	public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String name) {
		return getEnumOrDefault(enumClass, name, null);
	}
}
