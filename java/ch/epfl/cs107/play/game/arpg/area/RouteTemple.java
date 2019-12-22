package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RouteTemple extends ARPGArea {

	/** To what area leads the door ? */
	final String[] destinations = { "zelda/Route", "zelda/Temple" } ;

	/** Where do you arrive, once you  passed the door */
	final DiscreteCoordinates[] arrivCoords = { new DiscreteCoordinates(18,11),
			new DiscreteCoordinates(4, 1) };


	/** Where is the door in the area (usually 2 coordinates : mainCords and otherCords) */
	final DiscreteCoordinates[] mainCoords = { new DiscreteCoordinates(0,5),
			new DiscreteCoordinates(5,6) } ;

	final DiscreteCoordinates[] otherCoords = { new DiscreteCoordinates(0, 5), 
			new DiscreteCoordinates(5,6) } ;


	/** Orientation once you've entered the new area */
	final Orientation[] orientations = {Orientation.UP, Orientation.DOWN, Orientation.UP} ;

	@Override
	public void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));

		//Register Doors
		for(int i = 0 ; i < 2; i++ ) {
			registerActor( new Door(destinations[i], arrivCoords[i], Logic.TRUE,
					this, orientations[i], mainCoords[i], otherCoords[i]));
		}
	}
	
	
	
	
	@Override
	public String getTitle() {
		return "zelda/RouteTemple";
	}
	
}
