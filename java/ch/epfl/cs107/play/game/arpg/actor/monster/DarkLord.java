package ch.epfl.cs107.play.game.arpg.actor.monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.actor.*;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class DarkLord extends Monster {

	//static constants
	private final static Vulnerability[] VULNERABILITIES = { Vulnerability.MAGICAL };
	private final static Vulnerability ATTACK_TYPE = Vulnerability.MAGICAL;

	private final static float MAX_HP = 35.0f;

	private final static float ATTACKING_PROBABILITY = 0.5f;

	private final static Orientation[] ORDER = { Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT };
	private final static int ANIMATION_DURATION = 2;

	private final static int MIN_SPELL_WAIT_DURATION = 25;
	private final static int MAX_SPELL_WAIT_DURATION = 50;
	private final static int INACTION_CYCLE = 30;

	private final static int TELEPORTATION_RADIUS = 3;
	private final static int FIELD_OF_VIEW_RADIUS = 2;

	private static final int FRAMES_MOVE = 5;

	private static final float DELTA_X = -0.5f;

	//attributes
	private DarkLordState currentState;

	private Sprite[][] moveSprites;
	private Sprite[][] spellSprites;

	private Animation[] moveAnimations;
	private Animation[] spellAnimations;

	private float spellWait;
	private float inactionTime;

	private boolean teleports; //true when teleporting false otherwise

	private boolean spellCasting; //used to know when to draw the spell animation

	private DarkLord teleportedCopy; //used to avoid problems when teleporting

	/**
	 * private constructor used directly only in the class when creating a copy for teleportation so as to keep track of HP loss
	 * @param area
	 * @param orientation
	 * @param position
	 * @param currentHP
	 */
	private DarkLord(Area area, Orientation orientation, DiscreteCoordinates position, float currentHP) {
		super(area, orientation, position, MAX_HP, currentHP, VULNERABILITIES, 0);

		moveSprites = RPGSprite.extractSprites("zelda/darkLord", 3, 2, 2, this, 32, 32, ORDER);
		spellSprites = RPGSprite.extractSprites("zelda/darkLord.spell", 3, 2, 2, this, 32, 32, ORDER);

		setSpriteArraysAnchor(moveSprites, DELTA_X, 0);
		setSpriteArraysAnchor(spellSprites, DELTA_X, 0);

		moveAnimations = RPGSprite.createAnimations(ANIMATION_DURATION,moveSprites);
		spellAnimations = RPGSprite.createAnimations(ANIMATION_DURATION,spellSprites, false);
		setCurrentAnim(moveAnimations[getOrientation().ordinal()]);



		currentState = DarkLordState.IDLE;


		setHandler(new DarkLordInteractionHandler());

		spellWait = MIN_SPELL_WAIT_DURATION + RandomGenerator.getInstance().nextFloat()*(MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
		inactionTime = INACTION_CYCLE;

		spellCasting = false;

		teleports = false;
	}

	/**
	 * public constructor used to instanciate a DarkLord outside the class
	 * @param area
	 * @param orientation
	 * @param position
	 */
	public DarkLord(Area area, Orientation orientation, DiscreteCoordinates position) {
		this(area, orientation, position, MAX_HP);
	}

	@Override
	public boolean takeCellSpace() {
		return !getIsDead() && !teleports;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {

		List<DiscreteCoordinates> cells = new ArrayList<DiscreteCoordinates>();

		//starting from the left corner
		cells.add(getCurrentMainCellCoordinates().jump(new Vector(-FIELD_OF_VIEW_RADIUS, -FIELD_OF_VIEW_RADIUS)));

		for (int i = 0 ; i <= 2*FIELD_OF_VIEW_RADIUS ; ++i) {
			for (int j = 0 ; j <= 2*FIELD_OF_VIEW_RADIUS ; j++) {
				cells.add(cells.get(0).jump(i,j));
			}
		}

		cells.remove(0); //removing duplicate
		cells.remove(getCurrentMainCellCoordinates()); //removing current main cell coordinates

		return cells;
	}

	@Override
	public boolean wantsCellInteraction() {
		return false;
	}

	@Override
	public boolean isCellInteractable() {
		return !getIsDead();
	}

	@Override
	public boolean wantsViewInteraction() {
		return !getIsDead() && !(currentState == DarkLordState.TELEPORTING);
	}

	@Override
	protected void dropItem() {
		getOwnerArea().registerActor(new CastleKey(getOwnerArea(), getCurrentMainCellCoordinates()));

	}

	@Override
	protected void movement() {
		if (!getIsDead() && !isDisplacementOccurs()) {
			orientate(Orientation.values()[RandomGenerator.getInstance().nextInt(4)]);
			setCurrentAnim(moveAnimations[getOrientation().ordinal()]);
			move(FRAMES_MOVE);
		}
	}

	/**
	 * tests and selects a random orientation of all four possible if it can allow a fireSpell
	 * to enter the area in the cell following DarkLord in that orientation, then orientates DarkLord accordingly if a fireSpell can be placed
	 * @return boolean : true if a fireSpell can be placed, false otherwise
	 */
	private boolean strategicOrienting() {

		Orientation randomOrientation;
		List<Orientation> OrientationTrials = new ArrayList<Orientation>();
		int randomIndex;

		for (Orientation o : Orientation.values()) {
			OrientationTrials.add(o);
		}

		List<DiscreteCoordinates> firePositionTrial;
		FireSpell fireTrial;

		do {
			randomIndex = RandomGenerator.getInstance().nextInt(OrientationTrials.size());
			randomOrientation = Orientation.values()[randomIndex];

			OrientationTrials.remove(randomIndex);

			firePositionTrial = Collections.singletonList
					(getCurrentMainCellCoordinates().jump(randomOrientation.toVector()));

			fireTrial = new FireSpell(getOwnerArea(), randomOrientation, firePositionTrial.get(0), 0);

		} while (!OrientationTrials.isEmpty() && !getOwnerArea().canEnterAreaCells(fireTrial, firePositionTrial));

		if(getOwnerArea().canEnterAreaCells(fireTrial, firePositionTrial)) {
			orientate(randomOrientation);
			return true;
		}

		return false;
	}

	/**
	 * switches the DarkLord's currentState to ATTACKING or SUMMONING randomly
	 */
	private void randomState() {
		if(RandomGenerator.getInstance().nextFloat() <= ATTACKING_PROBABILITY) {
			currentState = DarkLordState.ATTACKING;

		}else {
			currentState = DarkLordState.SUMMONING;
		}
	}

	/**
	 * determines DarkLord's actions according to its currentState
	 */
	private void action() {
		switch(currentState) {
			case IDLE :
				--inactionTime;
				if(inactionTime <= 0 && !spellCasting) { //moving only if no spell are being casted
					movement();
					inactionTime = INACTION_CYCLE;
				}
				break;

			case ATTACKING :
				if(strategicOrienting()) {
					getOwnerArea().registerActor(new FireSpell(getOwnerArea(), getOrientation(),getCurrentMainCellCoordinates().jump(getOrientation().toVector()), FireSpell.randomForce()));

					setCurrentAnim(spellAnimations[getOrientation().ordinal()]);
					spellCasting = true;
					currentState = DarkLordState.IDLE;
				}
				break;

			case SUMMONING :
				if(strategicOrienting()) {
					getOwnerArea().registerActor(new FlameSkull(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector())));
					setCurrentAnim(spellAnimations[getOrientation().ordinal()]);
					spellCasting = true;
					currentState = DarkLordState.IDLE;
				}
				break;

			case CASTING_TELEPORTATION :
				setCurrentAnim(spellAnimations[getOrientation().ordinal()]);
				spellCasting = true;
				currentState = DarkLordState.TELEPORTING;
				break;

			case TELEPORTING :
				teleportedCopy = teleport();
				currentState = DarkLordState.IDLE;
				break;
		}



	}

	/**
	 * in charge of DarkLord's teleportation
	 * @return teleported DarkLord (new instance if teleportation was "successful")
	 */
	private DarkLord teleport() {
		int currentX = getCurrentMainCellCoordinates().x,
				currentY = getCurrentMainCellCoordinates().y;

		int trialX,
				trialY;

		DiscreteCoordinates trialPos;

		int trialsNb = (TELEPORTATION_RADIUS*2 + 1)*(TELEPORTATION_RADIUS*2 + 1); //total number of cells in the TELEPORTATION_RADIUS
		List<Integer> trialsX = new ArrayList<Integer>();
		List<Integer> trialsY = new ArrayList<Integer>();

		List<DiscreteCoordinates> trialPosSingleton;

		DarkLord teleportedDarkLord = this;
		do {
			trialX = currentX + (RandomGenerator.getInstance().nextDouble() >= 0.5 ? 1 : -1)*
					RandomGenerator.getInstance().nextInt(TELEPORTATION_RADIUS + 1);

			trialY = currentY + (RandomGenerator.getInstance().nextDouble() >= 0.5 ? 1 : -1)*
					RandomGenerator.getInstance().nextInt(TELEPORTATION_RADIUS + 1);

			trialPos = new DiscreteCoordinates(trialX, trialY);

			trialPosSingleton = Collections.singletonList(trialPos);


			if(trialsX.contains(trialX) && trialsY.contains(trialY)) {
				continue;

			} else {
				trialsX.add(trialX);
				trialsY.add(trialY);
			}

			teleportedDarkLord = new DarkLord(getOwnerArea(), getOrientation(), trialPos, getHp());

		} while((trialsX.size() < trialsNb) && !getOwnerArea().canEnterAreaCells(teleportedDarkLord, trialPosSingleton));


		if(super.getOwnerArea().canEnterAreaCells(teleportedDarkLord, trialPosSingleton)) {

			teleports = true;
			getOwnerArea().registerActor(teleportedDarkLord);
			return teleportedDarkLord;
		}

		return this;

	}

	@Override
	public void update(float deltaTime) {
		if(!getIsDead()) {

			//avoiding DarkLord from disappearing in the case its teleportedCopy (teleported version) wouldn't have been added
			if (getOwnerArea().exists(teleportedCopy) && teleports) {
				getOwnerArea().unregisterActor(this);
			} else {
				teleports = false;
			}

			//spell casting cycle
			--spellWait;
			if(spellWait <= 0) {
				randomState();
				spellWait = MIN_SPELL_WAIT_DURATION +
						RandomGenerator.getInstance().nextInt(MAX_SPELL_WAIT_DURATION - MIN_SPELL_WAIT_DURATION);
			}

			action();
		}

		super.update(deltaTime);

	}

	@Override
	protected void animationUpdate(float deltaTime) {

		if(isDisplacementOccurs() || spellCasting || getIsDead()) {

			getCurrentAnim().update(deltaTime);

			if(getCurrentAnim().isCompleted() && !getIsDead()) {
				getCurrentAnim().reset();
				setCurrentAnim(moveAnimations[getOrientation().ordinal()]);
				spellCasting = false;
			}

		} else {
			getCurrentAnim().reset();

		}

	}

	@Override
	public void draw(Canvas canvas) {
		//not drawing itself while teleporting
		if (!teleports) {
			super.draw(canvas);
		}
	}

	@Override
	public Vulnerability getAttackType() {
		return ATTACK_TYPE;
	}

	/** private enum used for DarkLord states*/
	private enum DarkLordState {
		IDLE,
		ATTACKING,
		SUMMONING,
		CASTING_TELEPORTATION,
		TELEPORTING;

	}

	/** private Interaction handler */
	private class DarkLordInteractionHandler extends MonsterInteractionHandler {

		@Override
		public void interactWith(ARPGPlayer player) {
			currentState = DarkLordState.CASTING_TELEPORTATION;
		}
	}

}
