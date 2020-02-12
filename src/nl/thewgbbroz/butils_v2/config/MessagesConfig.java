package nl.thewgbbroz.butils_v2.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public class MessagesConfig extends Config {
	private final WGBPlugin plugin;
	
	private String prefix;
	private String suffix;
	
	private Map<String, String> cache = new HashMap<>(); // Contains messages with translated color chars
	
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
		
		cache.clear();
		
		// Use Bukkit#getConsoleSender to display colors in console.
		if(get().contains("global-prefix")) {
			prefix = ChatColor.translateAlternateColorCodes('&', get().getString("global-prefix"));
			Bukkit.getConsoleSender().sendMessage("Message prefix: " + prefix);
		}else {
			prefix = "";
			plugin.getLogger().info("No message prefix configured. To configure a message prefix, add a 'global-prefix' entry in messages.yml.");
		}
		
		if(get().contains("global-suffix")) {
			suffix = ChatColor.translateAlternateColorCodes('&', get().getString("global-suffix"));
			Bukkit.getConsoleSender().sendMessage("Message suffix: " + suffix);
		}else {
			suffix = "";
			plugin.getLogger().info("No message suffix configured. To configure a message suffix, add a 'global-suffix' entry in messages.yml.");
		}
	}
	
	/**
	 * Helper function to get a cached message, with the translated colors, from the config.
	 * The objects in the replace parameter will be replaced from '%1', '%2' and so on..
	 * Important to note that index 0 will be replaced from %1.
	 */
	public String getMessage(String path, Object... replace) {
		if(!get().contains(path))
			return path;
		
		String s;
		if(cache.containsKey(path)) {
			s = cache.get(path);
		}else{
			if(get().isList(path)) {
				s = "";
				for(String str : get().getStringList(path)) {
					s += str + '\n';
				}
				s = s.substring(0, s.length() - 1);
			}else{
				s = get().getString(path);
			}
			s = ChatColor.translateAlternateColorCodes('&', s).replace("\\n", "\n");
			
			cache.put(path, s);
		}
		
		for(int i = 0; i < replace.length; i++) {
			s = s.replace("%" + (i + 1), String.valueOf(replace[i]));
		}
		
		return prefix + s + suffix;
	}
}
