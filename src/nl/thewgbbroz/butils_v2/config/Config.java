package nl.thewgbbroz.butils_v2.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public class Config {
	private WGBPlugin plugin;
	private String name;
	private File file;
	private FileConfiguration config;
	
	public Config(WGBPlugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;
		this.file = new File(plugin.getDataFolder(), name);
		
		saveDefault();
		
		plugin._registerConfig(this);
	}
	
	/**
	 * Reloads the config from the file on disk.
	 */
	public void reload() {
		config = YamlConfiguration.loadConfiguration(file);
		
		if(hasDefaults()) {
			try {
				Reader defConfigStream = new InputStreamReader(plugin.getResource(name), "UTF8");
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				config.setDefaults(defConfig);
			}catch(IOException e) {
				plugin.getLogger().severe("Could not reload config '" + name + "'!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return A FileConfiguration object representative of the config file in memory.
	 */
	public FileConfiguration get() {
		if(config == null) reload();
		return config;
	}
	
	/**
	 * Saves the current config in memory to the disk.
	 */
	public void save() {
		if(config == null) return;
		
		try{
			config.save(file);
		}catch(IOException e) {
			plugin.getLogger().severe("Could not save config '" + name + "'!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the default config values, located in the plugins jar file, if the config file does not exist on the disk.
	 */
	public void saveDefault() {
		if(!file.exists()) {
			if(hasDefaults()) {
				plugin.saveResource(name, false);
			}else {
				try {
					file.createNewFile();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @return Whether the config file has defaults in the jar file.
	 */
	public boolean hasDefaults() {
		return plugin.getResource(name) != null;
	}
	
	/**
	 * @return The config name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The file of the config file on the disk.
	 */
	public File getFile() {
		return file;
	}
}
