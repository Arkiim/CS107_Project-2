package ch.epfl.cs107.play.game.arpg.damage;

/**
 * By default type of all actor that have an hp and that can receive damage;
 */
public interface DamageReceiver {

	/**
	 * @return The amount of damage a DamageReceiver can take before dying
	 */
	float getHp();
	
	/**
	 * Interaction between a damageReceiver and a damageDealer
	 * @param dealer
	 */
	void receiveDmg(DmgInteractionVisitor dealer);
}
