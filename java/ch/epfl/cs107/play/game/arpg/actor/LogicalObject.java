package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public abstract class LogicalObject extends AreaEntity{

	private Logic signalState;

	/**
	 * Constructor of abstract class LogicalObject
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public LogicalObject(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);

		signalState = Logic.FALSE;
	}
	
	/**
	 * Activate the LogicalObject (by setting signalState to Logic.TRUE)
	 */
	public void setStateOn() {
		signalState = Logic.TRUE;
	}

	/**
	 * Deactivate the LogicalObject (by setting signalState to Logic.FALSE)
	 */
	public void setStateOf() {
		signalState = Logic.FALSE;
	}

	public boolean getSignalState() {
		return signalState.getIntensity() == 1.0;
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates()) ;
	}

	@Override
	public boolean takeCellSpace() {
		return true;
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

}
