package nl.thewgbbroz.butils_v2;

import nl.thewgbbroz.butils_v2.services.WGBService;

public class BUtilsPlugin extends WGBPlugin {
	private static BUtilsPlugin instance;
	
	@Override
	public void wgb_onEnable() {
		BUtilsPlugin.instance = this;
	}

	@Override
	public void wgb_onDisable() {
	}
	
	@SuppressWarnings("unchecked")
	public <E extends WGBService> E loadPublicService(E service) {
		WGBService loadedService = serviceManager.loadService(service.getClass());
		if(loadedService != null) {
			try {
				return (E) loadedService;
			}catch(ClassCastException e) {}
		}
		
		return serviceManager.loadService(service);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends WGBService> E loadPublicService(Class<? extends E> serviceClass) {
		WGBService loadedService = serviceManager.loadService(serviceClass);
		if(loadedService != null) {
			try {
				return (E) loadedService;
			}catch(ClassCastException e) {}
		}
		
		return serviceManager.loadService(serviceClass);
	}
	
	public static BUtilsPlugin instance() {
		return instance;
	}
}
