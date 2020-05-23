package nl.thewgbbroz.butils_v2.playerattributes;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.playerattributes.attributes.PlayerAttribute;

public class PlayerAttributes {
	private final WGBPlugin plugin;
	
	protected final UUID uuid;
	protected final Map<Class<? extends PlayerAttribute>, PlayerAttribute> attributes = new HashMap<>();
	
	private Player onlinePlayer;
	
	protected PlayerAttributes(WGBPlugin plugin, UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.onlinePlayer = Bukkit.getPlayer(uuid);
	}
	
	protected void onJoin(Player self) {
		this.onlinePlayer = self;
	}
	
	protected void onLeave() {
		this.onlinePlayer = null;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends PlayerAttribute> E getAttribute(Class<E> clazz) {
		PlayerAttribute attrib = attributes.get(clazz);
		if(attrib != null) {
			return (E) attrib;
		}
		
		// Attribute doesn't exist. Create it.
		
		attrib = createAttribute(clazz);
		attributes.put(attrib.getClass(), attrib);
		save();
		
		return (E) attrib;
	}
	
	public <E extends PlayerAttribute> boolean hasAttribute(Class<E> clazz) {
		return attributes.containsKey(clazz);
	}
	
	public WGBPlugin getPlugin() {
		return plugin;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public OfflinePlayer getPlayer() {
		if(onlinePlayer != null) {
			return onlinePlayer;
		}
		
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	public Player getOnlinePlayer() {
		return onlinePlayer;
	}
	
	public Collection<PlayerAttribute> getAttributes() {
		return Collections.unmodifiableCollection(attributes.values());
	}
	
	public void save() {
		plugin.getService(PlayerAttributeService.class).save(this);
	}
	
	protected <E extends PlayerAttribute> E createAttribute(Class<E> clazz) {
		try {
			Constructor<E> constructor = clazz.getConstructor(PlayerAttributes.class);
			
			E attrib = constructor.newInstance(this);
			
			return attrib;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
