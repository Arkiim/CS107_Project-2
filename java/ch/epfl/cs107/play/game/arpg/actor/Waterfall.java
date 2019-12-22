package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Waterfall extends AreaEntity{
	private final static int ANIMATION_DURATION = 5;

	private Sprite[][] sprites;
	private Animation animation;

	/**
	 * Constructor of AreaEntity Waterfall
	 * @param owner (Area)
	 * @param position (DiscreteCoordinates)
	 */
	public Waterfall(Area owner, DiscreteCoordinates position) {
		super(owner, Orientation.DOWN, position);

		sprites = createSprites();
		for(Sprite[] i : sprites) {
			for(Sprite j : i) {
				j.setDepth(-1999);
			}
		}
		
		animation = createAnimation(sprites, 0);
		
	}
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
	
	/**
	 * Create Sprites for Waterfall.
	 * See RPGSprite.extractSprites in the method for more infos
	 * @return array of 4 sprite
	 */
	private Sprite[][] createSprites() {
		return RPGSprite.extractSprites("zelda/waterfall", 3, 4, 5, this, 64, 64, new Vector(2,-2.2f), Orientation.values());
	}
	
	/**
	 * Create Animations for Waterfall.
	 * @param sprites (Sprite[][]) : multiple dimension array
	 * @param index   (int)        : which animation is to be returned
	 * See RPGSprite.createAnimations in the method for more infos
	 * @return The animation of the "index"-th array of the multiple dimension array sprites (corresponding to one movement)
	 */
	private Animation createAnimation(Sprite[][] sprites, int index){
		return RPGSprite.createAnimations(ANIMATION_DURATION, sprites, true)[index];
	}

	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates()) ;
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
	public void acceptInteraction(AreaInteractionVisitor v) {
	}

	@Override
	public boolean takeCellSpace() {
		// TODO Auto-generated method stub
		return false;
	}

}
