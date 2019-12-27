package nl.thewgbbroz.butils_v2.utils;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	private InventoryUtils() {
	}
	
	/**
	 * Sets the item without replacing it.
	 */
	public static boolean setItemSafe(int slot, ItemStack itemStack, Inventory inv) {
		if(inv.getItem(slot) == null) {
			// We're good.
			inv.setItem(slot, itemStack);
			
			return true;
		}else {
			int firstEmpty = inv.firstEmpty();
			if(firstEmpty != -1) {
				// We're good, swap the item that's currently in the slot.
				ItemStack old = inv.getItem(slot);
				inv.setItem(firstEmpty, old);
				inv.setItem(slot, itemStack);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gives the item without destroying it. If the item couldn't be placed in the players inventory,
	 * it will be dropped.
	 */
	public static void giveItemsSafe(Player player, ItemStack... items) {
		HashMap<Integer, ItemStack> couldntFit = player.getInventory().addItem(items);
		
		couldntFit.forEach((index, item) -> {
			// Drop the item!
			player.getWorld().dropItemNaturally(player.getLocation(), item);
		});
	}
	
	public static void giveItemSafe(Player player, int preferredSlot, ItemStack item) {
		if(preferredSlot < 0 || player.getInventory().getItem(preferredSlot) == null) {
			// Set the item in the preferred slot.
			player.getInventory().setItem(preferredSlot, item);
			return;
		}
		
		// Item could not be placed in the preferred slot.
		giveItemsSafe(player, item);
	}
}
