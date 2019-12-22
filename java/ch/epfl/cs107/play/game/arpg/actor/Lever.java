package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Lever extends LogicalObject{
	private final Sprite[] sprites; 
	private Sprite currentSprite;
	private final static String[] nameTbl = {"addedSprites/LeverDown","addedSprites/LeverUp"};
	private final static String[] nameTbl1 = {"addedSprites/LeverDown1","addedSprites/LeverUp1"};
	/**
	 * Constructor of AreaEntity Lever
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public Lever(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);

		sprites = new Sprite[nameTbl.length];

		if(area.getTitle().equals("zelda/GrotteDonjon")) {
			for(int i = 0 ; i < nameTbl.length ; i++) {
				sprites[i] = new RPGSprite( nameTbl[i], 1, 1, this);
			}

		} else {
			for(int i = 0 ; i < nameTbl.length ; i++) {
				sprites[i] = new RPGSprite( nameTbl1[i], 1, 1, this);
			}
		}
		
		currentSprite = sprites[0];
	}

	@Override
	public void update(float deltaTime) {
		if(getSignalState()) {
			currentSprite = sprites[1];
		} else {
			currentSprite = sprites[0];
		}

		super.update(deltaTime);

		if(getSignalState()) {
			setStateOf();
		}
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
		return true;
	}

	public void draw(Canvas canvas) {
		currentSprite.draw(canvas);
	}

}
