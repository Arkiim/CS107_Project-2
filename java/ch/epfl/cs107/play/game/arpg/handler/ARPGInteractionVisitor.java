package ch.epfl.cs107.play.game.arpg.handler;

import ch.epfl.cs107.play.game.arpg.ARPGBehavior.ARPGCell;
import ch.epfl.cs107.play.game.arpg.actor.ARPGMayor;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.ARPGSign;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.Box;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.CastleKey;
import ch.epfl.cs107.play.game.arpg.actor.Coin;
import ch.epfl.cs107.play.game.arpg.actor.CollectableItem;
import ch.epfl.cs107.play.game.arpg.actor.FireSpell;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.Heart;
import ch.epfl.cs107.play.game.arpg.actor.Lever;
import ch.epfl.cs107.play.game.arpg.actor.LogicalObject;
import ch.epfl.cs107.play.game.arpg.actor.Orb;
import ch.epfl.cs107.play.game.arpg.actor.Reader;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.weapon.Arrow;
import ch.epfl.cs107.play.game.arpg.actor.weapon.MagicWaterProjectile;
import ch.epfl.cs107.play.game.arpg.damage.DmgInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.handler.RPGInteractionVisitor;

 public interface ARPGInteractionVisitor extends RPGInteractionVisitor {
	/**
	 * Default interaction between something and an ARPGCell
	 * @param cell
	 */
	default void interactWith(ARPGCell cell) {
		//by default empty
	}

	/**
	 * Default interaction between something and an ARPGPlayer
	 * @param player
	 */
	default void interactWith(ARPGPlayer player) {
		//by default empty
	}

	//interactWith (Door door) Already in RPGInteractionVisitor


	/**
	 * Default interaction between something and Grass
	 * @param grass (Grass)
	 */
	default void interactWith(Grass grass) {
		//by default empty
	}

	/**
	 * Default interaction between something and a Bomb
	 * @param bomb (Bomb)
	 */
	default void interactWith(Bomb bomb) {
		//by default empty
	}

	/**
	 * Default interaction between something and a Heart
	 * @param heart (Heart)
	 */
	default void interactWith(Heart heart) {
		// default empty
	}

	/**
	 * Default interaction between something and a Coin
	 * @param coin (Coin)
	 */
	default void interactWith(Coin coin) {
		//by default empty
	}

	/**
	 * Default interaction between something and a CastleKey
	 * @param key (CastleKey)
	 */
	default  void interactWith(CastleKey key) {
		//by default empty
	}

	/**
	 * Default interaction between something and a Monster
	 * @param monster (Monster)
	 */
	default void interactWith(Monster monster) {
		//by default empty
	}

	/**
	 * Default interaction between a dmgReceiver and dmgDealer( those who implements DmgInteractionVisitor )
	 * @param dealer
	 */
	default void interactWith(DmgInteractionVisitor dealer) {
		//by default empty
	}

	/**
	 * Default interaction between something and the CastleDoor
	 * @param castleDoor
	 */
	default void interactWith(CastleDoor castleDoor) {
		//by default empty
	}

	/**
	 * Default interaction between something and an Arrow
	 * @param arrow
	 */
	default void interactWith(Arrow arrow) {
		//by default empty
	}

	/**
	 * Default interaction between something and an MagicWaterProjectile
	 * @param magicWaterProjectile
	 */
	default void interactWith(MagicWaterProjectile magicWaterProjectile) {
		//by default empty
	}

	/**
	 * Default interaction between something and an Orb
	 * @param orb
	 */
	default void interactWith(Orb orb) {
		//by default empty
	}

	/**
	 * Default interaction between something and a Lever
	 * @param lever
	 */
	default void interactWith(Lever lever) {
		//by default empty
	}

	/**
	 * Default interaction between something and a FireSpell
	 * @param fireSpell
	 */
	default void interactWith(FireSpell fireSpell) {
		//by default empty
	}

	/**
	 * Default interaction between something and the ARPGMayor
	 * @param mayor (ARPGMayor)
	 */
	default void interactWith(ARPGMayor mayor) {
		//by default empty
	}

	/**
	 * Default interaction between something and a LogicalObject
	 * @param logicalObject
	 */
	default void interactWith(LogicalObject logicalObject) {
		//by default empty
	}

	/**
	 * Default interaction between something and a Reader
	 * @param reader
	 */
	default void interactWith(Reader reader) {
		//by default empty
	}

	/**
	 * Default interaction between something and a Box
	 * @param box (Box)
	 */
	default void interactWith(Box box) {
		//by default empty
	}

	/**
	 * Default interaction between something and a CollectableItem
	 * @param item (CollectableItem)
	 */
	default void interactWith(CollectableItem item) {
		//by default empty
	}

	 /**
	  * Default interaction between something and an ARPGSign
	  * @param sign (ARPGSign), not null
	  */
	default void interactWith(ARPGSign sign) {
		//by default empty
	}

}