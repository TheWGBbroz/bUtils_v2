package nl.thewgbbroz.butils_v2.custominventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import net.wesjd.anvilgui.AnvilGUI;
import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.services.WGBService;

public class GUITextInputService extends WGBService implements Listener {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_SECOND_INPUT = 1;
	public static final int SLOT_OUTPUT = 2;
	
	private final WGBPlugin plugin;
	
	private Map<Player, GUITextInputListener> inInv = new HashMap<>();
	
	public GUITextInputService(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void load() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@Override
	public void unload() {
		List<Player> players = new ArrayList<>(inInv.keySet());
		for(Player p : players) {
			p.closeInventory();
		}
		
		inInv.clear();
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if(inInv.containsKey(e.getPlayer())) {
			e.getPlayer().closeInventory();
			inInv.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		if(inInv.containsKey(e.getPlayer())) {
			try {
				inInv.get(e.getPlayer()).onClose();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			inInv.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		// e.getInventory() is TOP inventory
		
		if(inInv.containsKey(e.getWhoClicked())) {
			e.setCancelled(true);
			
			if(e.getInventory() instanceof AnvilInventory) {
				AnvilInventory inv = (AnvilInventory) e.getInventory();
				GUITextInputListener listener = inInv.get(e.getWhoClicked());
				
				String text = "null";
				ItemStack output = inv.getItem(SLOT_OUTPUT);
				if(output != null && output.hasItemMeta() && output.getItemMeta().hasDisplayName()) {
					text = output.getItemMeta().getDisplayName();
				}
				
				listener.onClick(text, e.getSlot() == SLOT_OUTPUT, e.getSlot());
			}
		}
	}
	
	/**
	 * Open an anvil GUI.
	 */
	public void openTextGUI(Player p, String placeholder, GUITextInputListener listener) {
		new AnvilGUI(plugin, p, placeholder, (player, text) -> {
			return text;
		});
		
		inInv.put(p, listener);
	}
}
