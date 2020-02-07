package nl.thewgbbroz.butils_v2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	private ItemUtils() {
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack itemstackFromConfig(ConfigurationSection conf) {
		Material type = Material.STONE;
		if(conf.contains("type")) {
			type = Material.getMaterial(conf.getString("type").toUpperCase());
			
			if(type == null) {
				// Invalid type..
				type = Material.STONE;
			}
		}
		
		int amount = 1;
		if(conf.contains("amount")) {
			amount = conf.getInt("amount");
			
			if(amount < 1)
				amount = 1;
			if(amount > type.getMaxStackSize())
				amount = type.getMaxStackSize();
		}
		
		ItemStack is;
		if(VersionUtils.is1_13()) {
			// 1.13+ implementation
			is = new ItemStack(type, amount);
		}else {
			// Pre 1.13 implementation
			short damage = 0;
			if(conf.contains("damage")) {
				damage = (short) conf.getInt("damage");
			}else if(conf.contains("durability")) {
				damage = (short) conf.getInt("durability");
			}
			
			if(damage < 0)
				damage = 0;
			if(damage > type.getMaxDurability())
				damage = type.getMaxDurability();
			
			is = new ItemStack(type, amount, damage);
		}
		
		ItemMeta im = is.getItemMeta();
		
		if(conf.contains("name")) {
			String name = conf.getString("name");
			name = ChatColor.translateAlternateColorCodes('&', name);
			
			im.setDisplayName(ChatColor.RESET + name);
		}
		
		if(conf.contains("lore")) {
			List<String> lore = conf.getStringList("lore");
			lore.replaceAll(str -> ChatColor.translateAlternateColorCodes('&', str));
			
			im.setLore(lore);
		}
		
		is.setItemMeta(im);
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static void itemstackToConfig(ItemStack item, ConfigurationSection conf) {
		conf.set("type", item.getType().name());
		
		conf.set("amount", item.getAmount());
		
		if(!VersionUtils.is1_13()) {
			conf.set("damage", item.getDurability());
		}
		
		if(item.hasItemMeta()) {
			ItemMeta im = item.getItemMeta();
			
			if(im.hasDisplayName()) {
				String displayName = im.getDisplayName();
				displayName = displayName.replace("§", "&");
				while(displayName.toLowerCase().startsWith("&r"))
					displayName = displayName.substring(2);
				
				conf.set("name", displayName);
			}
			
			if(im.hasLore()) {
				List<String> configLore = new ArrayList<>(im.getLore());
				
				configLore.replaceAll(str -> {
					str = str.replace("§", "&");
					while(str.toLowerCase().startsWith("&r"))
						str = str.substring(2);
					
					return str;
				});
				
				conf.set("lore", configLore);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack parseItemStack(String s) {
		// name amount damage name:___ ench:___ ench:___ lore:_,_,_
		
		if(s == null || s.equalsIgnoreCase("null") || s.equalsIgnoreCase("none"))
			return null;
		
		String[] parts;
		if(s.contains(" "))
			parts = s.split(" ");
		else
			parts = new String[] { s };
		
		Material mat = Material.getMaterial(parts[0].toUpperCase());
		
		if(mat == null)
			return null;
		
		int amount = 1;
		if(parts.length > 1) {
			// Amount
			try{
				amount = Integer.parseInt(parts[1]);
			}catch(Exception e) {}
		}
		
		ItemStack is;
		if(VersionUtils.is1_13()) {
			short damage = 0;
			if(parts.length > 2) {
				// Damage
				try{
					damage = Short.parseShort(parts[2]);
				}catch(Exception e) {}
			}
			
			is = new ItemStack(mat, amount, damage);
		}else {
			is = new ItemStack(mat, amount);
		}
		
		if(parts.length > 3) {
			ItemMeta im = is.getItemMeta();
			
			for(int i = 3; i < parts.length; i++) {
				try{
					String part = parts[i];
					
					if(part.startsWith("name:")) {
						String name = part.substring("name:".length());
						name = ChatColor.translateAlternateColorCodes('&', name);
						name = name.replace("_", " ");
						im.setDisplayName(name);
					}else if(part.startsWith("ench:")) {
						String[] parts2 = part.substring("ench:".length()).split("/");
						
						Enchantment ench = Enchantment.getByName(parts2[0].toUpperCase());
						if(ench == null) continue;
						
						int lvl = Integer.parseInt(parts2[1]);
						
						im.addEnchant(ench, lvl, true);
					}else if(part.startsWith("lore:")) {
						String p = part.substring("lore:".length());
						String[] parts2;
						if(p.contains(","))
							parts2 = p.split(",");
						else
							parts2 = new String[] { p };
						
						List<String> lore = new ArrayList<>();
						for(String l : parts2) {
							lore.add(ChatColor.translateAlternateColorCodes('&', l).replace("_", " "));
						}
						
						im.setLore(lore);
					}
				}catch(Exception e) {}
			}
			
			is.setItemMeta(im);
		}
		
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static String stringifyItemStack(ItemStack is) {
		// item amount damage name:___ ench:___ ench:___ lore:_,_,_
		
		if(is == null)
			return "null";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(is.getType().name());
		sb.append(" " + is.getAmount());
		
		if(!VersionUtils.is1_13()) {
			sb.append(" " + is.getDurability());
		}
		
		if(is.hasItemMeta() && is.getItemMeta().hasDisplayName())
			sb.append(" name:" + is.getItemMeta().getDisplayName().replace(" ", "_"));
		
		for(Enchantment ench : is.getEnchantments().keySet()) {
			int lvl = is.getEnchantments().get(ench);
			sb.append(" ench:" + ench.getName() + "/" + lvl);
		}
		
		if(is.hasItemMeta()) {
			ItemMeta im = is.getItemMeta();
			
			if(im.hasLore()) {
				sb.append(" lore:");
				for(String l : im.getLore())
					sb.append(l.replace(" ", "_") + ",");
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		
		return sb.toString();
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack createItem(Material type, int amount, int damage, String name, List<String> lore, Consumer<ItemMeta> customMetaConsumer) {
		ItemStack is;
		if(!VersionUtils.is1_13() && damage >= 0) {
			is = new ItemStack(type, amount, (short) damage);
		}else {
			is = new ItemStack(type, amount);
		}
		
		ItemMeta im = is.getItemMeta();
		
		if(name != null) {
			im.setDisplayName(ChatColor.RESET + name);
		}
		
		if(lore != null) {
			im.setLore(lore);
		}
		
		if(customMetaConsumer != null) {
			customMetaConsumer.accept(im);
		}
		
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack createItem(String typeName, int amount, int damage, String name, List<String> lore, Consumer<ItemMeta> customMetaConsumer) {
		Material type = Material.getMaterial(typeName.toUpperCase());
		if(type == null)
			type = Material.STONE;
		
		return createItem(type, amount, damage, name, lore, customMetaConsumer);
	}
	
	public static ItemStack createItem(Material type, int amount, int damage, String name, List<String> lore) {
		return createItem(type, amount, damage, name, lore, null);
	}
	
	public static ItemStack createItem(String typeName, int amount, int damage, String name, List<String> lore) {
		return createItem(typeName, amount, damage, name, lore, null);
	}
	
	// -- 1.13 methods (without the damage argument)
	public static ItemStack createItem(Material type, int amount, String name, List<String> lore, Consumer<ItemMeta> customMetaConsumer) {
		return createItem(type, amount, -1, name, lore, customMetaConsumer);
	}
	
	public static ItemStack createItem(String typeName, int amount, String name, List<String> lore, Consumer<ItemMeta> customMetaConsumer) {
		return createItem(typeName, amount, -1, name, lore, customMetaConsumer);
	}
	
	public static ItemStack createItem(Material type, int amount, String name, List<String> lore) {
		return createItem(type, amount, -1, name, lore, null);
	}
	
	public static ItemStack createItem(String typeName, int amount, String name, List<String> lore) {
		return createItem(typeName, amount, -1, name, lore, null);
	}
	// --
	
	public static final int CHECK_MATERIAL			= 0;
	public static final int CHECK_DURABILITY		= 1;
	public static final int CHECK_AMOUNT			= 2;
	public static final int CHECK_NAME				= 3;
	public static final int CHECK_LORE				= 4;
	public static final int CHECK_ENCHANTS			= 5;
	public static final int CHECK_ENCHANT_LEVELS	= 6;
	
	@SuppressWarnings("deprecation")
	public static boolean isSimilar(ItemStack a, ItemStack b, int... checkAttributes) {
		if(a == null || b == null) {
			return a == b;
		}
		
		if(ArrayUtils.contains(checkAttributes, CHECK_MATERIAL) && !a.getType().equals(b.getType()))
			return false;
		
		if(!VersionUtils.is1_13()) {
			if(ArrayUtils.contains(checkAttributes, CHECK_DURABILITY) && a.getDurability() != b.getDurability())
				return false;
		}
		
		if(ArrayUtils.contains(checkAttributes, CHECK_AMOUNT) && a.getAmount() != b.getAmount())
			return false;
		
		// After this point item meta stuff
		
		ItemMeta im_a = a.getItemMeta();
		ItemMeta im_b = b.getItemMeta();
		
		if(im_a == null || im_b == null) {
			return im_a == im_b;
		}
		
		if(ArrayUtils.contains(checkAttributes, CHECK_NAME)) {
			String dis_a = im_a.getDisplayName();
			String dis_b = im_b.getDisplayName();
			
			if(dis_a == null || dis_b == null) {
				if(dis_a != dis_b)
					return false;
			}else {
				if(!dis_a.equals(dis_b))
					return false;
			}
		}
		
		if(ArrayUtils.contains(checkAttributes, CHECK_LORE)) {
			List<String> lore_a = im_a.getLore();
			List<String> lore_b = im_b.getLore();
			
			if(lore_a == null || lore_b == null) {
				if(lore_a != lore_b)
					return false;
			}else {
				if(lore_a.size() != lore_b.size())
					return false;
				
				for(int i = 0; i < lore_a.size(); i++) {
					if(!lore_a.get(i).equals(lore_b.get(i)))
						return false;
				}
			}
		}
		
		if(ArrayUtils.contains(checkAttributes, CHECK_ENCHANTS)) {
			Map<Enchantment, Integer> ench_a = im_a.getEnchants();
			Map<Enchantment, Integer> ench_b = im_b.getEnchants();
			
			if(ench_a == null || ench_b == null) {
				if(ench_a != ench_b)
					return false;
			}else {
				if(ench_a.size() != ench_b.size())
					return false;
				
				boolean checkLevels = ArrayUtils.contains(checkAttributes, CHECK_ENCHANT_LEVELS);
				for(Enchantment ench : ench_a.keySet()) {
					if(!ench_b.containsKey(ench))
						return false;
					
					if(checkLevels) {
						int lvl_a = ench_a.get(ench);
						int lvl_b = ench_b.get(ench);
						if(lvl_a != lvl_b)
							return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public static boolean isSimilarMultiple(ItemStack master, ItemStack[] others, int... checkAttributes) {
		for(ItemStack other : others) {
			if(isSimilar(master, other, checkAttributes))
				return true;
		}
		
		return false;
	}
	
	public static String serializeItemStack(ItemStack is) {
		YamlConfiguration config = new YamlConfiguration();
		config.set("is", is);
		
		return config.saveToString();
	}
	
	public static ItemStack deserializeItemStack(String s) {
		YamlConfiguration config = new YamlConfiguration();
		
		try {
			config.loadFromString(s);
		}catch(InvalidConfigurationException e) {
			throw new IllegalArgumentException("Invalid format!");
		}
		
		ItemStack is = config.getItemStack("is");
		if(is == null) {
			throw new IllegalArgumentException("Invalid format!");
		}
		
		return is;
	}
	
	public static Material getMaterialSafe(String s, Material def) {
		if(s == null)
			return def;
		
		try {
			s = s.toUpperCase().replace("-", "_").replace(" ", "_");
			return Material.valueOf(s);
		}catch(Exception e) {
			return def;
		}
	}
	
	public static Material getMaterialSafe(String s) {
		return getMaterialSafe(s, Material.STONE);
	}
	
	public static void cloneAll(ItemStack[] items) {
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null)
				continue;
			
			items[i] = items[i].clone();
		}
	}
	
	public static ItemStack[] recipeToInventory(Recipe recipe) {
		ItemStack[] res = new ItemStack[9];
		
		if(recipe instanceof ShapedRecipe) {
			ShapedRecipe shaped = (ShapedRecipe) recipe;
			
			String[] shape;
			if(shaped.getShape().length == 0) {
				shape = new String[] {"", "", ""};
			}else if(shaped.getShape().length == 1) {
				shape = new String[] {
						"",
						shaped.getShape()[0],
						""
				};
			}else if(shaped.getShape().length == 2) {
				shape = new String[] {
						"",
						shaped.getShape()[0],
						shaped.getShape()[1]
				};
			}else {
				shape = new String[] {
						shaped.getShape()[0],
						shaped.getShape()[1],
						shaped.getShape()[2]
				};
			}
			
			StringBuilder shapeString = new StringBuilder(9);
			for(int i = 0; i < shape.length; i++) {
				String s = shape[i];
				
				if(s.length() == 0) {
					s = "   ";
				}else if(s.length() == 1) {
					s = " " + s + " ";
				}else if(s.length() == 2) {
					s = s + " ";
				}else if(s.length() > 3) {
					s = s.substring(0, 3);
				}
				
				shapeString.append(s);
			}
			
			for(int i = 0; i < res.length; i++) {
				char c = shapeString.charAt(i);
				ItemStack item = shaped.getIngredientMap().get(c);
				if(item == null) {
					continue;
				}
				
				res[i] = item.clone();
			}
		}else if(recipe instanceof ShapelessRecipe) {
			ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
			for(int i = 0; i < shapeless.getIngredientList().size(); i++) {
				res[i] = shapeless.getIngredientList().get(i).clone();
			}
		}
		else {
			throw new UnsupportedOperationException("Recipe type not implemented.");
		}
		
		return res;
	}
}
