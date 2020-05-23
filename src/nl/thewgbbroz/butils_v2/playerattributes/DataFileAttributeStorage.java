package nl.thewgbbroz.butils_v2.playerattributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.datafiles.DataFile;
import nl.thewgbbroz.butils_v2.datafiles.DataFileMaster;
import nl.thewgbbroz.butils_v2.playerattributes.attributes.PlayerAttribute;

public class DataFileAttributeStorage implements IAttributeStorage {
	private final WGBPlugin plugin;
	
	private Map<UUID, PlayerAttributes> attribs = new HashMap<>();
	private DataFile dataFile;
	
	public DataFileAttributeStorage(WGBPlugin plugin, String dataFileName) {
		this.plugin = plugin;
		
		dataFile = new DataFile(new File(plugin.getDataFolder(), dataFileName), new DataFileMaster() {
			@Override
			public void save(DataOutputStream dos) throws Exception {
				dos.writeInt(attribs.size());
				for(UUID uuid : attribs.keySet()) {
					PlayerAttributes pattribs = attribs.get(uuid);
					
					dos.writeUTF(pattribs.uuid.toString());
					
					dos.writeInt(pattribs.attributes.size());
					for(PlayerAttribute attrib : pattribs.attributes.values()) {
						dos.writeUTF(attrib.getClass().getName());
						
						YamlConfiguration config = new YamlConfiguration();
						
						try {
							attrib.serialize(config);
						}catch(Exception e) {
							e.printStackTrace();
						}
						
						dos.writeUTF(config.saveToString());
					}
				}
			}
			
			@Override
			public void load(DataInputStream dis) throws Exception {
				int attribsAmount = dis.readInt();
				
				attribs.clear();
				for(int i = 0; i < attribsAmount; i++) {
					UUID uuid = UUID.fromString(dis.readUTF());
					
					PlayerAttributes pattribs = new PlayerAttributes(plugin, uuid);
					
					int attributesAmount = dis.readInt();
					for(int j = 0; j < attributesAmount; j++) {
						String clazzName = dis.readUTF();
						String configString = dis.readUTF();
						
						YamlConfiguration config = new YamlConfiguration();
						config.loadFromString(configString);
						
						PlayerAttribute attrib;
						try {
							Class<?> clazz = Class.forName(clazzName);
							@SuppressWarnings("unchecked")
							Class<? extends PlayerAttribute> attribClazz = (Class<? extends PlayerAttribute>) clazz;
							
							attrib = pattribs.createAttribute(attribClazz);
							attrib.deserialize(config);
						}catch(Exception e) {
							e.printStackTrace();
							continue;
						}
						
						pattribs.attributes.put(attrib.getClass(), attrib);
					}
					
					attribs.put(pattribs.getUUID(), pattribs);
				}
			}
		});
		dataFile.savePeriodicallyMinute(plugin);
	}
	
	public DataFileAttributeStorage(WGBPlugin plugin) {
		this(plugin, "player-attributes.dat");
	}
	
	@Override
	public void updateOrPutPlayerAttributes(PlayerAttributes pattribs) {
		if(!attribs.containsKey(pattribs.getUUID())) {
			attribs.put(pattribs.getUUID(), pattribs);
		}
		
		dataFile.save();
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
		dataFile.save(true);
	}
}
