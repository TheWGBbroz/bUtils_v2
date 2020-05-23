package nl.thewgbbroz.butils_v2;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import nl.thewgbbroz.butils_v2.commands.CommandManager;
import nl.thewgbbroz.butils_v2.config.Config;
import nl.thewgbbroz.butils_v2.config.MessagesConfig;
import nl.thewgbbroz.butils_v2.metrics.Metrics;
import nl.thewgbbroz.butils_v2.services.ServiceManager;
import nl.thewgbbroz.butils_v2.services.WGBService;
import nl.thewgbbroz.butils_v2.utils.ArrayUtils;

public abstract class WGBPlugin extends JavaPlugin {
	private static final String[] LANG_FILE_NAMES = {
			"messages.yml",
			"lang.yml"
	};
	
	protected Config config;
	protected MessagesConfig msgConfig;
	
	protected boolean debugEnabled = false;
	
	protected ServiceManager serviceManager;
	protected CommandManager commandManager;
	
	protected Metrics metrics = null;
	
	private Set<Config> allConfigs = new HashSet<>();
	
	@Override
	public final void onEnable() {
		super.onEnable();
		
		// Print version info
		if(this.getClass() == BUtilsPlugin.class) {
			// This is bUtils!
			getLogger().info("Starting bUtils_v2 v" + getDescription().getVersion() + " (general utility plugin by TheWGBbroz)");
		}else {
			if(BUtilsPlugin.instance() == null) {
				getLogger().severe("Standalone bUtils_v2 is not loaded yet, but this plugin depends on it. Problems can occur if this is not fixed!");
			}else {
				getLogger().info("Using standalone bUtils_v2 v" + BUtilsPlugin.instance().getDescription().getVersion() + " by TheWGBbroz.");
			}
		}
		
		// Load config if present
		if(getResource("config.yml") != null) {
			config = new Config(this, "config.yml");
		}
		
		debugEnabled = config != null ? config.get().getBoolean("debug", false) : false;
		
		// Load lang config if present
		for(String fileName : LANG_FILE_NAMES) {
			if(getResource(fileName) != null) {
				msgConfig = new MessagesConfig(this, fileName);
				break;
			}
		}
		
		// Initialize managers
		serviceManager = new ServiceManager(this);
		commandManager = new CommandManager(this);
		
		// Initialize main plugin
		try {
			wgb_onEnable();
		}catch(Exception e) {
			getLogger().warning("Could not initialize plugin " + getClass().getName() + ": " + e.toString());
			e.printStackTrace();
			
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.isOp()) {
					p.sendMessage(ChatColor.RED + "Could not initialize plugin " + getClass().getName() + ": " + e.toString());
					p.sendMessage(ChatColor.RED + "Please check the console for more information. The plugin is disabling.");
				}
			}
			
