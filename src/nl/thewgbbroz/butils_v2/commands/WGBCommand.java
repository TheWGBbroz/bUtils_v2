package nl.thewgbbroz.butils_v2.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public abstract class WGBCommand implements CommandExecutor {
	public static final String NO_PERMISSIONS		= "You don't have permissions to do this!";
	public static final String NEED_PLAYER			= "You need to be a player to do this!";
	public static final String USAGE				= "Usage: ";
	public static final String INVALID_ARGUMENTS	= "Invalid arguments!";
	
	private final WGBPlugin plugin;
	private final String command;
	
	private String messagePathPrefix = "";
	
	/**
	 * @param plugin An instance of the plugin object used in some helper functions.
	 * @param command The command name of this command.
	 */
	public WGBCommand(WGBPlugin plugin, String command) {
		this.plugin = plugin;
		this.command = command;
	}
	
	/**
	 * @param command The command name of this command.
	 */
	public WGBCommand(String command) {
		this(null, command);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			execute(sender, args);
		}catch(CommandInterrupt interrupt) {
			String msg = interrupt.getErrorMessage();
			if(msg != null) {
				sender.sendMessage(ChatColor.RED + msg);
			}
		}
		
		return true;
	}
	
	/**
	 * @return The command name of this command.
	 */
	public String getCommand() {
		return command;
	}
	
	protected void setMessagePathPrefix(String messagePathPrefix) {
		this.messagePathPrefix = messagePathPrefix;
	}
	
	/**
	 * Registers this command in the plugin.
	 */
	public void register(WGBPlugin plugin, String command) {
		plugin.getCommand(command).setExecutor(this);
	}
	
	/**
	 * Registers this command with the supplied command through the constructor.
	 */
	public void register() {
		if(plugin == null)
			throw new IllegalStateException("To register the command in this way, please pass the plugin instance through the constructor of the command.");
		
		plugin.getCommand(command).setExecutor(this);
	}
	
	/**
	 * @return A non-null player object representative of the sender.
	 */
	public Player checkPlayer(CommandSender sender) {
		if(!(sender instanceof Player)) {
			throw new CommandInterrupt(NEED_PLAYER);
		}
		
		return (Player) sender;
	}
	
	/**
	 * @return A non-null player object representative of the name parameter.
	 */
	public Player checkPlayer(CommandSender sender, String name) {
		Player p = Bukkit.getPlayer(name);
		if(p != null)
			return p;
		
		@SuppressWarnings("deprecation")
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		
		String msg;
		if(op.isOnline() || op.hasPlayedBefore())
			msg = op.getName() + " isn't online right now!";
		else
			msg = op.getName() + " has never played on the server before!";
		
		throw new CommandInterrupt(msg);
	}
	
	/**
	 * @return A non-null offline player object, which has played on this server before, representative of the name parameter.
	 */
	public OfflinePlayer checkOfflinePlayer(CommandSender sender, String name) {
		@SuppressWarnings("deprecation")
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		if(op.isOnline() || op.hasPlayedBefore())
			return op;
		
		throw new CommandInterrupt(op.getName() + " has never played on the server before!");
	}
	
	/**
	 * Checks if the sender has the required permission.
	 */
	public void checkPermission(CommandSender sender, String permission) {
		if(!sender.hasPermission(permission)) {
			throw new CommandInterrupt(NO_PERMISSIONS);
		}
	}
	
	/**
	 * Checks if the sender has the required permissions.
	 */
	public void checkPermissions(CommandSender sender, String... permissions) {
		for(String perm : permissions) {
			checkPermission(sender, perm);
		}
	}
	
	/**
	 * @return Returns an integer representative of the number parameter.
	 */
	public int checkInt(CommandSender sender, String number) {
		try {
			return Integer.parseInt(number);
		}catch(NumberFormatException e) {}
		
		throw new CommandInterrupt("Invalid number '" + number + "'!");
	}
	
	/**
	 * @return Returns a double representative of the number parameter.
	 */
	public double checkDouble(CommandSender sender, String number) {
		try {
			return Double.parseDouble(number);
		}catch(NumberFormatException e) {}
		
		throw new CommandInterrupt("Invalid decimal '" + number + "'!");
	}
	
	/**
	 * @return A non-null world object representative of the worldName parameter. 
	 */
	public World checkWorld(CommandSender sender, String worldName) {
		World world = Bukkit.getWorld(worldName);
		if(world != null)
			return world;
		
		throw new CommandInterrupt("Invalid world '" + worldName + "'!");
	}
	
	/**
	 * Sends a usage message with the usage in the parameters if the number of
	 * arguments supplied is less than the minArgs parameter.
	 */
	public void checkNumArgs(CommandSender sender, String[] args, int minArgs, String usage) {
		if(args.length < minArgs) {
			throw new CommandInterrupt("Usage: " + usage);
		}
	}
	
	/**
	 * Wrapper function for {@link CommandSender#sendMessage(String)} which makes use of the {@link WGBPlugin#getMessage(String, Object...)} method.
	 */
	public void sendMessage(CommandSender sender, String path, boolean addMessagePrefix, Object... replace) {
		checkPlugin();
		
		if(addMessagePrefix)
			path = messagePathPrefix + path;
		
		sender.sendMessage(plugin.getMessage(path, replace));
	}
	
	/**
	 * Wrapper function for {@link CommandSender#sendMessage(String)} which makes use of the {@link WGBPlugin#getMessage(String, Object...)} method.
	 * 
	 * This will always add the message prefix.
	 */
	public void sendMessage(CommandSender sender, String path, Object... replace) {
		sendMessage(sender, path, true, replace);
	}
	
	/**
	 * Helper function for the helper functions. Checks if a plugin was passed through the constructor
	 */
	private void checkPlugin() {
		if(plugin == null)
			throw new IllegalStateException("Please pass through a plugin object in the command constructor to make use of this helper method.");
	}
	
	/**
	 * @param sender The sender of the command.
	 * @param args The arguments passed with the command.
	 * 
	 * This method gets called instead of Bukkit's onCommand method.
	 * 
	 * Any CommandInterrupt thrown by the "check" methods in this class will cancel the
	 * command and will be catched.
	 */
	public abstract void execute(CommandSender sender, String[] args);
}
