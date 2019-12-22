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
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class CastleKey extends CollectableAreaEntity {
	private static int ANIMATION_DURATION = 3;
	private Sprite[] sprites;
	private Animation animation;

	public CastleKey(Area area, DiscreteCoordinates coord) {
		super(area, Orientation.UP, coord);
		sprites = new Sprite[8];
		for (int i=0 ; i < sprites.length/2 ; ++i) {
			sprites[i] = new RPGSprite("zelda/key", 1, 1, this, new RegionOfInterest(0,0,16,16), new Vector(0,0), (i+1)*0.25f, 0);
			sprites[sprites.length - 1 - i] = sprites[i];
		}
		animation = new Animation(ANIMATION_DURATION, sprites);
	}
	
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}
	
	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor)v).interactWith(this);
	}
	
	public void addKey(ARPGInventory inventory) {
		inventory.add(ARPGItem.CASTLEKEY,1);
	}
	
	@Override
	public void draw(Canvas canvas) {
		
		animation.draw(canvas);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates());
	}

}
