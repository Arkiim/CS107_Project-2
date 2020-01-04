
package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.ARPGMayor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Village extends ARPGArea {

	/** To what area leads the door ? */
	final String[] destinations = { "zelda/Ferme", "zelda/Ferme", "zelda/Route" } ;

	/** Where do you arrive, once you  passed the door */
	final DiscreteCoordinates[] arrivCoords = { new DiscreteCoordinates(4,1),
			new DiscreteCoordinates(14, 1), new DiscreteCoordinates(9,1) };


	/** Where is the door in the area (usually 2 coordinates : mainCords and otherCords) */
	final DiscreteCoordinates[] mainCoords = { new DiscreteCoordinates(4,19),
			new DiscreteCoordinates(13,19), new DiscreteCoordinates(29,19) } ;

	final DiscreteCoordinates[] otherCoords = { new DiscreteCoordinates(5,19),
			new DiscreteCoordinates(14,19), new DiscreteCoordinates(30,19), new DiscreteCoordinates(15,19) } ;


	/** Orientation once you've entered the new area */
	final Orientation[] orientations = {Orientation.UP};

	@Override
	public void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));

		//Register Doors
		for(int i = 0; i < 3 ; i++) {
			if(i == 1) {
				registerActor( new Door(destinations[1], arrivCoords[1], Logic.TRUE,
						this, orientations[0], mainCoords[1], otherCoords[1], otherCoords[3]));
			} else {
				registerActor(new Door(destinations[i], arrivCoords[i], Logic.TRUE,
						this, orientations[0], mainCoords[i], otherCoords[i]));
			}	
		}

		//Register ARPGMayor
		registerActor(new ARPGMayor(this, Orientation.DOWN, new DiscreteCoordinates(17, 9)));

	}

	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
	}

	@Override
	public String getTitle() {
		return "zelda/Village";
	}

}