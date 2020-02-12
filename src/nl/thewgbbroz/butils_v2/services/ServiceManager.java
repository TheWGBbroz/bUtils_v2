package nl.thewgbbroz.butils_v2.services;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.thewgbbroz.butils_v2.WGBPlugin;

public class ServiceManager {
	private final WGBPlugin plugin;
	
	/**
	 * Use a HashMap to get lookups to constant time, O(1).
	 */
	private Map<Class<? extends WGBService>, WGBService> loadedServices = new HashMap<>();
	
	public ServiceManager(WGBPlugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Unloads all the services
	 */
	public void unload() {
		for(WGBService service : loadedServices.values()) {
			try {
				service.unload();
			}catch(Exception e) {
				plugin.getLogger().warning("Could not properly unload service " + service.getClass().getSimpleName() + ": " + e.toString());
				e.printStackTrace();
			}
		}
		
		loadedServices.clear();
	}
	
	/**
	 * Loads a service.
	 */
	public <E extends WGBService> E loadService(E service) {
		if(!plugin.isEnabled()) {
			plugin.getLogger().warning("Not loading service " + service.getClass() + " because plugin is disabled.");
			return null;
		}
		
		if(service == null)
			throw new NullPointerException("Service is null!");
		
		if(isServiceLoaded(service.getClass()))
			throw new IllegalArgumentException("Can't load a service if it's already loaded!");
		
		try {
			loadedServices.put(service.getClass(), service);
			
			service.load();
			service.reload();
			
			return service;
		}catch(Exception e) {
			plugin.getLogger().severe("Could not initialize service " + service.getClass().getSimpleName() + ": " + e.toString());
			e.printStackTrace();
			
			plugin.disable();
		}
		
		return null;
	}
	
	/**
	 * Loads a service. The constructor class with the plugin as argument will be called.
	 * If this fails, the service cannot be loaded with this method and {@link ServiceManager#loadService(WGBService)} should be called instead.
	 */
	@SuppressWarnings("unchecked")
	public <E extends WGBService> E loadService(Class<? extends E> serviceClass) {
		// Try to initialize service with plugin as only parameter
		
		Constructor<? extends WGBService> serviceConstructor = null;
		
		if(serviceConstructor == null) {
			try {
				serviceConstructor = serviceClass.getConstructor(WGBPlugin.class);
			}catch(Exception e) {}
		}
		
		if(serviceConstructor == null) {
			try {
				serviceConstructor = serviceClass.getConstructor(plugin.getClass());
			}catch(Exception e) {}
		}
		
		if(serviceConstructor == null)
			throw new IllegalArgumentException("Service " + serviceClass.getName() + " needs special constructor arguments!");
		
		try {
			WGBService service = serviceConstructor.newInstance(plugin);
			
			return (E) loadService(service);
		} catch(Exception e) {
			plugin.getLogger().warning("Could not call constructor of service " + serviceClass.getSimpleName() + ": " + e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Unloads a service.
	 */
	public void unloadService(Class<? extends WGBService> serviceClass) {
		WGBService service = getService(serviceClass);
		if(service == null)
			throw new IllegalArgumentException("Can't unload a service if it was never loaded!");
		
		try {
			service.unload();
		}catch(Exception e) {
			plugin.getLogger().warning("Could not properly unload service " + service.getClass().getSimpleName() + ": " + e.toString());
			e.printStackTrace();
		}
		
		loadedServices.remove(service.getClass());
	}
	
	/**
	 * Returns the loaded service of a certain class.
	 */
	@SuppressWarnings("unchecked")
	public <E extends WGBService> E getService(Class<? extends E> serviceClass) {
		return (E) loadedServices.get(serviceClass);
	}
	
	/**
	 * @return Whether or not this service is loaded.
	 */
	public boolean isServiceLoaded(Class<? extends WGBService> serviceClass) {
		return getService(serviceClass) != null;
	}
	
	/**
	 * @return A non-modifiable list of all loaded services.
	 */
	public Collection<WGBService> getLoadedServices() {
		return Collections.unmodifiableCollection(loadedServices.values());
	}
	
	/**
	 * Reloads all loaded services.
	 */
	public void reloadAllServices() {
		loadedServices.values().forEach(service -> {
			try {
				service.reload();
			}catch(Exception e) {
				plugin.getLogger().warning("Could not reload service " + service.getClass().getSimpleName() + ": " + e.toString());
				e.printStackTrace();
			}
		});
	}
}
