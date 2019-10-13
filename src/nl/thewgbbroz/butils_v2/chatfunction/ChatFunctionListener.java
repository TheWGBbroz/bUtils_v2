package nl.thewgbbroz.butils_v2.chatfunction;

import org.bukkit.entity.Player;

public interface ChatFunctionListener {
	/**
	 * Listener method.
	 * 
	 * @return If this returns TRUE, the chat function will be stopped/exited.
	 */
	public boolean onMessage(Player p, String msg);
}
