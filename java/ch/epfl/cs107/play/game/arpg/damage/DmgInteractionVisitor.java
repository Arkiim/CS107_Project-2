  
package ch.epfl.cs107.play.game.arpg.damage;

public interface DmgInteractionVisitor {

	/**
	 * Interact with victim to inflict him his "amount of damage"
	 * @param victim
	 */
	/*void dealDmg(DamageReceiver victim);*/
	
	/**
	 * get the amount of damage a damgeDealer can deal to a DamageReceiver
	 * @return
	 */
	float getDmg();
	
}
