package nl.thewgbbroz.butils_v2.playerattributes.attributes;

import org.bukkit.configuration.ConfigurationSection;

import nl.thewgbbroz.butils_v2.playerattributes.PlayerAttributes;

public abstract class IntegerAttribute extends PlayerAttribute {
	protected int value;
	
	public IntegerAttribute(PlayerAttributes parent) {
		super(parent);
	}
	
	public int get() {
		return value;
	}
	
	public void set(int value) {
		if(this.value != value) {
			this.value = value;
			save();
		}
	}
	
	public void add(int add) {
		set(get() + add);
	}
	
	public void remove(int remove) {
		set(get() - remove);
	}
	
	@Override
	public void serialize(ConfigurationSection config) {
		config.set("v", value);
	}
	
	@Override
	public void deserialize(ConfigurationSection config) {
		this.value = config.getInt("v");
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + value + "]";
	}
}
