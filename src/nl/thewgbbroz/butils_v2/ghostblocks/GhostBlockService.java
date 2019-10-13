package nl.thewgbbroz.butils_v2.ghostblocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.services.WGBService;

public class GhostBlockService extends WGBService {
	private static final int PUBLIC_VIEWERS_UPDATE_DELAY = 20;
	private static final double PUBLIC_VIEW_DIST = 64;
	
	private final WGBPlugin plugin;
	
	// TODO: ACTUALLY ADD GHOST BLOCKS TO THIS LIST!
	private List<GhostBlock> ghostBlocks = new ArrayList<>();
	
	public GhostBlockService(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void load() {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateViewers();
			}
		}.runTaskTimer(plugin, PUBLIC_VIEWERS_UPDATE_DELAY, PUBLIC_VIEWERS_UPDATE_DELAY);
	}

	@Override
	public void unload() {
	}
	
	private void updateViewers() {
		ghostBlocks.forEach(gb -> {
			if(!gb.isPublic())
				return;
			
			gb.getViewers().clear();
			
			Location loc = gb.getLocation();
			loc.getWorld().getNearbyEntities(loc, PUBLIC_VIEW_DIST, PUBLIC_VIEW_DIST, PUBLIC_VIEW_DIST, (en -> en.getType() == EntityType.PLAYER))
					.forEach(en -> {
				gb.getViewers().add((Player) en);
			});
		});
	}
}
