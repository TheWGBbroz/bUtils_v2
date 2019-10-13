package nl.thewgbbroz.butils_v2.custominventory;

import org.bukkit.inventory.ItemStack;

public interface SimpleGUIListener {
	/**
	 * @param contents The inventory contents upon closing.
	 * 
	 * This method gets called when an inventory gets closed.
	 */
	public void onClose(ItemStack[] contents);
	
	/**
	 * @return If this returns TRUE, the event will be canceled.
	 * 
	 * This method gets called when a player clicks in an inventory.
	 */
	public boolean onClick(ItemStack item, int slot, boolean clickedInGUI);
}
