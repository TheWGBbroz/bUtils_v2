package nl.thewgbbroz.butils_v2.editsessions;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockChange_1_13 implements IBlockChange {
	private final BlockData blockData;
	
	public BlockChange_1_13(BlockData blockData) {
		this.blockData = blockData;
	}
	
	@Override
	public void apply(Block block, boolean applyPhysics) {
		block.setBlockData(blockData, applyPhysics);
	}
	
	@Override
	public boolean matches(Block block) {
		return blockData.matches(block.getBlockData());
	}
}
