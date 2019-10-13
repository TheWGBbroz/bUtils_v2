package nl.thewgbbroz.butils_v2.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerState {
	private final Player player;
	
	private Location location;
	
	private ItemStack[] inventoryContents;
	private ItemStack[] enderchestContents;
	private ItemStack[] armorContents;
	
	private boolean allowFlight;
	private boolean flying;
	private float flySpeed;
	
	private float walkSpeed;
	
	private GameMode gameMode;
	
	private double healthScale;
	private double health;
	
	private int foodLevel;
	private float saturation;
	
	private float exhaustion;
	
	private float exp;
	private int level;
	
	private List<PotionEffect> activePotionEffects = new ArrayList<>();
	
	private boolean canPickupItems;
	private float fallDistance;
	private int fireTicks;
	private int heldItemSlot;
	
	/**
	 * @param player The player
	 * 
	 * Automatically calls the load() function.
	 */
	public PlayerState(Player player) {
		this.player = player;
		
		load();
	}
	
	/**
	 * Loads the current player state in memory.
	 */
	public void load() {
		location = player.getLocation();
		
		inventoryContents = player.getInventory().getContents();
		ItemUtils.cloneAll(inventoryContents);
		
		enderchestContents = player.getEnderChest().getContents();
		ItemUtils.cloneAll(enderchestContents);
		
		armorContents = player.getEquipment().getArmorContents();
		ItemUtils.cloneAll(armorContents);
		
		gameMode = player.getGameMode();
		
		allowFlight = player.getAllowFlight();
		flying = player.isFlying();
		flySpeed = player.getFlySpeed();
		
		walkSpeed = player.getWalkSpeed();
		
		healthScale = player.isHealthScaled() ? player.getHealthScale() : -1;
		health = player.getHealth();
		
		foodLevel = player.getFoodLevel();
		saturation = player.getSaturation();
		
		exhaustion = player.getExhaustion();
		
		exp = player.getExp();
		level = player.getLevel();
		
		activePotionEffects.clear();
		player.getActivePotionEffects().forEach(pe -> activePotionEffects.add(PotionEffectUtils.clonePotionEffect(pe)));
		
		canPickupItems = player.getCanPickupItems();
		fallDistance = player.getFallDistance();
		fireTicks = player.getFireTicks();
		heldItemSlot = player.getInventory().getHeldItemSlot();
	}
	
	/**
	 * Restores the player state in memory to the real player state.
	 */
	public void restore() {
		player.teleport(location);
		
		player.getInventory().setContents(inventoryContents);
		player.getEnderChest().setContents(enderchestContents);
		player.getEquipment().setArmorContents(armorContents);
		
		player.setGameMode(gameMode);
		
		player.setAllowFlight(allowFlight);
		player.setFlying(flying);
		player.setFlySpeed(flySpeed);
		
		player.setWalkSpeed(walkSpeed);
		
		if(healthScale != -1) {
			// Health IS scaled!
			player.setHealthScale(healthScale);
		}else {
			// Health is NOT scaled.
			player.setHealthScaled(false);
		}
		player.setHealth(health);
		
		player.setFoodLevel(foodLevel);
		player.setSaturation(saturation);
		
		player.setExhaustion(exhaustion);
		
		player.setExp(exp);
		player.setLevel(level);
		
		player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
		activePotionEffects.forEach(pe -> player.addPotionEffect(pe, true));
		
		player.setCanPickupItems(canPickupItems);
		player.setFallDistance(fallDistance);
		player.setFireTicks(fireTicks);
		player.getInventory().setHeldItemSlot(heldItemSlot);
		
		// Update inventory lastly
		player.updateInventory();
	}
	
	/**
	 * Resets the player state to defaults.
	 */
	public void reset(Location teleportTo) {
		if(teleportTo != null)
			player.teleport(teleportTo);
		
		player.getInventory().clear();
		player.getEnderChest().clear();
		player.getEquipment().setArmorContents(new ItemStack[] {null, null, null, null});
		
		player.setGameMode(GameMode.SURVIVAL);
		
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlySpeed(1.0f);
		
		player.setWalkSpeed(1.0f);
		
		player.setHealthScaled(false);
		player.setHealth(20.0);
		
		player.setFoodLevel(20);
		player.setSaturation(10.0f);
		
		player.setExhaustion(0.0f);
		
		player.setExp(0.0f);
		player.setLevel(0);
		
		player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
		
		player.setCanPickupItems(true);
		player.setFallDistance(0.0f);
		player.setFireTicks(0);
		player.getInventory().setHeldItemSlot(0);
		
		// Update inventory lastly
		player.updateInventory();
	}
	
	/**
	 * Resets the player state to defaults, but does not teleport the player.
	 */
	public void reset() {
		reset(null);
	}
}
