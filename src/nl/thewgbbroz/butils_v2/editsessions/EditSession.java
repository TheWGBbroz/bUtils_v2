package nl.thewgbbroz.butils_v2.editsessions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class EditSession {
	private Map<Block, IBlockChange> changes = new HashMap<>();
	private boolean applyPhysics;
	
	private BukkitTask currentTask = null;
	private List<Block> blockOrder = new ArrayList<>();
	
	/**
	 * @param applyPhysics Whether to apply physics when changing the blocks or not
	 */
	public EditSession(boolean applyPhysics) {
		this.applyPhysics = applyPhysics;
	}
	
	/**
	 * {@link #EditSession(boolean)} with a default value of <code>true</code> for applying physics.
	 */
	public EditSession() {
		this(true);
	}
	
	/**
	 * @param block The block to change
	 * @param change The change to apply
	 * 
	 * Adds a change to the change queue if the block doesn't already match with the change.
	 */
	public void setBlockCustom(Block block, IBlockChange change) {
		if(currentTask != null) {
			throw new IllegalStateException("A change task is running!");
		}
		
		if(!change.matches(block)) {
			changes.put(block, change);
		}
	}
	
	/**
	 * @param block The block to change the material of
	 * @param type The material to change the block to
	 * @param data The data to change on the block
	 * 
	 * Adds the new material to the change queue, if the current block's material
	 * doesn't already match with the given material.
	 */
	public void setBlock(Block block, Material type, byte data) {
		setBlockCustom(block, new BlockChange_pre_1_13(type, data));
	}
	
	/**
	 * @param block The block to change the material of
	 * @param type The material to change the block to
	 * 
	 * Adds the new material to the change queue, if the current block's material
	 * doesn't already match with the given material.
	 */
	public void setBlock(Block block, Material type) {
		setBlockCustom(block, new BlockChange_pre_1_13(type, (byte) 0));
	}
	
	/**
	 * @param block The block to change the block data of
	 * @param blockData The block data to change the block to
	 * 
	 * Adds the new block data to the change queue, if the current block's block daat
	 * doesn't already match with the given block data.
	 */
	public void setBlock(Block block, BlockData blockData) {
		setBlockCustom(block, new BlockChange_1_13(blockData));
	}
	
	/**
	 * Forces a flush without creating a new task.
	 */
	public void forceFlush() {
		changes.forEach((block, change) -> {
			change.apply(block, applyPhysics);
		});
	}
	
	/**
	 * @param plugin A reference to the plugin
	 * @param ticksDelay The delay in ticks between changing blocks
	 * @param blocksPerTick The amount of blocks to change per tick
	 * 
	 * Creates a task to change all blocks in the queue gradually.
	 */
	public void start(JavaPlugin plugin, int ticksDelay, int blocksPerTick, Callback callback) {
		if(currentTask != null) {
			throw new IllegalStateException("This edit session already has a running task!");
		}
		
		long taskStart = System.currentTimeMillis();
		
		// Order the blocks
		blockOrder.clear();
		blockOrder.addAll(changes.keySet());
		
		Collections.sort(blockOrder, new Comparator<Block>() {
			@Override
			public int compare(Block o1, Block o2) {
				return o2.getY() - o1.getY();
			}
		});
		
		this.currentTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			boolean completed = tick(blocksPerTick);
			if(completed) {
				currentTask.cancel();
				currentTask = null;
				
				blockOrder.clear();
				
				if(callback != null) {
					long taskElapsed = System.currentTimeMillis() - taskStart;
					callback.completed(taskElapsed);
				}
			}
		}, 0, ticksDelay);
	}
	
	/**
	 * @param plugin A reference to the plugin
	 * @param ticksDelay The delay in ticks between changing blocks
	 * @param numTicks The number of ticks to use for this operation.
	 * 
	 * Calculates the blocksPerTick value and calls {@link #start(JavaPlugin, int, int)} with this value.
	 */
	public void startMaxTime(JavaPlugin plugin, int ticksDelay, int numTicks, Callback callback) {
		int blocksPerTick = (changes.size() * ticksDelay) / numTicks;
		if(blocksPerTick < 1) blocksPerTick = 1;
		
		start(plugin, ticksDelay, blocksPerTick, callback);
	}
	
	/**
	 * @return Returns true if the task has been completed.
	 */
	private boolean tick(int blocksPerTick) {
		if(changes.isEmpty()) {
			return true;
		}
		
		for(int i = 0; i < blocksPerTick; i++) {
			if(blockOrder.isEmpty()) {
				return true;
			}
			
			Block block = blockOrder.get(blockOrder.size() - 1);
			IBlockChange change = changes.get(block);
			
			if(change != null) {
				change.apply(block, applyPhysics);
			}
			
			blockOrder.remove(blockOrder.size() - 1);
			changes.remove(block);
		}
		
		return false;
	}
	
	public static interface Callback {
		public void completed(long elapsedMs);
	}
}
