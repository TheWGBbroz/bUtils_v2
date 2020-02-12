package nl.thewgbbroz.butils_v2.playerattributes.attributes;

import org.bukkit.configuration.ConfigurationSection;

import nl.thewgbbroz.butils_v2.playerattributes.PlayerAttributes;

public abstract class DoubleAttribute extends PlayerAttribute {
	protected double value;
	
	public DoubleAttribute(PlayerAttributes parent) {
		super(parent);
	}
	
	public double get() {
		return value;
	}
	
	public void set(double value) {
		if(this.value != value) {
			this.value = value;
			save();
		}
	}
	
	public void add(double add) {
		set(get() + add);
	}
	
	public void remove(double remove) {
		set(get() - remove);
	}
	
	@Override
	public void serialize(ConfigurationSection config) {
		config.set("v", value);
	}
	
	@Override
	public void deserialize(ConfigurationSection config) {
		this.value = config.getDouble("v");
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + value + "]";
	}
}
