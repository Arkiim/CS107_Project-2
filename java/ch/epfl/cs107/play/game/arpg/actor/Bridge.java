package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Bridge extends AreaEntity {
	
	private Sprite sprite;
	public boolean isBridgeOn;
	private boolean construct;
	
	/**
	 * Constructor for AreaEntity Bridge
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public Bridge(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		sprite = new RPGSprite("zelda/bridge", 4, 2, this, new RegionOfInterest(0,0, 64, 32), new Vector(0,0), 0, -1000) ;
		sprite.setAnchor(new Vector(-1, 0));
		
		construct = false;
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates()) ;
	}
	
	@Override
	public void update(float deltaTime) {
		
		if(construct) {
			sprite.setAlpha(1);
		} else {
			sprite.setAlpha(0);
		}
		
		super.update(deltaTime);
	}

	@Override
	public boolean takeCellSpace() {
		return !construct;
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
	public void acceptInteraction(AreaInteractionVisitor v) {}

	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}

	public boolean isConstruct() {
		return construct;
	}

	public void setConstruct(boolean construct) {
		this.construct = construct;
	}

}
