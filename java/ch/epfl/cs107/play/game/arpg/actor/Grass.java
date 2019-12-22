package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Grass extends AreaEntity{
	private Sprite defaultSprite;
	private Sprite[] sprites;
	private boolean isCut;
	private Animation animation;
	private boolean isBeingCut;
	private final static int ANIMATION_DURATION = 4;
	private final static float  PROBABILITY_TO_DROP_ITEM = 0.3f;
	private final static float  PROBABILITY_TO_DROP_HEART = 0.2f;


	/**
	 * Constructor of Grass
	 * @param area (Area) : area of the grass
	 * @param orientation (Orientation)
	 * @param position (DiscreteCoordinates)
	 */
	public Grass(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);

		defaultSprite = new RPGSprite("zelda/grass", 1, 1, this, new RegionOfInterest(0,0,16,16));

		sprites = createSprites("zelda/grass.sliced", 0, 4, 32, 32, 2, 2);
		setSpritesAnchor(sprites, -0.3f, -0.4f);
		animation = new Animation(ANIMATION_DURATION, sprites, false);

		isBeingCut = false;
		isCut = false;
	}

	@Override
	public void update(float deltaTime) {

		if (isBeingCut) animation.update(deltaTime);

		if(animation.isCompleted()) {
			isCut = true;
			this.getOwnerArea().unregisterActor(this);
			dropItem();
		}

	}

	/**
	 * Create Animations for Grass.
	 * See RPGSprite.createAnimations in the method for more infos
	 * @param sprites (Sprites[][]) : arrays of sprites to be used for the animation
	 * @param index (int) : index of the array to be returned
	 * @return The animation of the "index"-th array of sprites
	 */
	public Animation createAnimation(Sprite[][] sprites, int index ){
		return RPGSprite.createAnimations(ANIMATION_DURATION, sprites, false)[index];
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates()) ;
	}

	/**
	 * Method that is called on the interactWith
	 */
	public void setIsCut() {
		isBeingCut = true; //verified
	}

	/**
	 * randomly drops an item (coin or heart) or not
	 */
	public void dropItem() {
		float randomFloat = RandomGenerator.getInstance().nextFloat();
		if (randomFloat < PROBABILITY_TO_DROP_ITEM) {
			randomFloat = RandomGenerator.getInstance().nextFloat();
			if (randomFloat < PROBABILITY_TO_DROP_HEART) {
				getOwnerArea().registerActor(new Heart(getOwnerArea(), Orientation.UP, getCurrentCells().get(0)));
			} else {
				getOwnerArea().registerActor(new Coin(getOwnerArea(), getCurrentCells().get(0)));
			}
		}
	}

	@Override
	public boolean takeCellSpace() {
		return !isCut;
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return true;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {

		if(!isCut) {
			if(isBeingCut) {
				animation.draw(canvas);
			} else {
				defaultSprite.draw(canvas);

			}


		}
	}

}