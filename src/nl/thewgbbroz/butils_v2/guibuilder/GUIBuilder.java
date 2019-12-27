package nl.thewgbbroz.butils_v2.guibuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.utils.ItemUtils;

public class GUIBuilder {
	private static final ItemStack DEFAULT_ITEM = ItemUtils.createItem(
			Material.STONE,
			1,
			0,
			ChatColor.GRAY + "Unknown item",
			Arrays.asList(
					ChatColor.GRAY + "We could not find this item.", ChatColor.GRAY + "Please check your config."
			)
	);
	
	private static final String BACKGROUND_ALIAS = "background";
	
	private final WGBPlugin plugin;
	private final ConfigurationSection cfgSection;
	private final String cfgName;
	
	protected int size;
	private GUIBackground backgroundType;
	protected Map<Integer, String> solidItems = new HashMap<>();
	
	private Map<String, ItemStack> itemAliases = new HashMap<>();
	
	public GUIBuilder(WGBPlugin plugin, ConfigurationSection conf) {
		this.plugin = plugin;
		this.cfgSection = conf;
		this.cfgName = conf.getName();
		
		int height = conf.getInt("height");
		if(height <= 0 || height > 6) {
			plugin.getLogger().warning("Invalid height '" + height + "' for GUI '" + cfgName + "'!");
			height = 6;
		}
		this.size = height * 9;
		
		// Load the background type
		loadBackgroundType();
		
		// Apply the background type to the solid item list
		applyBackground();
		
		// Load custom solid items
		importCustomSolidItems();
		
		// Import all alias - item relations.
		if(conf.contains("item-aliases")) {
			importItemAliases(conf.getConfigurationSection("item-aliases"));
		}else {
			plugin.getLogger().warning("No item aliases set for gui '" + cfgName + "'! Define some by creating the item-aliases section.");
		}
	}
	
	private void loadBackgroundType() {
		try {
			this.backgroundType = GUIBackground.valueOf(cfgSection.getString("background-type", GUIBackground.NONE.name()).toUpperCase());
		}catch(IllegalArgumentException e) {
			plugin.getLogger().warning("Invalid background-type for gui '" + cfgName + "'. Available background types:");
			for(GUIBackground type : GUIBackground.values())
				plugin.getLogger().warning(type.name());
			
			this.backgroundType = GUIBackground.NONE;
		}
	}
	
	private void applyBackground() {
		if(backgroundType != GUIBackground.NONE) {
			if(backgroundType == GUIBackground.SOLID) {
				for(int slot = 0; slot < size; slot++) {
					solidItems.put(slot, BACKGROUND_ALIAS);
				}
			}else if(backgroundType == GUIBackground.EDGES) {
				final int width = 9;
				final int height = size / width;
				
				// Horizontal lines
				for(int x = 0; x < width; x++) {
					solidItems.put(x + 0 * width, BACKGROUND_ALIAS);
					solidItems.put(x + (height - 1) * width, BACKGROUND_ALIAS);
				}
				
				// Vertical lines
				for(int y = 0; y < height; y++) {
					solidItems.put(0 + y * width, BACKGROUND_ALIAS);
					solidItems.put((width - 1) + y * width, BACKGROUND_ALIAS);
				}
			}
			else {
				plugin.getLogger().warning("GUIBackground " + backgroundType.name() + " not implemented yet.");
			}
		}
	}
	
	private void importCustomSolidItems() {
		for(String s : cfgSection.getStringList("solid-items")) {
			try {
				String[] parts = s.split(" ");
				if(parts.length != 2) throw new IllegalStateException();
				
				String slotsStr = parts[0];
				String itemAlias = parts[1];
				
				List<Integer> slots = parseSlots(slotsStr);
				
				for(Integer slot : slots) {
					solidItems.put(slot, itemAlias);
				}
			}catch(Exception e) {
				plugin.getLogger().warning("Invalid solid item format for gui '" + cfgName + "'. Examples:");
				plugin.getLogger().warning("- 0 back_btn # slot 0");
				plugin.getLogger().warning("- 0-9 stone # slots 0 through 9");
				plugin.getLogger().warning("- 2x2 next_btn # slot at x=2, y=2");
				continue;
			}
		}
	}
	
	private void importItemAliases(ConfigurationSection conf) {
		for(String alias : conf.getKeys(false)) {
			ItemStack item = ItemUtils.itemstackFromConfig(conf.getConfigurationSection(alias));
			if(item == null) continue;
			
			addItemAlias(alias, item);
		}
	}
	
	/**
	 * Creates a GUI
	 */
	public GUI create(Function<Integer, ItemStack> populator, Function<String, ItemStack> customAliases) {
		return new GUI(this, populator, customAliases);
	}
	
	/**
	 * Adds (or overrides) an item to the alias list.
	 */
	public void addItemAlias(String alias, ItemStack item) {
		itemAliases.put(alias.toLowerCase(), item.clone());
	}
	
