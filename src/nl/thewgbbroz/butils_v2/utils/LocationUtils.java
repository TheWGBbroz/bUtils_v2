package nl.thewgbbroz.butils_v2.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class LocationUtils {
	private LocationUtils() {
	}
	
	public static String stringifyLocation(Location loc) {
		return loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + loc.getYaw() + " " + loc.getPitch();
	}
	
	public static Location parseLocation(String s) {
		if(!s.contains(" "))
			throw new IllegalArgumentException("Invalid location format");
		
		String[] parts = s.split(" ");
		if(parts.length < 4)
			throw new IllegalArgumentException("Invalid location format");
		
		String worldName = parts[0];
		World world = Bukkit.getWorld(worldName);
		if(world == null)
			throw new IllegalArgumentException("Invalid world name");
		
		double x, y, z;
		float yaw = 0;
		float pitch = 0;
		try {
			x = Double.parseDouble(parts[1]);
			y = Double.parseDouble(parts[2]);
			z = Double.parseDouble(parts[3]);
			
			if(parts.length >= 6) {
				yaw = Float.parseFloat(parts[4]);
				pitch = Float.parseFloat(parts[5]);
			}
		}catch(NumberFormatException e) {
			throw new IllegalArgumentException("Invalid coordinates: " + e.toString());
		}
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public static <E extends Entity> E getClosest(Entity en, List<E> group, double maxDist) {
		maxDist = maxDist * maxDist;
		
		E closest = null;
		double closestDistSq = Double.POSITIVE_INFINITY;
		
		for(E target : group) {
			double distSq = en.getLocation().distanceSquared(target.getLocation());
			
			if(distSq < maxDist && distSq < closestDistSq) {
				closest = target;
				closestDistSq = distSq;
			}
		}
		
		return closest;
	}
	
	public static <E extends Entity> E getClosest(Entity en, List<E> group) {
		return getClosest(en, group, Double.POSITIVE_INFINITY);
	}
	
	public static boolean blockEquals(Location a, Location b) {
		return a.getWorld() == b.getWorld() && a.getBlockX() == b.getBlockX() &&
				a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
	}
}
