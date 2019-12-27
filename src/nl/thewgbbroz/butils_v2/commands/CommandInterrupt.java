package nl.thewgbbroz.butils_v2.commands;

public class CommandInterrupt extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final String errorMsg;
	
	public CommandInterrupt(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public CommandInterrupt() {
		this("Unknown command interrupt");
	}
	
	public String getErrorMessage() {
		return errorMsg;
	}
}
