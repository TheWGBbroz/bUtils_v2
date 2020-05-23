package nl.thewgbbroz.butils_v2.playerattributes;

import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public interface IAttributeStorage {
	public void updateOrPutPlayerAttributes(PlayerAttributes pattribs);
	public PlayerAttributes fetchOrNewPlayerAttributes(UUID uuid);
	public Map<UUID, PlayerAttributes> fetchAllPlayerAttributes();
	
	public void destroy();
	
	public static IAttributeStorage fromConfig(ConfigurationSection conf, WGBPlugin plugin) {
		String storage = conf.getString("storage");
		
		if(storage.equalsIgnoreCase("yaml")) {
			String fileName = conf.getString("file-name", "player-attributes.yml");
			return new YamlAttributeStorage(plugin, fileName);
		}
		else {
			// Invalid storage. Default to .dat.
			
			String fileName = conf.getString("file-name", "player-attributes.dat");
			return new DataFileAttributeStorage(plugin, fileName);
		}
	}
}
