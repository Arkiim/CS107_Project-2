package ch.epfl.cs107.play.game.arpg.actor.monster;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;
import ch.epfl.cs107.play.game.arpg.damage.DamageReceiver;
import ch.epfl.cs107.play.game.arpg.damage.DmgInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.damage.MonsterAttacker;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Monster extends MovableAreaEntity implements Interactor, MonsterAttacker, DamageReceiver {

	//static constants
	private final static int DEATH_ANIM_DURATION = 3;

	private static final int DIGIT_SIZE = 16;
	private static final float DIGIT_GAME_SIZE = 0.5f;
	private final static int DMG_DRAW_DURATION = 6;

	//attributes
	private float maxHP;
	private float hp;

	private Vulnerability[] vulnerabilities;

	private MonsterInteractionHandler handler;
	private float damage;

	private Animation currentAnim;
	private int currentAnimIndex;
	private Animation[] animations;
	private Sprite[] dmgUnits;
	private Sprite[] dmgDecimals;
	private int dmgCounter;
	private float dmgReceived;


	private boolean isDead;
	private Sprite[] deathSprites;
	private Animation deathAnim;

	/**
	 * Constructor
	 *
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public Monster(Area area, Orientation orientation, DiscreteCoordinates position, float maxHPValue, float hpValue, Vulnerability[] vulnerabilities, float dmg) {
		super(area, orientation, position);

		deathSprites = createSprites("zelda/vanish", 7, 32);
		setSpritesAnchor(deathSprites, 0, 0.3f);

		deathAnim = new Animation(DEATH_ANIM_DURATION, deathSprites, false);
		isDead = false;

		maxHP = maxHPValue;
		hp = hpValue;

		dmgCounter = 0;
		dmgUnits = createDmgUnits();
		dmgDecimals = createDmgDecimals();
		this.vulnerabilities = vulnerabilities;

		damage = dmg;
	}

	public Monster(Area area, Orientation orientation, DiscreteCoordinates position, float maxHPValue, Vulnerability[] vulnerabilities, float dmg) {
		this(area, orientation, position, maxHPValue, maxHPValue, vulnerabilities, dmg);
	}

	/**
	 * @return copy of the array of vulnerabilities,
	 * used to determine whether or not damage should be received
	 */
	public Vulnerability[] getVulnerabilities() {
		return vulnerabilities.clone();
	}

	/**
	 * @return true : dead, false : alive
	 */
	boolean getIsDead() {
		return isDead;
	}

	/**
	 * makes the monster die
	 */
	public final void makeDead() {
		currentAnim = deathAnim;
		isDead = true;
	}

	/**
	 * makes sure a monster isn't
	 * traversable while alive and is after its death
	 */
	@Override
	public boolean takeCellSpace() {
		return !getIsDead();
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
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler);
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
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}

	/**
	 * makes the monster drop an item
	 */
	protected abstract void dropItem();

	/**
	 * update for the animation
	 *
	 * @param deltaTime
	 */
	protected void animationUpdate(float deltaTime) {
		if (!getIsDead() && getOrientation().ordinal() != currentAnimIndex) {

			currentAnimIndex = getOrientation().ordinal();
			currentAnim = animations[currentAnimIndex];
		}

		currentAnim.update(deltaTime);
	}

	@Override
	public final float getHp() {
		return hp;
	}

	@Override
	public final float getDmg() {
		return damage;
	}

	@Override
	public void update(float deltaTime) {
		if (hp <= 0) {
			makeDead();
		}

		animationUpdate(deltaTime);
		super.update(deltaTime);
	}

	/**
	 * controls the monster's movements
	 */
	protected abstract void movement();

	/**
	 * Indicates if the monster's current Orientation
	 * is the same as the given orientation
	 * @param orientation
	 * @return boolean
	 */
	boolean isSameDirection(Orientation orientation) {
		return getOrientation().equals(orientation);
	}


	/**
	 * used by the monster to receive damage
	 * and avoid hp from getting off-boundaries
	 */
	@Override
	public void receiveDmg(DmgInteractionVisitor v) {
		if (hp > 0 && v.getDmg() != 0) {
			dmgReceived = v.getDmg();
			hp -= dmgReceived;
			dmgCounter = DMG_DRAW_DURATION;
		}

		if (hp < 0) {
			hp = 0;
		} else if (hp > maxHP) {
			hp = maxHP;
		}

	}

	@Override
	public void draw(Canvas canvas) {
		if (currentAnim.isCompleted() && isDead) {
			getOwnerArea().unregisterActor(this);
			dropItem();

		} else {
			currentAnim.draw(canvas);
			if (!getIsDead()) {
				new RPGSprite("addedSprites/HPBarRed", hp / 10, 0.1f, this, new RegionOfInterest(0, 0, 32, 32),
						new Vector(0.53f - maxHP * 0.05f, 2.f), 1, 1).draw(canvas);
			}
		}

		//drawing the sprites corresponding to eventual damage
		if (dmgCounter > 0 && dmgReceived > 0) {
			--dmgCounter;
			Sprite[] dmgSprites = createDmgSprites(dmgReceived);
			for (Sprite s : dmgSprites) {
				s.draw(canvas);
				//	System.out.println(dmgCounter);
			}
		}

	}

	/**
	 * creates the sprite array used to draw the damage units
	 *
	 * @return sorted sprites of each digits
	 */
	private Sprite[] createDmgUnits() {
		Sprite[] sprites = new Sprite[10];

		//adding the 0
		sprites[0] = new RPGSprite("addedSprites/damageDigits", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE,
				this, new RegionOfInterest(DIGIT_SIZE, 2 * DIGIT_SIZE, DIGIT_SIZE, DIGIT_SIZE), new Vector(0, 2.3f), 1, 2);
		int index = 1;

		//adding 1 to 8
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 4; ++j) {
				sprites[index] = new RPGSprite("addedSprites/damageDigits", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE,
						this, new RegionOfInterest(DIGIT_SIZE * j, DIGIT_SIZE * i, DIGIT_SIZE, DIGIT_SIZE), new Vector(0, 2.3f), 1, 2);
				++index;
			}
		}

		//adding 9
		sprites[9] = new RPGSprite("addedSprites/damageDigits", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE,
				this, new RegionOfInterest(0, 2 * DIGIT_SIZE, DIGIT_SIZE, DIGIT_SIZE), new Vector(0, 2.3f), 1, 2);
		return sprites;
	}

	/**
	 * creates the sprite array used to draw the damage decimals + "-"
	 *
	 * @return sorted sprites of each decimal + "-"
	 */
	private Sprite[] createDmgDecimals() {
		Sprite[] sprites = new Sprite[11];

		//adding the 0
		sprites[0] = new RPGSprite("addedSprites/damageDecimals", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE,
				this, new RegionOfInterest(DIGIT_SIZE, 2 * DIGIT_SIZE, DIGIT_SIZE, DIGIT_SIZE),
				new Vector(DIGIT_GAME_SIZE, 2.3f), 1, 2);

		//adding 1 to 8
		int index = 1;
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 4; ++j) {
				sprites[index] = new RPGSprite("addedSprites/damageDecimals", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE,
						this, new RegionOfInterest(DIGIT_SIZE * j, DIGIT_SIZE * i, DIGIT_SIZE, DIGIT_SIZE),
						new Vector(DIGIT_GAME_SIZE, 2.3f), 1, 2);
				++index;
			}
		}

		//adding 9
		sprites[9] = new RPGSprite("addedSprites/damageDecimals", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE, this,
				new RegionOfInterest(0, 2 * DIGIT_SIZE, DIGIT_SIZE, DIGIT_SIZE),
				new Vector(DIGIT_GAME_SIZE, 2.3f), 1, 2);

		//adding "-"
		sprites[10] = new RPGSprite("addedSprites/damageDecimals", DIGIT_GAME_SIZE, DIGIT_GAME_SIZE,
				this, new RegionOfInterest(2 * DIGIT_SIZE, 2 * DIGIT_SIZE, DIGIT_SIZE, DIGIT_SIZE),
				new Vector(-DIGIT_GAME_SIZE, 2.3f), 1, 2);

		return sprites;
	}

	/**
	 * generates an array of sprites corrisponding to the dmg (float) put in paramater
	 *
	 * @param dmg
	 * @return 1-dimensional array containing 3 sprites
	 */
	private Sprite[] createDmgSprites(float dmg) {
		Sprite[] dmgSprites = new Sprite[3];
		if (dmg > 9.9f) {
			dmg = 9.9f;
		}
		dmgSprites[0] = dmgUnits[(int) dmg];
		dmgSprites[1] = dmgDecimals[(int) ((dmg - (int) dmg) * 10)];
		dmgSprites[2] = dmgDecimals[10];
		return dmgSprites;

	}

	Animation[] getAnimations() {
		return animations;
	}

	void setAnimations(Animation[] animations) {
		this.animations = animations;
	}

	/**
	 * @return the currentAnim
	 */
	Animation getCurrentAnim() {
		return currentAnim;
	}

	/**
	 * @param currentAnim the currentAnim to set
	 */
	void setCurrentAnim(Animation currentAnim) {
		this.currentAnim = currentAnim;
	}

	void setHandler(MonsterInteractionHandler handler) {
		this.handler = handler;
	}

	/**
	 * private class handler
	 **/
	abstract class MonsterInteractionHandler implements ARPGInteractionVisitor {
	}

}