	/**
	 * @return The item with the alias, or the built-in default item of none could be found.
	 */
	public ItemStack getItem(String alias) {
		ItemStack item = itemAliases.get(alias.toLowerCase());
		if(item == null) {
			item = DEFAULT_ITEM.clone();
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.GRAY + alias);
			item.setItemMeta(im);
		}
		
		return item.clone();
	}
	
	/**
	 * @param item The item to compare with the alias
	 * @param alias The item to compare to the item
	 * @param checkAttributes The attributes to compare with (as defined in {@link ItemUtils}
	 * 
	 * @return Whether or not the items are similar
	 */
	public boolean itemEquals(ItemStack item, String alias, int... checkAttributes) {
		ItemStack other = itemAliases.get(alias.toLowerCase());
		if(other == null) return false;
		
		return ItemUtils.isSimilar(item, other, checkAttributes);
	}
	
	/**
	 * @return Calls {@link GUIBuilder#itemEquals(ItemStack, String, int...)} with the following checks:
	 * - {@link ItemUtils#CHECK_MATERIAL}
	 * - {@link ItemUtils#CHECK_NAME}
	 * - {@link ItemUtils#CHECK_LORE}
	 * - {@link ItemUtils#CHECK_AMOUNT}
	 * - {@link ItemUtils#CHECK_DURABILITY}
	 */
	public boolean itemEquals(ItemStack item, String alias) {
		return itemEquals(item, alias, ItemUtils.CHECK_MATERIAL, ItemUtils.CHECK_NAME, ItemUtils.CHECK_LORE,
				ItemUtils.CHECK_AMOUNT, ItemUtils.CHECK_DURABILITY);
	}
	
	/**
	 * @param guiSlot The slot to compare with the alias
	 * @param alias The alias to compare with the slot
	 * 
	 * @return Whether or not the item at slot guiSlot is equal to the alias alias.
	 */
	public boolean itemEquals(int guiSlot, String... aliases) {
		String realAlias = solidItems.get(guiSlot);
		if(realAlias == null)
			return false;
		
		for(String alias : aliases) {
			if(realAlias.equalsIgnoreCase(alias))
				return true;
		}
		
		return false;
	}
	
	/**
	 * @param guiSlot The slot to check for
	 * @return The alias, or null if none, which is at the guiSlot.
	 */
	public String getAlias(int guiSlot) {
		return solidItems.get(guiSlot);
	}
	
	public int getSize() {
		return size;
	}
	
	public int getHeight() {
		return size / 9;
	}
	
	private static List<Integer> parseSlots(String slotsStr) throws Exception {
		List<Integer> slots = new ArrayList<>();
		
		if(slotsStr.contains("-") && slotsStr.contains("x")) {
			// 1x1-3x3 (cube with coordinates)
			// 1x1 is top left!
			
			String[] coordParts = slotsStr.split("-");
			if(coordParts.length != 2) throw new IllegalStateException();
			
			
			String[] pos1Parts = coordParts[0].split("x");
			if(pos1Parts.length != 2) throw new IllegalStateException();
			
			int x1 = Integer.parseInt(pos1Parts[0]) - 1;
			int y1 = Integer.parseInt(pos1Parts[1]) - 1;
			
			
			String[] pos2Parts = coordParts[1].split("x");
			if(pos2Parts.length != 2) throw new IllegalStateException();
			
			int x2 = Integer.parseInt(pos2Parts[0]) - 1;
			int y2 = Integer.parseInt(pos2Parts[1]) - 1;
			
			int minx = Math.min(x1, x2);
			int miny = Math.min(y1, y2);
			
			int maxx = Math.max(x1, x2);
			int maxy = Math.max(y1, y2);
			
			for(int x = minx; x <= maxx; x++) {
				for(int y = miny; y <= maxy; y++) {
					int slot = x + y * 9; // width = 9
					slots.add(slot);
				}
			}
		}else if(slotsStr.contains("-")) {
			// 0-9 (list of slots)
			
			String[] slotsParts = slotsStr.split("-");
			if(slotsParts.length != 2) throw new IllegalStateException();
			
			int start = Integer.parseInt(slotsParts[0]);
			int end = Integer.parseInt(slotsParts[1]);
			
			for(int i = start; i < end; i++)
				slots.add(i);
		}else if(slotsStr.contains("x")) {
			// 2x2 (coordinates)
			// 1x1 is top left!
			
			String[] slotsParts = slotsStr.split("x");
			if(slotsParts.length != 2) throw new IllegalStateException();
			
			int x = Integer.parseInt(slotsParts[0]) - 1;
			int y = Integer.parseInt(slotsParts[1]) - 1;
			
			int slot = x + y * 9; // width = 9
			slots.add(slot);
		}else {
			// 1 (single slot)
			
			slots.add(Integer.parseInt(slotsStr));
		}
		
		return slots;
	}
}
