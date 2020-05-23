package nl.thewgbbroz.butils_v2.confirm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.services.WGBService;

public class ConfirmService extends WGBService implements Listener {
	private static final int EXPIRE_CHECK_TICKS = 20;
	
	private final WGBPlugin plugin;
	
	private Map<CommandSender, List<PendingConfirm>> confirms = new HashMap<>();
	
	public ConfirmService(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void load() {
		new BukkitRunnable() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				
				List<CommandSender> remove = new ArrayList<>();
				for(CommandSender p : confirms.keySet()) {
					List<PendingConfirm> playerConfirms = confirms.get(p);
					for(int i = 0; i < playerConfirms.size(); i++) {
						PendingConfirm pc = playerConfirms.get(i);
						if(now >= pc.endTime) {
							playerConfirms.remove(i--);
						}
					}
					
					if(playerConfirms.isEmpty())
						remove.add(p);
				}
				
				remove.forEach(rem -> confirms.remove(rem));
			}
		}.runTaskTimer(plugin, EXPIRE_CHECK_TICKS, EXPIRE_CHECK_TICKS);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void unload() {
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		confirms.remove(e.getPlayer());
	}
	
	/**
	 * @param p The player.
	 * @param tag The name of this confirmation.
	 * @param expireDelay Expiration delay in milliseconds.
	 * 
	 * @return If this returns true, this is the second time the method is called with the player and tag combination within the expireDelay.
	 * If this returns false, this is the first time the method is called with the player and tag combination and will return true when this method is called a second time within the expireDelay given.
	 */
	public boolean confirm(CommandSender p, String tag, long expireDelay) {
		List<PendingConfirm> playerConfirms = confirms.get(p);
		if(playerConfirms == null) {
			playerConfirms = new ArrayList<>();
			confirms.put(p, playerConfirms);
		}
		
		PendingConfirm pc = null;
		for(PendingConfirm testPc : playerConfirms) {
			if(testPc.tag.equalsIgnoreCase(tag)) {
				pc = testPc;
				break;
			}
		}
		
		if(pc == null) {
			// Not confirmed yet!
			
			long now = System.currentTimeMillis();
			pc = new PendingConfirm(tag, now, now + expireDelay);
			playerConfirms.add(pc);
			
			return false;
		}else {
			// Confirmed!
			
			playerConfirms.remove(pc);
			if(playerConfirms.isEmpty()) {
				confirms.remove(p);
			}
			
			return true;
		}
	}
}
