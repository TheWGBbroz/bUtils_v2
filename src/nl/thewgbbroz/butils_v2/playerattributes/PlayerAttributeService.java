package nl.thewgbbroz.butils_v2.playerattributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.datafiles.DataFile;
import nl.thewgbbroz.butils_v2.datafiles.DataFileMaster;
import nl.thewgbbroz.butils_v2.services.WGBService;

public class PlayerAttributeService extends WGBService {
	private final WGBPlugin plugin;
	
	private Map<UUID, PlayerAttributes> attribs = new HashMap<>();
	private DataFile dataFile;
	
	public PlayerAttributeService(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void load() {
		dataFile = new DataFile(new File(plugin.getDataFolder(), "player-attributes.dat"), new DataFileMaster() {
			@Override
			public void save(DataOutputStream dos) throws Exception {
				dos.writeInt(attribs.size());
				for(UUID uuid : attribs.keySet()) {
					PlayerAttributes pattribs = attribs.get(uuid);
					pattribs.serialize(dos);
				}
			}
			
			@Override
			public void load(DataInputStream dis) throws Exception {
				int attribsAmount = dis.readInt();
				for(int i = 0; i < attribsAmount; i++) {
					PlayerAttributes pattribs;
					try {
						pattribs = PlayerAttributes.deserialize(plugin, dis);
					}catch(IOException e) {
						e.printStackTrace();
						continue;
					}
					
					attribs.put(pattribs.getUUID(), pattribs);
				}
			}
		});
		dataFile.savePeriodicallyMinute(plugin);
	}
	
	@Override
	public void unload() {
		dataFile.save(true);
	}
	
	protected void save(PlayerAttributes pattribs) {
		if(!attribs.containsKey(pattribs.getUUID())) {
			attribs.put(pattribs.getUUID(), pattribs);
		}
		
		dataFile.save();
	}
	
	public PlayerAttributes getAttributes(UUID uuid) {
		PlayerAttributes pattribs = attribs.get(uuid);
		if(pattribs != null) {
			return pattribs;
		}
		
		return new PlayerAttributes(plugin, uuid);
	}
	
	public PlayerAttributes getAttributes(OfflinePlayer player) {
		return getAttributes(player.getUniqueId());
	}
}
