package nl.thewgbbroz.butils_v2.services;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.thewgbbroz.butils_v2.WGBPlugin;
import nl.thewgbbroz.butils_v2.utils.ArrayUtils;

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
		
		if(serviceConstructor == null) {
			throw new IllegalArgumentException("Service " + serviceClass.getName() + " does not have the required parameters! (" + plugin.getClass().getName() + ")");
		}
		
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
	 * Loads a service. The constructor class with the plugin as argument will be called.
	 * If this fails, the service cannot be loaded with this method and {@link ServiceManager#loadService(WGBService)} should be called instead.
	 */
	@SuppressWarnings("unchecked")
	public <E extends WGBService> E loadService(Class<? extends E> serviceClass, Object... serviceParameters) {
		// Try to initialize service with the plugin and serviceParameters as parameters.
		
		Class<?>[] constructorParameterClasses = new Class<?>[1 + serviceParameters.length];
		for(int i = 0; i < serviceParameters.length; i++) {
			constructorParameterClasses[i + 1] = serviceParameters[i].getClass();
		}
		
		constructorParameterClasses[0] = plugin.getClass();
		
		Constructor<? extends WGBService> serviceConstructor = findConstructor(serviceClass, constructorParameterClasses);
		
		if(serviceConstructor == null) {
			String required = ArrayUtils.concat(constructorParameterClasses, ", ", clazz -> clazz.getName());
			throw new IllegalArgumentException("Service " + serviceClass.getName() + " does not have the required parameters! (" + required + ")");
		}
		
		try {
			Object[] parameters = new Object[1 + serviceParameters.length];
			for(int i = 0; i < serviceParameters.length; i++) {
				parameters[i + 1] = serviceParameters[i];
			}
			
			parameters[0] = plugin;
			
			WGBService service = serviceConstructor.newInstance(parameters);
			
			return (E) loadService(service);
		} catch(Exception e) {
			plugin.getLogger().warning("Could not call constructor of service " + serviceClass.getSimpleName() + ": " + e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> findConstructor(Class<?> clazz, Class<?>[] lookingFor) {
		for(Constructor<?> constructor : clazz.getConstructors()) {
			if(constructor.getParameters().length != lookingFor.length) {
				continue;
			}
			
			boolean isCorrectConstructor = true;
			
			for(int i = 0; i < constructor.getParameters().length; i++) {
				Class<?> parameter = constructor.getParameters()[i].getType();
				Set<Class<?>> lookingForParents = getSuperclassesAndInterfaces(lookingFor[i]);
				
				if(!lookingForParents.contains(parameter)) {
					isCorrectConstructor = false;
					break;
				}
			}
			
			if(isCorrectConstructor) {
				return (Constructor<T>) constructor;
			}
		}
		
		return null;
	}
	
	private static Set<Class<?>> getSuperclassesAndInterfaces(Class<?> clazz) {
		Set<Class<?>> res = new HashSet<>();
		
		for(Class<?> interfaze : clazz.getInterfaces()) {
			res.add(interfaze);
			res.addAll(getSuperclassesAndInterfaces(interfaze));
		}
		
		Class<?> superClazz = clazz.getSuperclass();
		if(superClazz != null) {
			res.add(clazz.getSuperclass());
			res.addAll(getSuperclassesAndInterfaces(clazz.getSuperclass()));
		}
		
		return res;
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
