package nl.thewgbbroz.butils_v2.utils;

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
}