			disable();
			return;
		}
		
		// Print out some information
		getLogger().info("Registered " + serviceManager.getLoadedServices().size() + " service(s).");
	}
	
	@Override
	public final void onDisable() {
		// Disable main plugin
		try {
			wgb_onDisable();
		}catch(Exception e) {
			getLogger().warning("Could not disable plugin properly: " + e.toString());
			e.printStackTrace();
		}
		
		// Unload/disable services
		if(serviceManager != null) {
			serviceManager.unload();
		}else {
			getLogger().warning("ServiceManager didn't initialize properly!");
		}
		
		if(commandManager != null) {
			commandManager.unload();
		}else {
			getLogger().warning("Commandmanager didn't initialize properly!");
		}
	}
	
	/**
	 * Reloads all configuration files and calls {@link ServiceManager#reloadAllServices()}
	 */
	public void reload() {
		// Reload configuration files
		allConfigs.forEach(Config::reload);
		
		debugEnabled = config != null ? config.get().getBoolean("debug", false) : false;
		
		// Reload services
		serviceManager.reloadAllServices();
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config.get();
	}
	
	@Override
	public void reloadConfig() {
		config.reload();
	}
	
	@Override
	public void saveConfig() {
		config.save();
	}
	
	@Override
	public void saveDefaultConfig() {
		config.saveDefault();
	}
	
	/**
	 * @param obj The object(s) to print
	 * 
	 * Prints an information message.
	 */
	public void info(Object... obj) {
		getLogger().info(() -> ArrayUtils.concat(obj, ", "));
	}
	
	/**
	 * @param obj The object(s) to print
	 * 
	 * Prints a warning message.
	 */
	public void warning(Object... obj) {
		getLogger().warning(() -> ArrayUtils.concat(obj, ", "));
	}
	
	/**
	 * @param obj The object(s) to print
	 * 
	 * Prints a severe message.
	 */
	public void severe(Object... obj) {
		getLogger().severe(() -> ArrayUtils.concat(obj, ", "));
	}
	
	/**
	 * @param obj The object(s) to print
	 * 
	 * Prints a debug message if debugging is enabled.
	 */
	public void debug(Object... obj) {
		if(debugEnabled) {
			getLogger().info(() -> "[DEBUG] " + ArrayUtils.concat(obj, ", "));
		}
	}
	
	/**
	 * @return Custom config object.
	 */
	public Config getConfiguration() {
		return config;
	}
	
	/**
	 * @return Whether or not the plugin has a config file.
	 */
	public boolean hasConfig() {
		return config != null;
	}
	
	/**
	 * @return The messages config file.
	 */
	public MessagesConfig getMessagesConfiguration() {
		return msgConfig;
	}
	
	/**
	 * @return Whether or not the plugin has a messages config file.
	 */
	public boolean hasMessagesConfig() {
		return msgConfig != null;
	}
	
	/**
	 * @return Passes the path and replacements to the message config, if it exists.
	 * 
	 * If a message could not be found, or if a message config is not present the path will be returned.
	 */
	public String getMessage(String path, Object... replace) {
		if(msgConfig == null) {
			return path;
		}
		
		return msgConfig.getMessage(path, replace);
	}
	
	/**
	 * @return Passes the path and replacements to the message config, if it exists. If it doesn't exist or the path doesn't exist, the defaultMessage will be used.
	 * 
	 * If a message could not be found, or if a message config is not present the default message will be returned.
	 */
	public String getMessageOrDefault(String path, String defaultMessage, Object... replace) {
		if(msgConfig == null) {
			return MessagesConfig.treatMessage(defaultMessage, replace);
		}
		
		return msgConfig.getMessageOrDefault(path, defaultMessage, replace);
	}
	
	/**
	 * @return The metrics object of this plugin
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	/**
	 * @return Whether or not this plugin has metrics
	 */
	public boolean hasMetrics() {
		return metrics != null;
	}
	
	/**
	 * @param pluginId The plugin ID
	 * 
	 * Enables metrics for this plugin.
	 */
	protected void enableMetrics(int pluginId) {
		if(metrics != null) {
			throw new IllegalStateException("Metrics have already been enabled!");
		}
		
		metrics = new Metrics(this, pluginId);
		if(!metrics.isEnabled()) {
			getLogger().warning("Metrics is not enabled. Enabling this would do the plugin developer a huge favor!");
		}
	}
	
	/**
	 * The replacement for JavaPlugin's onEnable method.
	 */
	public abstract void wgb_onEnable();
	
	/**
	 * The replacement for JavaPlugin's onDisable method.
	 */
	public abstract void wgb_onDisable();
	
	/**
	 * Disables the plugin.
	 */
	public void disable() {
		setEnabled(false);
	}
	
	/**
	 * @return The service manager of this plugin.
	 */
	public ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	/**
	 * @return The command manager of this plugin.
	 */
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	/**
	 * Wrapper of the {@link ServiceManager#getService(Class)} method
	 */
	public <E extends WGBService> E getService(Class<? extends E> serviceClass) {
		return serviceManager.getService(serviceClass);
	}
	
	/**
	 * Registers a config for this plugin.
	 * This SHOULD NOT be called manually!
	 */
	public void _registerConfig(Config config) {
		allConfigs.add(config);
	}
}
