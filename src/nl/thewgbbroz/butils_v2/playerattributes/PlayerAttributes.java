package nl.thewgbbroz.butils_v2.playerattributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.playerattributes.attributes.PlayerAttribute;

public class PlayerAttributes {
	private final WGBPlugin plugin;
	private final PlayerAttributeService service;
	
	private final UUID uuid;
	private final Map<Class<? extends PlayerAttribute>, PlayerAttribute> attributes = new HashMap<>();
	
	protected PlayerAttributes(WGBPlugin plugin, UUID uuid) {
		this.plugin = plugin;
		this.service = plugin.getService(PlayerAttributeService.class);
		this.uuid = uuid;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends PlayerAttribute> E getAttribute(Class<E> clazz) {
		PlayerAttribute attrib = attributes.get(clazz);
		if(attrib != null) {
			return (E) attrib;
		}
		
		// Attribute doesn't exist. Create it.
		
		attrib = createAttribute(this, clazz);
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
	
	public PlayerAttributeService getService() {
		return service;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}
	
	public Collection<PlayerAttribute> getAttributes() {
		return Collections.unmodifiableCollection(attributes.values());
	}
	
	public void save() {
		service.save(this);
	}
	
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(uuid.toString());
		
		dos.writeInt(attributes.size());
		for(PlayerAttribute attrib : attributes.values()) {
			serializeAttribute(attrib, dos);
		}
	}
	
	private void serializeAttribute(PlayerAttribute attrib, DataOutputStream dos) throws IOException {
		dos.writeUTF(attrib.getClass().getName());
		
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			attrib.serialize(config);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		dos.writeUTF(config.saveToString());
	}
	
	public static PlayerAttributes deserialize(WGBPlugin plugin, DataInputStream dis) throws IOException {
		UUID uuid = UUID.fromString(dis.readUTF());
		
		PlayerAttributes res = new PlayerAttributes(plugin, uuid);
		
		int attributesAmount = dis.readInt();
		for(int i = 0; i < attributesAmount; i++) {
			PlayerAttribute attrib = deserializeAttribute(res, dis);
			if(attrib != null) {
				res.attributes.put(attrib.getClass(), attrib);
			}
		}
		
		return res;
	}
	
	private static PlayerAttribute deserializeAttribute(PlayerAttributes parent, DataInputStream dis) throws IOException {
		String clazzName = dis.readUTF();
		String configString = dis.readUTF();
		
		try {
			YamlConfiguration config = new YamlConfiguration();
			config.loadFromString(configString);
			
			Class<?> clazz = Class.forName(clazzName);
			if(!PlayerAttribute.class.isAssignableFrom(clazz)) {
				throw new IllegalStateException("The class '" + clazzName + "' is not an instance of '" + PlayerAttribute.class.getName() + "'!");
			}
			
			@SuppressWarnings("unchecked")
			Class<? extends PlayerAttribute> attribClazz = (Class<? extends PlayerAttribute>) clazz;
			
			PlayerAttribute attrib = createAttribute(parent, attribClazz);
			attrib.deserialize(config);
			
			return attrib;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static <E extends PlayerAttribute> E createAttribute(PlayerAttributes parent, Class<E> clazz) {
		try {
			Constructor<E> constructor = clazz.getConstructor(PlayerAttributes.class);
			
			E attrib = constructor.newInstance(parent);
			
			return attrib;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
