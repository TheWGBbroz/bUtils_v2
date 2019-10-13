package nl.thewgbbroz.butils_v2.confirm;

public class PendingConfirm {
	public final String tag;
	public final long startTime;
	public final long endTime;
	
	protected PendingConfirm(String tag, long startTime, long endTime) {
		this.tag = tag;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
