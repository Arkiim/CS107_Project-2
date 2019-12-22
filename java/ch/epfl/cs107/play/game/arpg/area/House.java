package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.ARPGItem;
import ch.epfl.cs107.play.game.arpg.actor.Box;
import ch.epfl.cs107.play.game.arpg.actor.CollectableItem;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class House extends ARPGArea {

	final DiscreteCoordinates DEFAULT_BOX_CONTENT_COORDINATES = new DiscreteCoordinates(0, 0);

	@Override
	protected void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));

		//Register Door
		registerActor(new Door("zelda/Ferme", new DiscreteCoordinates(6,10), Logic.TRUE,
				this, Orientation.UP, new DiscreteCoordinates(3,0), new DiscreteCoordinates(4,0)));

		//Register Box
		Box box = new Box(this, new DiscreteCoordinates(6, 6), new CollectableAreaEntity[]
				{new CollectableItem(this, DEFAULT_BOX_CONTENT_COORDINATES, ARPGItem.BOW, 1),
						new CollectableItem(this, DEFAULT_BOX_CONTENT_COORDINATES,  ARPGItem.ARROW, 45),
						new CollectableItem(this, DEFAULT_BOX_CONTENT_COORDINATES, ARPGItem.BOMB, 5)});
		box.setStateOn();
		registerActor(box);
	}

	@Override
	public String getTitle() {
		return "PetalburgTimmy";
	}
}