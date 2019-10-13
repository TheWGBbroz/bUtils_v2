package nl.thewgbbroz.butils_v2.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.utils.ArrayUtils;

public abstract class WGBCommand implements CommandExecutor {
	public static final String NO_PERMISSIONS = ChatColor.RED + "You don't have permissions to do this!";
	public static final String NEED_PLAYER = ChatColor.RED + "You need to be a player to do this!";
	public static final String USAGE = ChatColor.RED + "Usage: ";
	public static final String INVALID_ARGUMENTS = ChatColor.RED + "Invalid arguments!";
	
	private String command;
	
	@Deprecated
	public WGBCommand() {
		this(null);
	}
	
	/**
	 * @param command The command name of this command.
	 */
	public WGBCommand(String command) {
		this.command = command;
	}
	
	@Deprecated
	private List<String> permissions = null;
	
	@Deprecated
	private int minArgs = -1;
	
	@Deprecated
	private String usage = null;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(permissions != null) {
			// Check for permissions
			for(String perm : permissions) {
				if(!sender.hasPermission(perm)) {
					sender.sendMessage(NO_PERMISSIONS);
					return true;
				}
			}
		}
		
		if(args.length < minArgs) {
			sender.sendMessage(USAGE + usage);
			return true;
		}
		
		try {
			execute(sender, args);
		}catch(CommandInterrupt interrupt) {
		}
		
		return true;
	}
	
	/**
	 * @return The command name of this command.
	 */
	public String getCommand() {
		return command;
	}
	
	@Deprecated
	public void setPermissions(String... permissions) {
		this.permissions = ArrayUtils.arrayToArrayList(permissions);
	}
	
	@Deprecated
	public void setMinimumArguments(int minArgs) {
		this.minArgs = minArgs;
	}
	
	@Deprecated
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	/**
	 * Registers this command in the plugin.
	 */
	public void register(WGBPlugin plugin, String command) {
		plugin.getCommand(command).setExecutor(this);
	}
	
	/**
	 * @return A non-null player object representative of the sender.
	 */
	public Player checkPlayer(CommandSender sender) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(NEED_PLAYER);
			throw new CommandInterrupt();
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
		
		if(op.isOnline() || op.hasPlayedBefore())
			sender.sendMessage(ChatColor.RED + op.getName() + " isn't online right now!");
		else
			sender.sendMessage(ChatColor.RED + op.getName() + " has never played on the server before!");
		
		throw new CommandInterrupt();
	}
	
	/**
	 * @return A non-null offline player object, which has played on this server before, representative of the name parameter.
	 */
	public OfflinePlayer checkOfflinePlayer(CommandSender sender, String name) {
		@SuppressWarnings("deprecation")
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		if(op.isOnline() || op.hasPlayedBefore())
			return op;
		
		sender.sendMessage(ChatColor.RED + op.getName() + " has never played on the server before!");
		
		throw new CommandInterrupt();
	}
	
	/**
	 * Checks if the sender has the required permission.
	 */
	public void checkPermission(CommandSender sender, String permission) {
		if(!sender.hasPermission(permission)) {
			sender.sendMessage(NO_PERMISSIONS);
			throw new CommandInterrupt();
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
		
		sender.sendMessage(ChatColor.RED + "Invalid number '" + number + "'!");
		throw new CommandInterrupt();
	}
	
	/**
	 * @return Returns a double representative of the number parameter.
	 */
	public double checkDouble(CommandSender sender, String number) {
		try {
			return Double.parseDouble(number);
		}catch(NumberFormatException e) {}
		
		sender.sendMessage(ChatColor.RED + "Invalid decimal '" + number + "'!");
		throw new CommandInterrupt();
	}
	
	/**
	 * @return A non-null world object representative of the worldName parameter. 
	 */
	public World checkWorld(CommandSender sender, String worldName) {
		World world = Bukkit.getWorld(worldName);
		if(world != null)
			return world;
		
		sender.sendMessage(ChatColor.RED + "Invalid world '" + worldName + "'!");
		throw new CommandInterrupt();
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
