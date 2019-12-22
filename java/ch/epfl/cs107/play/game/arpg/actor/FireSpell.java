package ch.epfl.cs107.play.game.arpg.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.damage.DamageReceiver;
import ch.epfl.cs107.play.game.arpg.damage.MonsterAttacker;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

public class FireSpell extends AreaEntity implements MonsterAttacker, Interactor {

	private final static int FORCE_MAX = 7;
	private final static int FORCE_MIN = 0;

	private final static float DAMAGE = 0.5f;
	private final static Vulnerability ATTACK_TYPE = Vulnerability.FIRE;
	private final static int DMG_CYCLE = 30; // prevents the fire from stacking too much damage in a short period of time

	private final static float MIN_LIFE_TIME = 120.f;
	private final static float MAX_LIFE_TIME = 140.f;

	private final static float PROPAGATION_TIME_CYCLE = 20.f;

	private final static int ANIMATION_DURATION = 3;

	private Sprite[] sprites;
	private Animation animation;

	private float lifeTime;

	private int dmgCount; // prevents the fire from stacking too much damage in a short period of time
	private int propagationCount;
	private int propagationForce;
	private List<DiscreteCoordinates> propagationCells;

	private List<DamageReceiver> dmgVictims;

	private FireInteractionHandler handler;

	/**
	 * FireSpell constructor
	 * @param area
	 * @param orientation
	 * @param position
	 * @param force - determines the fire's propagation
	 */
	public FireSpell(Area area, Orientation orientation, DiscreteCoordinates position, int force) {

		super(area, orientation, position);

		// making sure propagationForce doesn't get off boundaries
		if (force > FORCE_MAX) {
			propagationForce = FORCE_MAX;
		} else if (force < FORCE_MIN) {
			propagationForce = FORCE_MIN;
		} else {
			propagationForce = force;
		}

		sprites = createSprites("zelda/fire", 7, 16);
		animation = new Animation(ANIMATION_DURATION, sprites);

		dmgVictims = new ArrayList<DamageReceiver>();
		dmgCount = 0;

		propagationCount = 0;
		propagationCells = new ArrayList<DiscreteCoordinates>();

		lifeTime = MIN_LIFE_TIME + RandomGenerator.getInstance().nextFloat() * (MAX_LIFE_TIME - MIN_LIFE_TIME);
		handler = new FireInteractionHandler();
	}

	@Override
	public float getDmg() {
		return DAMAGE;
	}

	@Override
	public Vulnerability getAttackType() {
		return ATTACK_TYPE;
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}

	@Override
	public boolean takeCellSpace() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return true;
	}

	@Override
	public boolean isViewInteractable() {
		return false;
	}

	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);

		// life cycle
		--lifeTime;
		if (lifeTime <= 0) {
			getOwnerArea().unregisterActor(this);

		} else {
			dmgReset();

			propagation();

		}
		super.update(deltaTime);
	}

	/**
	 * manages the FireSpell's propagation
	 */
	private void propagation() {
		++propagationCount;
		DiscreteCoordinates newFireSpellCell = getFieldOfViewCells().get(0);

		if ((propagationCount >= PROPAGATION_TIME_CYCLE) && (propagationForce > 0)
				&& (!propagationCells.contains(newFireSpellCell)) 
				&& getOwnerArea().canEnterAreaCells(this, getFieldOfViewCells())) {

			getOwnerArea().registerActor(new FireSpell(getOwnerArea(), getOrientation(),
					newFireSpellCell, propagationForce - 1));
			
			propagationCells.add(newFireSpellCell);
			propagationCount = 0;

		}
	}

	/**
	 * in charge of reseting dmgVictims according to DMG_CYCLE and dmgCount
	 */
	private void dmgReset() {

		if (dmgCount >= DMG_CYCLE && !dmgVictims.isEmpty()) {
			dmgVictims.clear();
			dmgCount = 0;

		} else if (!dmgVictims.isEmpty()) {
			++dmgCount;

		}
	}

	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);

	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
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

	/**
	 * keeping track of the latest damage victim so as to avoid dealing them damage
	 * again too quickly, adding a DamageReceiver to dmgVictims
	 * 
	 * @param victim
	 */
	private void dmgDealt(DamageReceiver victim) {
		dmgVictims.add(victim);
	}

	/**
	 * tests if a DamageReceiver is in dmgVictims
	 * 
	 * @param victim - DamageReceiver
	 * @return boolean
	 */
	private boolean wasDmgDealt(DamageReceiver victim) {
		return dmgVictims.contains(victim);
	}

	public static int randomForce() {
		return FORCE_MIN + RandomGenerator.getInstance().nextInt(FORCE_MAX - FORCE_MIN + 1);
	}

	private class FireInteractionHandler implements ARPGInteractionVisitor {

		/**
		 * dealing damage to a Monster
		 */
		@Override
		public void interactWith(Monster monster) {
			if (getAttackType().isInVulnerabilityArray(monster.getVulnerabilities()) && !wasDmgDealt(monster)) {
				monster.receiveDmg(FireSpell.this);
				dmgDealt(monster);
			}
		}

		/**
		 * dealing damage to the ARPGPlayer
		 */
		@Override
		public void interactWith(ARPGPlayer player) {
			if (!wasDmgDealt(player)) {
				player.receiveDmg(FireSpell.this);
				dmgDealt(player);
			}
		}

		/**
		 * cutting a Grass
		 */
		@Override
		public void interactWith(Grass grass) {
			grass.setIsCut();
		}

		/**
		 * exploding a Bomb
		 */
		@Override
		public void interactWith(Bomb bomb) {
			bomb.setExplode();
		}
	}

}
