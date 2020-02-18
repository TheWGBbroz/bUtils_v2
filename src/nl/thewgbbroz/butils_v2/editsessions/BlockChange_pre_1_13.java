package nl.thewgbbroz.butils_v2.editsessions;

import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockChange_pre_1_13 implements IBlockChange {
	private final Material type;
	private final byte data;
	
	public BlockChange_pre_1_13(Material type, byte data) {
		this.type = type;
		this.data = data;
	}
	
	@Override
	public void apply(Block block, boolean applyPhysics) {
		try {
			Method setData = block.getClass().getMethod("setData", byte.class);
			setData.invoke(block, data);
		}catch(Exception e) {}
		
		block.setType(type, applyPhysics);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean matches(Block block) {
		return block.getType() == type && block.getData() == data;
	}
}
