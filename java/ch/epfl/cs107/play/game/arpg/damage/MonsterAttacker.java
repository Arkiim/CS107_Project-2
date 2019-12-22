package ch.epfl.cs107.play.game.arpg.damage;

import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;

public interface MonsterAttacker extends DmgInteractionVisitor {

	/**
	 * used to determine if a monster should be dealt damage or not
	 * @return getAttackType() (Vulnerability)
 	 */
	default Vulnerability getAttackType() {
		return Vulnerability.PHYSICAL;
	}
}
