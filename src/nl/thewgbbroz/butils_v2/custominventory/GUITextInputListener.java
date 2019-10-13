package nl.thewgbbroz.butils_v2.custominventory;

public interface GUITextInputListener {
	/**
	 * When an anvil inventory gets closed.
	 */
	public void onClose();
	
	/**
	 * When a player clicks in an anvil inventory.
	 */
	public void onClick(String text, boolean clickedOnOutput, int anvilSlot);
}
