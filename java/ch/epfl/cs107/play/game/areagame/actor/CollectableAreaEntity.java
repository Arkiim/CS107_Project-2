package ch.epfl.cs107.play.game.areagame.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public abstract class CollectableAreaEntity extends MovableAreaEntity {

	/**
	 * Constructor of CollectableAreaEntity
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public CollectableAreaEntity(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean takeCellSpace() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void collect() {
		getOwnerArea().unregisterActor(this);
	}

	public void setCollectablePosition(float x, float y) {
		setCurrentPosition(new Vector(x,y));
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		v.interactWith(this);

	}

}
