package ch.epfl.cs107.play.game.arpg.actor;

public interface FlyableEntity {

	default boolean canFly() {
		return true;
	}
	
}
