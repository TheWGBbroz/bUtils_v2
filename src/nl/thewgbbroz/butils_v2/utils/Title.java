package nl.thewgbbroz.butils_v2.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Title {
	public static final int DEFAULT_FADEIN	= 10;
	public static final int DEFAULT_STAY	= 70;
	public static final int DEFAULT_FADEOUT	= 20;
	
	private String title;
	private String subtitle;
	
	private int fadeIn;
	private int stay;
	private int fadeOut;
	
	/**
	 * @param title The title text
	 * @param subtitle The subtitle text
	 * @param fadeIn The amount of ticks to fade in
	 * @param stay The amount of ticks to stat
	 * @param fadeOut The amount of ticks to fade out
	 */
	public Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		this.title = title;
		this.subtitle = subtitle;
		
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}
	
	/**
	 * @param title The title text
	 * @param subtitle The subtitle text
	 * 
	 * Uses the default parameters for the fade in, stay and fade out time.
	 */
	public Title(String title, String subtitle) {
		this(title, subtitle, DEFAULT_FADEIN, DEFAULT_STAY, DEFAULT_FADEOUT);
	}
	
	/**
	 * @param conf The configuration section to get the parameters from.
	 * 
	 * Configuration keys:
	 * - title
	 * - subtitle
	 * - fadein
	 * - stay
	 * - fadeout
	 */
	public Title(ConfigurationSection conf) {
		this.title = ChatColor.translateAlternateColorCodes('&', conf.getString("title", ""));
		this.subtitle = ChatColor.translateAlternateColorCodes('&', conf.getString("subtitle", ""));
		
		this.fadeIn = conf.getInt("fadein", DEFAULT_FADEIN);
		this.stay = conf.getInt("stay", DEFAULT_STAY);
		this.fadeOut = conf.getInt("fadeout", DEFAULT_FADEOUT);
	}
	
	/**
	 * @param player The player to send the title to
	 */
	public void send(Player player) {
		player.sendTitle(
				title,
				subtitle,
				fadeIn,
				stay,
				fadeOut
		);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSubtitle() {
		return subtitle;
	}
	
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public int getFadeIn() {
		return fadeIn;
	}
	
	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
	}
	
	public int getStay() {
		return stay;
	}
	
	public void setStay(int stay) {
		this.stay = stay;
	}
	
	public int getFadeOut() {
		return fadeOut;
	}
	
	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
	}
}
