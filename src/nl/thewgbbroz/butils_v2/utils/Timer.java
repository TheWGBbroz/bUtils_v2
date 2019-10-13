package nl.thewgbbroz.butils_v2.utils;

public class Timer {
	public final long start;
	
	public Timer(long start) {
		this.start = start;
	}
	
	public Timer() {
		this(System.currentTimeMillis());
	}
	
	public long elapsed(long now) {
		return now - start;
	}
	
	public long elapsed() {
		return elapsed(System.currentTimeMillis());
	}
}
