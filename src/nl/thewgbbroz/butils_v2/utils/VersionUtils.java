package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.Bukkit;

public class VersionUtils {
	private VersionUtils() {
	}
	
	private static boolean hasCachedVersion = false;
	
	private static String cachedVersion;
	private static int major, minor, patch;
	private static boolean is113;
	
	private static void cacheVersion() {
		if(!hasCachedVersion) {
			// Get the version
			try {
				String version = Bukkit.getBukkitVersion();
				version = version.split("-")[0];
				
				cachedVersion = version;
				
				String[] versionParts = version.split("\\.");
				
				major = Integer.parseInt(versionParts.length < 1 ? "0" : versionParts[0]);
				minor = Integer.parseInt(versionParts.length < 2 ? "0" : versionParts[1]);
				patch = Integer.parseInt(versionParts.length < 3 ? "0" : versionParts[2]);
				
				is113 = (major == 1 && minor >= 13) || (major > 1);
			}catch(Exception e) {
				Bukkit.getLogger().warning("Could not fetch version from version string '" + Bukkit.getBukkitVersion() + "'! " + e.toString());
				
				cachedVersion = null;
				major = minor = patch = 0;
				is113 = false;
			}
			
			hasCachedVersion = true;
		}
	}
	
	/**
	 * @return The version string of the minecraft version. Eg "1.8.8" or "1.13.3".
	 */
	public static String getVersionString() {
		cacheVersion();
		return cachedVersion;
	}
	
	/**
	 * @return Whether or not the current minecraft version is using the 1.13+ api.
	 */
	public static boolean is1_13() {
		cacheVersion();
		return is113;
	}
	
	/**
	 * @return The major, minor and patch versions in an integer array of length 3.
	 */
	public static int[] getMajorMinorPatch() {
		cacheVersion();
		return new int[] {major, minor, patch};
	}
	
	/**
	 * @return The major of the current minecraft version the server is running.
	 */
	public static int getMajor() {
		cacheVersion();
		return major;
	}
	
	/**
	 * @return The minor of the current minecraft version the server is running.
	 */
	public static int getMinor() {
		cacheVersion();
		return minor;
	}
	
	/**
	 * @return The patch of the current minecraft version the server is running.
	 */
	public static int getPatch() {
		cacheVersion();
		return patch;
	}
}
