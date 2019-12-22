package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.damage.DmgInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Heart extends CollectableAreaEntity implements DmgInteractionVisitor, Interactor {

	private final static float HEAL = -1.f;
	private final static int ANIMATION_DURATION = 4;
	private final static int SPRITE_SIZE = 16;

	private Sprite[] sprites;
	private Animation animation;
	private HeartInteractionHandler handler;


	private boolean dmgDealt;

	public Heart(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprites = createSprites();
		animation = new Animation(ANIMATION_DURATION, sprites);
		handler = new HeartInteractionHandler();
		dmgDealt = false;
	}

	private Sprite[] createSprites() {
		Sprite[] sprites = new RPGSprite[4];
		for (int i=0 ; i < sprites.length ; ++i) {
			sprites[i] = new RPGSprite("zelda/heart", 1, 1, this, new RegionOfInterest(i*SPRITE_SIZE, 0, SPRITE_SIZE,SPRITE_SIZE));
		}
		return sprites;
	}

	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		// TODO Auto-generated method stub
		return Collections.singletonList
				(getCurrentMainCellCoordinates());
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		animation.draw(canvas);
	}

	//@Override
	/*public void dealDmg(DamageReceiver victim) {
		if(!dmgDealt) {
			victim.receiveDmg(this);
			dmgDealt = true;
		}
	}*/

	@Override
	public float getDmg() {
		// TODO Auto-generated method stub
		return HEAL;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return getCurrentCells();
	}

	@Override
	public boolean wantsCellInteraction() {
		return true;
	}

	@Override
	public boolean wantsViewInteraction() {
		return false;
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}
	/**   Private Inner Class BombInteractionHandler	**/

	private class HeartInteractionHandler implements ARPGInteractionVisitor {

		@Override
		public void interactWith(ARPGPlayer player) {
			if(!dmgDealt) {
				player.receiveDmg(Heart.this);
				dmgDealt = true;
			}
		}

	}

}
