package ch.epfl.cs107.play.game.arpg.actor.weapon;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.FireSpell;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.Orb;
import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Arrow extends Projectile {

	public final static int RANGE = 5;
	/** Inverse of speed => The higher the value, the slowest the arrow will go **/
	public final static int SPEED = 2;
	private final static float dmg = 1.5f;

	private ArrowInteractionHandler handler;

	private final static Vulnerability ATTACK_TYPE = Vulnerability.PHYSICAL;

	private Sprite sprite;
	//Commenter MonsterAttacker

	/**
	 * Constructor for Arrow
	 * @param area (Area)
	 * @param orientation (Orientation)
	 * @param position (DiscreteCoordinates)
	 */
	public Arrow(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);	 

		handler = new ArrowInteractionHandler();

		this.setRange(RANGE);
		this.setSpeed(SPEED);

		//Choose the right region of interest based on the currentOrientation of player (the one on where is created the object "Arrow")
		sprite = new RPGSprite("zelda/arrow", 1, 1, this, new RegionOfInterest(getOrientation().ordinal()*32, 0, 32, 32));

		/**
		 * Set the anchor of arrows depending on their orientation when shot
		 */
		switch(getOrientation().ordinal()) {

		case 0 :
			sprite.setAnchor(new Vector(0, 0.5f));
			break;
		case 1 :
			sprite.setAnchor(new Vector(0.5f, 0));
			break;
		case 2 :
			sprite.setAnchor(new Vector(0, 0));
			break;
		case 3 :
			sprite.setAnchor(new Vector(-0.5f, 0));
			break;

		}

		//An arrow should be shot as soon as the bow was armed with it.
		//Since in ARPGPlayer the object Arrow isn't created until the player call the method "useItem()" (by clicking on space)
		//The Object arrow is created just at the moment when the player wants to use the bow. It should then be fired immediatly upon construction
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);	
	}

	@Override
	public float getDmg() {
		return dmg;
	}

	/**
	 * reset the Arrow's motion and call the inherited method from the super-class projectile, that unregister the arrows
	 */
	@Override 
	protected void stop() {
		resetMotion();
		super.stop();
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
		sprite.draw(canvas);
	}

	/** Inner Class ArrowInteractionHandler */

	private class ArrowInteractionHandler implements ARPGInteractionVisitor {

		@Override
		public void interactWith(Monster monster) {
			if(getAttackType().isInVulnerabilityArray(monster.getVulnerabilities())) {
				monster.receiveDmg(Arrow.this);
			}
			setHasInteracted(true);
			stop();
		}

		@Override
		public void interactWith(Grass grass) {
			grass.setIsCut();
		}

		@Override
		public void interactWith(Orb orb) {

			if(orb.getSignalState()) {
				orb.setStateOf();
				setHasInteracted(true);

			} else {
				orb.setStateOn();
				setHasInteracted(true);
			}
		}


		@Override
		public void interactWith(Bomb bomb) {
			bomb.setExplode();
			setHasInteracted(true);
			stop();
		}

		@Override
		public void interactWith(FireSpell fireSpell) {
			Arrow.this.getOwnerArea().unregisterActor(fireSpell);
		}

	}
}
