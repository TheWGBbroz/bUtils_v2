package nl.thewgbbroz.butils_v2.config;

import java.util.List;

import org.bukkit.ChatColor;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public class MessagesConfig extends Config {
	private final WGBPlugin plugin;
	
	private String prefix = "";
	private String suffix = "";
	
	public MessagesConfig(WGBPlugin plugin, String name) {
		super(plugin, name);
		
		this.plugin = plugin;
	}
	
	public MessagesConfig(WGBPlugin plugin) {
		this(plugin, "messages.yml");
	}
	
	@Override
	public void reload() {
		super.reload();
		
		if(get().contains("global-prefix")) {
			prefix = ChatColor.translateAlternateColorCodes('&', get().getString("global-prefix"));
		}
		
		if(get().contains("global-suffix")) {
			suffix = ChatColor.translateAlternateColorCodes('&', get().getString("global-suffix"));
		}
		
		if(!(get().contains("global-prefix") && get().contains("global-suffix"))) {
			// Prefix and/or suffix is/are missing.
			plugin.getLogger().info("No message prefix and/or suffix is configured. To do this, add a 'global-prefix' or 'global-suffix' entry in " + getName() + ". This is optional.");
		}
	}
	
	/**
	 * Helper function to get a cached message, with the translated colors, from the config.
	 * The objects in the replace parameter will be replaced from '%1', '%2' and so on..
	 * Important to note that index 0 will be replaced from %1.
	 */
	public String getMessageOrDefault(String path, String defaultMessage, Object... replace) {
		String message;
		if(!get().contains(path)) {
			message = defaultMessage != null ? defaultMessage : path;
		}else if(get().isList(path)) {
			StringBuilder messageBuilder = new StringBuilder();
			
			List<String> messageList = get().getStringList(path);
			for(int i = 0; i < messageList.size(); i++) {
				messageBuilder.append(messageList.get(i));
				
				if(i != messageList.size() - 1) {
					messageBuilder.append('\n');
				}
			}
			
			message = messageBuilder.toString();
		}else {
			message = get().getString(path);
		}
		
		message = treatMessage(message, replace);
		message = prefix + message + suffix;
		
		return message;
	}
	
	/**
	 * Helper function to get a cached message, with the translated colors, from the config.
	 * The objects in the replace parameter will be replaced from '%1', '%2' and so on..
	 * Important to note that index 0 will be replaced from %1.
	 */
	public String getMessage(String path, Object... replace) {
		return getMessageOrDefault(path, null, replace);
	}
	
	/**
	 * @param message The message to modify.
	 * @param replace The replacements to make.
	 * 
	 * @return The modified message.
	 */
	public static String treatMessage(String message, Object... replace) {
		message = ChatColor.translateAlternateColorCodes('&', message).replace("\\n", "\n");
		
		for(int i = 0; i < replace.length; i++) {
			message = message.replace("%" + (i + 1), String.valueOf(replace[i]));
		}
		
		return message;
	}
}
