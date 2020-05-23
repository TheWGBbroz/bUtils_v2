package nl.thewgbbroz.butils_v2.playerattributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.config.Config;
import nl.thewgbbroz.butils_v2.playerattributes.attributes.PlayerAttribute;

public class YamlAttributeStorage implements IAttributeStorage {
	private final WGBPlugin plugin;
	
	private Map<UUID, PlayerAttributes> attribs = new HashMap<>();
	private Config config;
	
	private boolean shouldSave = false;
	
	public YamlAttributeStorage(WGBPlugin plugin, String yamlFileName) {
		this.plugin = plugin;
		
		config = new Config(plugin, yamlFileName);
		
		Bukkit.getScheduler().runTaskTimer(plugin, this::savePeriodically, 20 * 60, 20 * 60);
		
		loadAll();
	}
	
	public YamlAttributeStorage(WGBPlugin plugin) {
		this(plugin, "player-attributes.yml");
	}
	
	private void savePeriodically() {
		if(shouldSave) {
			saveAll(true);
		}
	}
	
	private void saveAll(boolean force) {
		if(!force) {
			shouldSave = true;
			return;
		}
		
		// Forcing save!
		shouldSave = false; // We don't have to save again after this because we already have saved.
		
		// Clear current config
		for(String key : config.get().getKeys(false)) {
			config.get().set(key, null);
		}
		
		for(UUID uuid : attribs.keySet()) {
			PlayerAttributes pattribs = attribs.get(uuid);
			
			ConfigurationSection pattribsSection = config.get().createSection(uuid.toString());
			
			int i = 0;
			for(PlayerAttribute attrib : pattribs.attributes.values()) {
				ConfigurationSection attribSection = pattribsSection.createSection(String.valueOf(i++));
				
				try {
					attrib.serialize(attribSection);
					
					attribSection.set("class", attrib.getClass().getName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		config.save();
	}
	
	private void loadAll() {
		attribs.clear();
		for(String uuidStr : config.get().getKeys(false)) {
			UUID uuid = UUID.fromString(uuidStr);
			
			PlayerAttributes pattribs = new PlayerAttributes(plugin, uuid);
			
			ConfigurationSection pattribsSection = config.get().getConfigurationSection(uuidStr);
			
			for(String key : pattribsSection.getKeys(false)) {
				ConfigurationSection attribSection = pattribsSection.getConfigurationSection(key);
				
				String clazzName = attribSection.getString("class");
				
				PlayerAttribute attrib;
				try {
					Class<?> clazz = Class.forName(clazzName);
					@SuppressWarnings("unchecked")
					Class<? extends PlayerAttribute> attribClazz = (Class<? extends PlayerAttribute>) clazz;
					
					attrib = pattribs.createAttribute(attribClazz);
					attrib.deserialize(attribSection);
				}catch(Exception e) {
					e.printStackTrace();
					continue;
				}
				
				pattribs.attributes.put(attrib.getClass(), attrib);
			}
			
			attribs.put(pattribs.getUUID(), pattribs);
		}
	}
	
	@Override
	public void updateOrPutPlayerAttributes(PlayerAttributes pattribs) {
		if(!attribs.containsKey(pattribs.getUUID())) {
			attribs.put(pattribs.getUUID(), pattribs);
		}
		
		saveAll(false);
	}
	
	@Override
	public PlayerAttributes fetchOrNewPlayerAttributes(UUID uuid) {
		PlayerAttributes pattribs = attribs.get(uuid);
		if(pattribs != null) {
			return pattribs;
		}
		
		return new PlayerAttributes(plugin, uuid);
	}
	
	@Override
	public Map<UUID, PlayerAttributes> fetchAllPlayerAttributes() {
		return Collections.unmodifiableMap(attribs);
	}
	
	@Override
	public void destroy() {
		saveAll(true);
	}
}
