package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Chateau extends ARPGArea {


	@Override
	protected void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));
		
		//Register CastleDoor
		registerActor(new CastleDoor("zelda/RouteChateau", new DiscreteCoordinates(9,12),Logic.FALSE,
				this, Orientation.UP, new DiscreteCoordinates(7,0), new DiscreteCoordinates(8,0)));
	}
	
	
	@Override
	public String getTitle() {
		return "zelda/Chateau";
	}

	
}