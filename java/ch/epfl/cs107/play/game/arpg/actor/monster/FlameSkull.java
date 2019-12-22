package ch.epfl.cs107.play.game.arpg.actor.monster;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.Bomb;
import ch.epfl.cs107.play.game.arpg.actor.FlyableEntity;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;
import ch.epfl.cs107.play.game.arpg.damage.DamageReceiver;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

public class FlameSkull extends Monster implements FlyableEntity{

	private static final Vulnerability ATTACK_TYPE = Vulnerability.FIRE;
	private static final Vulnerability[] VULNERABILITIES = {Vulnerability.PHYSICAL, Vulnerability.MAGICAL};

	private static int ANIMATION_DURATION = 5;

	private final static float MAX_HP = 2.f;
	private final static int MIN_LIFE = 200;
	private final static int MAX_LIFE = 500;

	private final static float PROBABILITY_CHANGE_ORIENTATION = 0.2f;
	private static final int FRAMES_MOVE = 10;
	private static final float DAMAGE = 0.5f;

	private float lifeExpectancy;

	private int dmgCount; //counts to prevent FlamSkull from stacking damage on DamageReceivers
	private List<DamageReceiver> dmgVictims;

	/**
	 * Constructor of Monster FlameSkull
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public FlameSkull(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position, MAX_HP, VULNERABILITIES, DAMAGE);

		Sprite[][] spriteArrays = createSpritesArrays("zelda/flameSkull", 4, 3, 32, 32, 2, 2);
		Sprite[] tmp = spriteArrayCopy(spriteArrays[1]);

		spriteArrays[1] = spriteArrayCopy(spriteArrays[3]);
		spriteArrays[3] = spriteArrayCopy(tmp);
		setSpriteArraysAnchor(spriteArrays, -0.5f,0);

		setAnimations(createAnimationArray(ANIMATION_DURATION, spriteArrays));
		setCurrentAnim(getAnimations()[getOrientation().ordinal()]);

		lifeExpectancy = MIN_LIFE + RandomGenerator.getInstance().nextInt(MAX_LIFE - MIN_LIFE);

		dmgVictims = new ArrayList<DamageReceiver>();

		setHandler(new FlameSkullInteractionHandler());
	}

	@Override
	public void update(float deltaTime) {
		--lifeExpectancy;

		if(lifeExpectancy < 0) { makeDead(); }

		if(!dmgVictims.isEmpty() && isTargetReached() && isDisplacementOccurs() && dmgCount >= 2) {
			dmgVictims.clear();
			dmgCount = 0;
		} else if(!dmgVictims.isEmpty() && isDisplacementOccurs()) {
			++dmgCount;
		}

		if (!getIsDead()) {
			movement();
		}

		super.update(deltaTime);
	}

	@Override
	public boolean takeCellSpace() {
		return false;
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
	public void dropItem() {
		//getOwnerArea().registerActor(new Coin(getOwnerArea(), Orientation.UP, getCurrentMainCellCoordinates()));
	}

	@Override
	protected void movement() {
		float randomFloat = RandomGenerator.getInstance().nextFloat();

		if (randomFloat <= PROBABILITY_CHANGE_ORIENTATION) {
			orientate(Orientation.values()[RandomGenerator.getInstance().nextInt(4)]);
			move(FRAMES_MOVE);
		}

	}

	@Override
	public Vulnerability getAttackType() {
		return ATTACK_TYPE;
	}

	/**
	 * adds a DamageReceiver to the dmgVictims list
	 * @param victim
	 */
	public void dmgDealt(DamageReceiver victim) {
		dmgVictims.add(victim);
	}

	/**
	 * tests if a DamageReceiver is in dmgVictims
	 * @param victim
	 * @return true (is in dmgVictims) false (otherwise)
	 */
	public boolean wasDmgDealt(DamageReceiver victim) {
		return dmgVictims.contains(victim);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	/** Inner class FlameSkullInteractionHandler **/
	private class FlameSkullInteractionHandler extends MonsterInteractionHandler implements ARPGInteractionVisitor {

		@Override
		public void interactWith(ARPGPlayer player) {
			if(!wasDmgDealt(player)) {
				player.receiveDmg(FlameSkull.this);
				dmgDealt(player);
			}

		}

		@Override
		public void interactWith(Grass grass) {
			grass.setIsCut();
		}

		@Override
		public void interactWith(Bomb bomb) {
			bomb.setExplode();
		}

		public void interactWith(Monster monster) {
			if(getAttackType().isInVulnerabilityArray(monster.getVulnerabilities()) && !wasDmgDealt(monster)) {
				monster.receiveDmg(FlameSkull.this);
				dmgDealt(monster);
			}
		}
	}

}
