package nl.thewgbbroz.butils_v2.utils;

import java.util.Collection;

public class Asserts {
	private Asserts() {
	}
	
	/* Throws IllegalStateExceptions when assertion fails, or the specific exception if there are any */
	
	public static void assertNotNull(Object obj) {
		if(obj == null)
			throw new NullPointerException();
	}
	
	public static <E> void assertContains(Collection<E> list, E item) {
		assertNotNull(list);
		assertNotNull(item);
		
		if(!list.contains(item))
			throw new IllegalStateException();
	}
	
	public static <E> void assertNotContains(Collection<E> list, E item) {
		assertNotNull(list);
		assertNotNull(item);
		
		if(list.contains(item))
			throw new IllegalStateException();
	}
	
	public static void assertFalse(boolean b) {
		if(b)
			throw new IllegalStateException();
	}
	
	public static void assertTrue(boolean b) {
		assertFalse(!b);
	}
	
	public static void assertEquals(Object a, Object b) {
		if(a == null || b == null)
			assertTrue(a == b);
		else
			assertTrue(a == b || a.equals(b));
	}
	
	public static void assertNotEquals(Object a, Object b) {
		if(a == null || b == null)
			assertTrue(a != b);
		else
			assertTrue(a != b && !a.equals(b));
	}
}
