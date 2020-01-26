package nl.thewgbbroz.butils_v2.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SerializationUtils {
	private SerializationUtils() {
	}
	
	public static void serializeLocation(DataOutputStream dos, Location loc) throws IOException {
		dos.writeUTF(loc.getWorld().getName());
		
		dos.writeDouble(loc.getX());
		dos.writeDouble(loc.getY());
		dos.writeDouble(loc.getZ());
		
		dos.writeFloat(loc.getYaw());
		dos.writeFloat(loc.getPitch());
	}
	
	public static Location deserializeLocation(DataInputStream dis) throws IOException {
		String worldName = dis.readUTF();
		
		double x = dis.readDouble();
		double y = dis.readDouble();
		double z = dis.readDouble();
		
		float yaw = dis.readFloat();
		float pitch = dis.readFloat();
		
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			return null;
		}
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public static void serializeBlock(DataOutputStream dos, Block block) throws IOException {
		dos.writeUTF(block.getWorld().getName());
		
		dos.writeInt(block.getX());
		dos.writeInt(block.getY());
		dos.writeInt(block.getZ());
	}
	
	public static Block deserializeBlock(DataInputStream dis) throws IOException {
		String worldName = dis.readUTF();
		
		int x = dis.readInt();
		int y = dis.readInt();
		int z = dis.readInt();
		
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			return null;
		}
		
		return world.getBlockAt(x, y, z);
	}
	
	public static <E> void serializeList(DataOutputStream dos, List<E> list, BiConsumer<DataOutputStream, E> serializer) throws IOException {
		dos.writeInt(list.size());
		for(E entry : list) {
			serializer.accept(dos, entry);
		}
	}
	
	public static <E> List<E> deserializeList(DataInputStream dis, Function<DataInputStream, E> deserializer) throws IOException {
		List<E> list = new ArrayList<>();
		
		int listSize = dis.readInt();
		for(int i = 0; i < listSize; i++) {
			E entry = deserializer.apply(dis);
			list.add(entry);
		}
		
		return list;
	}
}
