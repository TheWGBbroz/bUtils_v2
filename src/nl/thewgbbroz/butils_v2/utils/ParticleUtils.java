package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.Particle;

public class ParticleUtils {
	private ParticleUtils() {
	}
	
	public static Particle getParticleSafe(String s, Particle def) {
		if(s == null)
			return def;
		
		try {
			s = s.toUpperCase().replace("-", "_").replace(" ", "_");
			return Particle.valueOf(s);
		}catch(Exception e) {
			return def;
		}
	}
	
	public static Particle getParticleSafe(String s) {
		return getParticleSafe(s, null);
	}
}
