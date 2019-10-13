package nl.thewgbbroz.butils_v2.ghostblocks;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class GhostBlock {
	private final Block block;
	private final Location loc;
	
	private BlockData fakeData;
	
	private List<Player> viewers;
	private boolean isPublic;
	
	/**
	 * @param block The block to disguise for the viewers.
	 * @param fakeData The fake block data to disguise the block with.
	 * @param isPublic Whether or not the ghost block is public. If it is, any nearby players will get added to the viewers list automatically.
	 */
	public GhostBlock(Block block, BlockData fakeData, boolean isPublic) {
		this.block = block;
		this.loc = block.getLocation();
		
		this.fakeData = fakeData;
		this.isPublic = isPublic;
	}
	
	/**
	 * Forces an update for all viewers.
	 */
	public void forceUpdate() {
		viewers.forEach(p -> p.sendBlockChange(loc, fakeData));
	}
	
	/**
	 * @return The real block which this ghost block disguises.
	 */
	public Block getBlock() {
		return block;
	}
	
	/**
	 * @return The location of the ghost block.
	 */
	public Location getLocation() {
		return loc.clone();
	}
	
	/**
	 * @return The fake block data of the ghost block.
	 */
	public BlockData getFakeData() {
		return fakeData;
	}
	
	/**
	 * Sets the fake block data of the ghost block.
	 */
	public void setFakeData(BlockData fakeData) {
		this.fakeData = fakeData;
		forceUpdate();
	}
	
	/**
	 * @return A modifiable list of all viewers of the ghost block.
	 */
	public List<Player> getViewers() {
		return viewers;
	}
	
	/**
	 * @return Whether or not this ghost block is public. If it is, any nearby players will get added to the viewers list automatically.
	 */
	public boolean isPublic() {
		return isPublic;
	}
	
	/**
	 * Sets the ghost block to be public or not. If it is public, any nearby players will get added to the viewers list automatically.
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
