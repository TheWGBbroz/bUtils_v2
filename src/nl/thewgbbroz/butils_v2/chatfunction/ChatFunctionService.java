package nl.thewgbbroz.butils_v2.chatfunction;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.services.WGBService;

public class ChatFunctionService extends WGBService implements Listener {
	private final WGBPlugin plugin;
	
	private Map<Player, ChatFunctionListener> listeners = new HashMap<>();
	
	public ChatFunctionService(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void load() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void unload() {
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		listeners.remove(e.getPlayer());
	}
	
	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		if(listeners.containsKey(e.getPlayer())) {
			e.setCancelled(true);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					ChatFunctionListener listener = listeners.get(e.getPlayer());
					if(listener == null) return;
					
					boolean exit = true;
					try {
						exit = listener.onMessage(e.getPlayer(), e.getMessage());
					}catch(Exception ex) {
						e.getPlayer().sendMessage(ChatColor.RED + "Something went wrong, please check the console.");
						ex.printStackTrace();
					}
					
					if(exit) {
						listeners.remove(e.getPlayer());
					}
				}
			}.runTask(plugin);
		}
	}
	
	/**
	 * Registers a chat function listener.
	 */
	public void registerChatFunctionListener(Player p, ChatFunctionListener listener) {
		if(listeners.containsKey(p))
			listeners.remove(p);
		listeners.put(p, listener);
	}
}
