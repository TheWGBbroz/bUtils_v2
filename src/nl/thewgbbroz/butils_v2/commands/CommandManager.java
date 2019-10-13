package nl.thewgbbroz.butils_v2.commands;

import java.util.ArrayList;
import java.util.List;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public class CommandManager {
	private final WGBPlugin plugin;
	
	private List<WGBCommand> registeredCommands = new ArrayList<>();
	
	public CommandManager(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void unload() {
	}
	
	/**
	 * Registers a command
	 */
	public <E extends WGBCommand> E registerCommand(E executor) {
		plugin.getCommand(executor.getCommand()).setExecutor(executor);
		registeredCommands.add(executor);
		
		return executor;
	}
	
	// TODO: Add get and unregister methods.
}
