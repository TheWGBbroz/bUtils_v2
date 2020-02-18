package nl.thewgbbroz.butils_v2.editsessions;

import org.bukkit.block.Block;

public interface IBlockChange {
	public void apply(Block block, boolean applyPhysics);
	public boolean matches(Block block);
}
