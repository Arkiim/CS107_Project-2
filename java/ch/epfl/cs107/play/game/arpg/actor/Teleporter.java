package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Teleporter extends Door {
	
	private Logic signal;

	/**
	 * Constructor of Teleporter
	 * @param destination (String)
	 * @param otherSideCoordinates
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public Teleporter(String destination, DiscreteCoordinates otherSideCoordinates, Area area,
			Orientation orientation, DiscreteCoordinates position) {
		
		super(destination, otherSideCoordinates, Logic.FALSE, area, orientation, position);
		
		this.signal = Logic.FALSE ;
	}
	
	@Override 
	public void update(float deltaTime) {
		super.update(deltaTime);
	}

	@Override
	public void setSignal(Logic signal) {
		
		this.signal = signal;
		super.setSignal(this.signal);
	}	

}
