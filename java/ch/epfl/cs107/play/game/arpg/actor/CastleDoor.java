package ch.epfl.cs107.play.game.arpg.actor;

import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;

public class CastleDoor extends Door{

	private Sprite spriteOpen;
	private Sprite spriteClosed;
	private boolean isOpen; // state (open or closed)$¨
	private boolean canPass;
	/**
	 * Complementary CastleDoor constructor
	 * @param destination        (String): Name of the destination area, not null
	 * @param otherSideCoordinates (DiscreteCoordinate):Coordinates of the other side, not null
	 * @param signal (Logic): LogicGate signal opening the door, may be null
	 * @param area        (Area): Owner area, not null
	 * @param position    (DiscreteCoordinate): Initial position of the entity, not null
	 * @param orientation (Orientation): Initial orientation of the entity, not null
	 * @param otherCells (DiscreteCoordinates): Other cells occupied by the AreaEntity if any (limited to 1). Assume absolute coordinates, not null
	 */
	public CastleDoor(String destination, DiscreteCoordinates otherSideCoordinates, Logic signal, Area area,
    		Orientation orientation, DiscreteCoordinates position, DiscreteCoordinates... otherCells) {
		super(destination, otherSideCoordinates, signal, area, orientation, position, otherCells);
        
		spriteClosed = RPGSprite.extractSprites("zelda/castleDoor.close", 1, 2, 2, this, 32, 32, Orientation.values())[0][0];
		spriteOpen = RPGSprite.extractSprites("zelda/castleDoor.open", 1, 2, 2, this, 32, 32, Orientation.values())[0][0];
		isOpen = false;
		canPass = false;
	}
	
	
	public void update(float deltaTime) {
		if(isOpen) {
			canPass = true;
		}
	}
	/**
	 * Opens CastleDoor;
	 */
	protected void setIsOpen() {
		isOpen = true;
		this.setSignal(Logic.TRUE);
	}
	
	/**
	 * Closes CastleDoor;
	 */
	protected void setIsClosed() {
		isOpen = false;
		canPass = false;
		this.setSignal(Logic.FALSE);
	}

	//Depends on a variable so that the players doesn't "glitch" through the doors if they are closed
	//(He should be able to pass through the doors only if they're open)
	@Override
	public boolean takeCellSpace() {
		return !canPass;
	}

	//Assigned to a variable so that the player doesn't glitch through the door if it's not open
	@Override
	public boolean isCellInteractable() {
		return canPass;
	}
	
	//Once the variable isOpen has been set to true the door doesn't have to have view interaction because the player has already opened the door if he has the key
		//so no further interaction are needed. This prevent that
		//the player could pass through the door even if he's actually not "on" the door
	@Override
	public boolean isViewInteractable() {
		return !isOpen;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}


	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return super.getCurrentCells() ;
	}

	@Override
	public void draw(Canvas canvas) {
		if(isOpen) spriteOpen.draw(canvas);

		else spriteClosed.draw(canvas);
	}

}