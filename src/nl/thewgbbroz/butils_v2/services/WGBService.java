package nl.thewgbbroz.butils_v2.services;

/**
 * Nothing should be happening in the constructor, except setting some values which will be passed through the constructor!
 */
public abstract class WGBService {
	/**
	 * The load method.
	 */
	public abstract void load();
	
	/**
	 * The unload method.
	 */
	public abstract void unload();
	
	/**
	 * The reload method.
	 * The reload method gets called for the first time after the load() method gets called.
	 */
	public void reload() {}
}
