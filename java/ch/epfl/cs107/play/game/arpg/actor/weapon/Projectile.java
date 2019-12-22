package ch.epfl.cs107.play.game.arpg.actor.weapon;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.FlyableEntity;
import ch.epfl.cs107.play.game.arpg.damage.MonsterAttacker;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

abstract public class Projectile extends MovableAreaEntity implements MonsterAttacker, FlyableEntity, Interactor {

	private int range;
	private int speed;

	private int nbCellsReached;
	private boolean hasInteracted;

	/**
	 * Constructor for super-class Projectile
	 * @param area (Area)
	 * @param orientation (Orientation)
	 * @param position (DiscreteCoordinates)
	 */
	public Projectile(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);

		nbCellsReached = 0;
		hasInteracted = false;
	}

	@Override
	public void update(float deltaTime) {

		movement();
		super.update(deltaTime);
	}

	/**
	 * makes the projectile move according to its range and speed
	 */
	private void movement() {
		if(nbCellsReached < range) {

			if(isTargetReached() && isDisplacementOccurs()) {
				++nbCellsReached ; 
			}

			move(speed);

			if (!isDisplacementOccurs()) { 
				stop() ;
			}

		} else {
			stop();

		}
	}

	/**
	 * Stop the projectile by unregister it
	 */
	protected void stop() {
		getOwnerArea().unregisterActor(this);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates()) ;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}

	@Override
	public boolean isCellInteractable() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

	@Override
	public boolean wantsCellInteraction() {
		return !hasInteracted;
	}

	/**Getter
	 * @return The range (int)
	 */
	protected int getRange() {
		return range;
	}

	/**Getter
	 * @return The speed (int)
	 */
	protected int getSpeed() {
		return speed;
	}
	
	protected boolean getHasInteracted() {
		return hasInteracted;
	}

	/**Setter
	 * set hasInteracted
	 * @param hasInteracted (boolean)
	 */
	protected void setHasInteracted(boolean hasInteracted) {
		this.hasInteracted = hasInteracted;
	}

	/**Setter
	 * set the range and test if the range is greater than 0 and smaller than 10 to avoid any typos
	 * @param range (int)
	 */
	protected void setRange(int range) {
		if( range > 0 && range < 10) {
			this.range = range;
		}
	}

	/**Setter
	 * set the speed and test if the range is greater than 0 and smaller than 10 to avoid any typos
	 * @param speed (int)
	 */
	protected void setSpeed(int speed) {
		if( speed > 0 && speed < 10 ) {
			this.speed = speed;
		}
	}

}
