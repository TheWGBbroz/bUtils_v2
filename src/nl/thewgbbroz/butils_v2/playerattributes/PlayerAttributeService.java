package nl.thewgbbroz.butils_v2.playerattributes;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.services.WGBService;

public class PlayerAttributeService extends WGBService implements Listener {
	private final WGBPlugin plugin;
	private final IAttributeStorage storage;
	
	public PlayerAttributeService(WGBPlugin plugin, IAttributeStorage storage) {
		this.plugin = plugin;
		this.storage = storage;
	}
	
	public PlayerAttributeService(WGBPlugin plugin, ConfigurationSection storageConf) {
		this(plugin, IAttributeStorage.fromConfig(storageConf, plugin));
	}
	
	public PlayerAttributeService(WGBPlugin plugin) {
		this(plugin, new DataFileAttributeStorage(plugin));
	}
	
	@Override
	public void load() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			getAttributes(player).onJoin(player);
		});
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void unload() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			getAttributes(player).onLeave();
		});
		
		storage.destroy();
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		getAttributes(e.getPlayer()).onJoin(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		getAttributes(e.getPlayer()).onLeave();
	}
	
	protected void save(PlayerAttributes pattribs) {
		storage.updateOrPutPlayerAttributes(pattribs);
	}
	
	public PlayerAttributes getAttributes(UUID uuid) {
		return storage.fetchOrNewPlayerAttributes(uuid);
	}
	
	public PlayerAttributes getAttributes(OfflinePlayer player) {
		return storage.fetchOrNewPlayerAttributes(player.getUniqueId());
	}
	
	public Map<UUID, PlayerAttributes> getAllAttributes() {
		return storage.fetchAllPlayerAttributes();
	}
}
