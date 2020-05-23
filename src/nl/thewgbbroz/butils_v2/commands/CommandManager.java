package nl.thewgbbroz.butils_v2.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.PluginCommand;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public class CommandManager {
	private final WGBPlugin plugin;
	
	private Set<WGBCommand> registeredCommands = new HashSet<>();
	
	public CommandManager(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void unload() {
	}
	
	/**
	 * Registers a command
	 */
	public <E extends WGBCommand> E registerCommand(E executor) {
		PluginCommand cmd = plugin.getCommand(executor.getCommand());
		if(cmd == null) {
			plugin.getLogger().warning("Tried to register command " + executor.getCommand() + ", but the PluginCommand is null. Is it listed in plugin.yml?");
			return null;
		}
		
		cmd.setExecutor(executor);
		registeredCommands.add(executor);
		
		return executor;
	}
	
	// TODO: Add get and unregister methods.
}
