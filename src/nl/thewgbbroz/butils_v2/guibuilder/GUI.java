package nl.thewgbbroz.butils_v2.guibuilder;

import java.util.function.Function;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI {
	private GUIBuilder builder;
	private Function<Integer, ItemStack> populator;
	private Function<String, ItemStack> customAliases;
	
	private ItemStack[] gui;
	
	protected GUI(GUIBuilder builder, Function<Integer, ItemStack> populator, Function<String, ItemStack> customAliases) {
		this.builder = builder;
		this.populator = populator;
		this.customAliases = customAliases;
	}
	
	public ItemStack[] build() {
		gui = new ItemStack[builder.size];
		
		// Set all solid items
		builder.solidItems.forEach((slot, alias) -> {
			ItemStack item = builder.getItem(alias);
			gui[slot] = item;
		});
		
		// Apply custom populator
		if(populator != null) {
			for(int slot = 0; slot < gui.length; slot++) {
				ItemStack item = populator.apply(slot);
				if(item != null) {
					gui[slot] = item.clone();
				}
			}
		}
		
		// Apply custom aliases
		if(customAliases != null) {
			builder.solidItems.forEach((slot, itemAlias) -> {
				ItemStack item = customAliases.apply(itemAlias.toLowerCase());
				if(item != null) {
					gui[slot] = item.clone();
				}
			});
		}
		
		return gui;
	}
	
	public void build(Inventory inv) {
		if(inv.getSize() != builder.size)
			throw new IllegalArgumentException("The inventory's size doesn't match with the GUI size.");
		
		build();
		for(int i = 0; i < gui.length; i++) {
			ItemStack item = gui[i];
			if(item != null) item = item.clone();
			
			inv.setItem(i, item);
		}
	}
	
	public GUIBuilder getBuilder() {
		return builder;
	}
}
