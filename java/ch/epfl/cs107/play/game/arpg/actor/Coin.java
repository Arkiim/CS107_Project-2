package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Coin extends CollectableAreaEntity {

	public final static int VALUE = 50;
	private final static int ANIMATION_DURATION = 4;
	private final static int SPRITE_SIZE = 16;

	private Sprite[] sprites;
	private Animation animation;

	public Coin(Area area, DiscreteCoordinates position) {
		super(area, Orientation.UP, position);
		sprites = createSprites();
		animation = new Animation(ANIMATION_DURATION, sprites);

	}

	private Sprite[] createSprites() {
		Sprite[] sprites = new RPGSprite[4];
		for (int i=0 ; i < sprites.length ; ++i) {
			sprites[i] = new RPGSprite("zelda/coin", 1, 1, this, new RegionOfInterest(i*SPRITE_SIZE, 0, SPRITE_SIZE,SPRITE_SIZE));
		}
		return sprites;
	}

	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates());
	}

	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

}
