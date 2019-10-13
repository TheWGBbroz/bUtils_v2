package nl.thewgbbroz.butils_v2.utils;

import java.util.ArrayList;

public class ArrayUtils {
	private ArrayUtils() {
	}
	
	public static <T> boolean contains(T[] array, T value) {
		for(int i = 0; i < array.length; i++) {
			if(value.equals(array[i]))
				return true;
		}
		
		return false;
	}
	
	public static boolean containsIgnoreCase(String[] array, String value) {
		for(int i = 0; i < array.length; i++) {
			if(value.equalsIgnoreCase(array[i]))
				return true;
		}
		
		return false;
	}
	
	public static <T> ArrayList<T> arrayToArrayList(T[] array) {
		ArrayList<T> res = new ArrayList<>();
		
		for(T t : array) {
			res.add(t);
		}
		
		return res;
	}
	
	public static <T> String concat(T[] array, String seperator) {
		StringBuilder res = new StringBuilder();
		
		for(int i = 0; i < array.length; i++) {
			res.append(String.valueOf(array[i]));
			if(i != array.length - 1)
				res.append(seperator);
		}
		
		return res.toString();
	}
	
	
	
	
	// -- Contains method for all data types
	public static boolean contains(byte[] array, byte value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	
	public static boolean contains(char[] array, char value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	
	public static boolean contains(short[] array, short value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	
	public static boolean contains(int[] array, int value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	
	public static boolean contains(float[] array, float value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	
	public static boolean contains(long[] array, long value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	
	public static boolean contains(double[] array, double value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == value)
				return true;
		}
		
		return false;
	}
	// --
}
