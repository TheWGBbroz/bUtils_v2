package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SlotItemStack {
	private ItemStack itemStack;
	private int slot;
	
	public SlotItemStack(ItemStack itemStack, int slot) {
		setItemStack(itemStack);
		setSlot(slot);
	}
	
	public static SlotItemStack fromConfig(ConfigurationSection conf, String itemPath, String slotPath) {
		ItemStack itemStack = ItemUtils.itemstackFromConfig(conf.getConfigurationSection(itemPath));
		int slot = conf.getInt(slotPath);
		
		return new SlotItemStack(itemStack, slot);
	}
	
	public static SlotItemStack fromConfig(ConfigurationSection conf) {
		return fromConfig(conf, "item", "slot");
	}
	
	// Inserts a CLONE of the item stack in the inventory
	public void insert(Inventory inv) {
		if(slot >= inv.getSize())
			throw new IllegalArgumentException("Inventory does not have enhough slots for this item.");
		
		inv.setItem(slot, itemStack.clone());
	}
	
	// Inserts a CLONE of the item stack in the itemstack array
	public void insert(ItemStack[] inv) {
		if(slot >= inv.length)
			throw new IllegalArgumentException("Inventory does not have enhough slots for this item.");
		
		inv[slot] = itemStack.clone();
	}
	
	public boolean isSlot(int checkSlot) {
		return slot == checkSlot;
	}
	
	public ItemStack getItemStack() {
		return itemStack.clone();
	}
	
	public void setItemStack(ItemStack itemStack) {
		if(itemStack == null)
			throw new NullPointerException("ItemStack argument is null.");
		
		this.itemStack = itemStack;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public void setSlot(int slot) {
		if(slot < 0)
			throw new IllegalArgumentException("The item slot cannot be less than 0.");
		
		this.slot = slot;
	}
}
