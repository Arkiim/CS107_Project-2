package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Orb extends LogicalObject{
	private final static int ANIMATION_DURATION = 5;

	private Sprite[][] sprites;
	private Animation animation;

	/**
	 * Constructor of AreaEntity Orb
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public Orb(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		
		sprites = RPGSprite.extractSprites("zelda/orb", 6, 1, 1, this, 32, 32, Orientation.values());
		animation = RPGSprite.createAnimations(ANIMATION_DURATION, sprites)[2];

	}
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
		super.update(deltaTime);
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}
	
	@Override
	public boolean isViewInteractable() {
		return false;
	}

	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}
	
}
