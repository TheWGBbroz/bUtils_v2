package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.Sound;

public class SoundUtils {
	private SoundUtils() {
	}
	
	public static Sound getSoundSafe(String s, Sound def) {
		if(s == null)
			return def;
		
		try {
			s = s.toUpperCase().replace("-", "_").replace(" ", "_");
			return Sound.valueOf(s);
		}catch(Exception e) {
			return def;
		}
	}
	
	public static Sound getSoundSafe(String s) {
		return getSoundSafe(s, null);
	}
}
