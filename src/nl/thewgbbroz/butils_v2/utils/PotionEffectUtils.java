package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.potion.PotionEffect;

public class PotionEffectUtils {
	private PotionEffectUtils() {
	}
	
	public static PotionEffect clonePotionEffect(PotionEffect pe) {
		return new PotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier(), pe.isAmbient(), pe.hasParticles(), pe.hasIcon());
	}
}
