package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.Lever;
import ch.epfl.cs107.play.game.arpg.actor.Teleporter;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Temple extends ARPGArea {

	private final Lever lever = new Lever(this, Orientation.UP, new DiscreteCoordinates(5, 3));

	private final Teleporter teleporter = new Teleporter("zelda/GrotteDonjon", new DiscreteCoordinates(8,3),
			this, Orientation.UP, new DiscreteCoordinates(4,3));

	@Override
	public void update(float deltaTime) {

		if(lever.getSignalState()) {
			teleporter.setSignal(Logic.TRUE);
		} else {
			teleporter.setSignal(Logic.FALSE);
		}

		super.update(deltaTime);
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
		registerActor(new Foreground(this));

		//Register CastleDoor
		registerActor(new Door("zelda/RouteTemple", new DiscreteCoordinates(5,4), Logic.TRUE,
				this, Orientation.UP, new DiscreteCoordinates(4,0)));

		registerActor(lever);

		registerActor(teleporter);

	}

	@Override
	public String getTitle() {
		return "zelda/Temple";
	}

}
