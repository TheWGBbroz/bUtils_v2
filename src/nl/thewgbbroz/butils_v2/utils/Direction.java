package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public enum Direction {
	NORTH(0, -1, 180, BlockFace.NORTH, "EAST",  "WEST"),
	EAST (1,  0,  -90, BlockFace.EAST,  "SOUTH", "NORTH"),
	SOUTH(0,  1,    0, BlockFace.SOUTH, "WEST",  "EAST"),
	WEST (-1, 0,  90, BlockFace.WEST,  "NORTH", "SOUTH");
	
	public static Direction fromBlockFace(BlockFace blockFace) {
		switch(blockFace) {
			case EAST:
				return Direction.EAST;
			case EAST_NORTH_EAST:
				return Direction.EAST;
			case EAST_SOUTH_EAST:
				return Direction.EAST;
			case NORTH:
				return Direction.NORTH;
			case NORTH_EAST:
				return Direction.NORTH; // ?
			case NORTH_NORTH_EAST:
				return Direction.NORTH;
			case NORTH_NORTH_WEST:
				return Direction.NORTH;
			case NORTH_WEST:
				return Direction.NORTH; // ?
			case SOUTH:
				return Direction.SOUTH;
			case SOUTH_EAST:
				return Direction.SOUTH; // ?
			case SOUTH_SOUTH_EAST:
				return Direction.SOUTH;
			case SOUTH_SOUTH_WEST:
				return Direction.SOUTH;
			case SOUTH_WEST:
				return Direction.SOUTH; // ?
			case WEST:
				return Direction.WEST;
			case WEST_NORTH_WEST:
				return Direction.WEST;
			case WEST_SOUTH_WEST:
				return Direction.WEST;
			default:
				return null;
		}
	}
	
	public static Direction fromVector(Vector vector) {
		return fromVector(vector.getX(), vector.getZ());
	}
	
	public static Direction fromVector(double x, double z) {
		double max = Math.max(Math.abs(x), Math.abs(z));
		if(max == 0)
			return null;
		
		int ix = (int) (x / max);
		int iz = (int) (z / max);
		
		// Favor the z direction (north and south)
		if(ix != 0 && iz != 0) ix = 0;
		
		Direction res = fromVectorExact((int) ix, (int) iz);
		return res != null ? res : NORTH; // Fall back to north
	}
	
	private static Direction fromVectorExact(int x, int z) {
		for(Direction dir : values()) {
			if(dir.dx == x && dir.dz == z)
				return dir;
		}
		
		return NORTH;
	}
	
	public static Direction fromLocation(Location location) {
		return fromVector(location.getDirection());
	}
	
	public final int dx, dz;
	public final float angleDeg;
	public final BlockFace blockFace;
	
	private final String cwName, ccwName;
	private Direction cw, ccw;
	
	private Direction(int dx, int dz, float angleDeg, BlockFace blockFace, String cwName, String ccwName) {
		this.dx = dx;
		this.dz = dz;
		
		this.angleDeg = angleDeg;
		
		this.blockFace = blockFace;
		
		this.cwName = cwName;
		this.ccwName = ccwName;
	}
	
	public Direction cw() {
		if(cw == null) cw = valueOf(cwName);
		return cw;
	}
	
	public Direction ccw() {
		if(ccw == null) ccw = valueOf(ccwName);
		return ccw;
	}
	
	public Direction applyRotation(Direction dir) {
		switch(dir) {
			case NORTH:
				return this;
			case EAST:
				return this.cw();
			case SOUTH:
				return this.cw().cw();
			case WEST:
				return this.ccw();
			default:
				return null;
		}
	}
	
	public Vector toVector() {
		return new Vector(dx, 0, dz);
	}
}
