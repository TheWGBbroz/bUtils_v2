package nl.thewgbbroz.butils_v2.utils;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unchecked")
public class TemporaryListener<E extends Event> implements Listener, EventExecutor {
	private final Class<E> eventClass;
	private final Function<E, Boolean> consumer;
	
	/**
	 * @param plugin The plugin instance
	 * @param consumer A function which takes the Event, and should return TRUE if the listener should unregister itself
	 */
	public TemporaryListener(Class<E> eventClass, Function<E, Boolean> consumer) {
		this.eventClass = eventClass;
		this.consumer = consumer;
	}

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		boolean unregister = consumer.apply((E) event);
		if(unregister) {
			unregister();
		}
	}
	
	/**
	 * @param plugin The plugin under which to register the listener
	 * @param eventPriority The priority with which to register the event
	 */
	public void register(JavaPlugin plugin, EventPriority eventPriority) {
		Bukkit.getPluginManager().registerEvent(eventClass, this, eventPriority, this, plugin);
	}
	
	/**
	 * @param plugin The plugin under which to register the listener
	 * 
	 * This will register the event with the {@link EventPriority#NORMAL} event priority.
	 */
	public void register(JavaPlugin plugin) {
		register(plugin, EventPriority.NORMAL);
	}
	
	/**
	 * This will unregister the listener
	 */
	public void unregister() {
		HandlerList.unregisterAll(this);
	}
}
