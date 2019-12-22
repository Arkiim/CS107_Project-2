package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.*;
import ch.epfl.cs107.play.game.arpg.actor.monster.Zombie;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Ferme extends ARPGArea{

	/** To what Area leads the door ? */
	final String[] destinations = { "zelda/Route", "zelda/Village",
			"zelda/Village", "PetalburgTimmy" } ;

	/** Where do you arrive, once you  passed the door */
	final DiscreteCoordinates[] arrivCoords = { new DiscreteCoordinates(1,15), new DiscreteCoordinates(4, 18),
			new DiscreteCoordinates(14,18), new DiscreteCoordinates(4,1) };


	/** Where is the door in the area (usually 2 coordinates : mainCords and otherCords) */
	final DiscreteCoordinates[] mainCoords = { new DiscreteCoordinates(19,15), new DiscreteCoordinates(4,0),
			new DiscreteCoordinates(13,0), new DiscreteCoordinates(6,11) } ;

	final DiscreteCoordinates[] otherCoords = { new DiscreteCoordinates(19,16), new DiscreteCoordinates(5,0),
			new DiscreteCoordinates(14,0), new DiscreteCoordinates(14,0) } ;


	/** Orientation once you've entered the new area */
	final Orientation[] orientations = {Orientation.RIGHT, Orientation.DOWN, Orientation.DOWN, Orientation.UP};

	@Override
	public void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));

		//Register Doors
		for(int i = 0 ; i < 4 ; i++) {
			registerActor(new Door(destinations[i], arrivCoords[i], Logic.TRUE, this, orientations[i],
					mainCoords[i], otherCoords[i]));
		}

		//Register zombie of the dead huntsman
		registerActor(new Zombie(this, Orientation.DOWN, new DiscreteCoordinates(6, 10)));

		//Register ARPGSign for the huntsman's house
		registerActor(new ARPGSign("huntsman's_house", this, Orientation.DOWN, new DiscreteCoordinates(5, 10)));

	}


	@Override
	public String getTitle() {
		return "zelda/Ferme";
	}

}