package nl.thewgbbroz.butils_v2.playerattributes.attributes;

import org.bukkit.configuration.ConfigurationSection;

import nl.thewgbbroz.butils_v2.playerattributes.PlayerAttributes;

public abstract class PlayerAttribute {
	protected final PlayerAttributes parent;
	
	public PlayerAttribute(PlayerAttributes parent) {
		this.parent = parent;
	}
	
	public abstract void serialize(ConfigurationSection config);
	public abstract void deserialize(ConfigurationSection config);
	
	public void save() {
		parent.save();
	}
}
