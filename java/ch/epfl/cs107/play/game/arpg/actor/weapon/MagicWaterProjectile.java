package ch.epfl.cs107.play.game.arpg.actor.weapon;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.FireSpell;
import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class MagicWaterProjectile extends Projectile{

	public final static int RANGE = 4;
	/** Inverse of speed => The higher the value, the slowest the arrow will go **/
	public final static int SPEED = 2;

	private static final float DMG = 2f;

	private MagicWaterProjectileInteractionHandler handler;

	private static Vulnerability ATTACK_TYPE = Vulnerability.MAGICAL;

	private Sprite[][] sprites;
	
	private Animation animation;

	/**
	 * Constructor for MagicWaterProjectile
	 * @param area (Area)
	 * @param orientation (Orientation)
	 * @param position (DiscreteCoordinates)
	 */
	public MagicWaterProjectile(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);

		handler = new MagicWaterProjectileInteractionHandler();

		this.setRange(RANGE);
		this.setSpeed(SPEED);

		sprites = RPGSprite.extractSprites("zelda/magicWaterProjectile", 4, 1, 1, this, 32, 32, Orientation.values());
		animation = RPGSprite.createAnimations(1, sprites, true)[0];

	}

	@Override 
	public void update(float deltaTime) {

		animation.update(deltaTime);
		super.update(deltaTime);
	}


	@Override
	public float getDmg() {
		return DMG;
	}

	@Override
	public Vulnerability getAttackType() {
		return ATTACK_TYPE;
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}

	/**	 Inner Class Handler for MagicWaterProjectile	 **/

	private class MagicWaterProjectileInteractionHandler implements ARPGInteractionVisitor{
		
		@Override
		public void interactWith(Monster monster) {

			if(getAttackType().isInVulnerabilityArray(monster.getVulnerabilities())) {

				monster.receiveDmg(MagicWaterProjectile.this);
				setHasInteracted(true);
				stop();
			}
		}

		@Override
		public void interactWith(FireSpell fire) {
			MagicWaterProjectile.this.getOwnerArea().unregisterActor(fire);
		}
	}

}
