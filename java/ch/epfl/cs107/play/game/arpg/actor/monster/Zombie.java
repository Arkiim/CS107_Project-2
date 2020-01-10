package ch.epfl.cs107.play.game.arpg.actor.monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.Heart;
import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;
import ch.epfl.cs107.play.game.arpg.damage.DamageReceiver;
import ch.epfl.cs107.play.game.arpg.damage.DmgInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class Zombie extends Monster {

    private static final Orientation[] ORIENTATIONS = {Orientation.UP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT};
    private static final int ANIMATION_DURATION = 8;
    private static final int MOVE_DURATION = 35;
    private static final float MAX_HP = 8;


    private final static int MIN_INACTION_DURATION = 35;
    private final static int MAX_INACTION_DURATION = 60;

    private final static float DELTA_ANCHOR = 0.5f;

    private static final int GRAVE_DURATION = 100;
    private final static double DAMAGE_PROBABILITY_GRAVE = 0.2;
    public static final int ATTACK_ANIM_DURATION = 16;
    private static final float DAMAGE = 1.0f;

    private static Vulnerability[] VULNERABILITIES = {Vulnerability.PHYSICAL, Vulnerability.MAGICAL};

    private Sprite graveSprite;

    private ZombieState currentState;

    private int inactionDuration;

    private final ZombieHealer healer;
    private int attackAnimCount;

    private List<DamageReceiver> dmgVictims;

    public Zombie(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position, MAX_HP, VULNERABILITIES, DAMAGE);

        Sprite[][] spriteArrays = RPGSprite.extractSprites("addedSprites/Zombie1.2", 4, 1, 2, this, 16, 32, ORIENTATIONS);
        setAnimations(RPGSprite.createAnimations(ANIMATION_DURATION, spriteArrays, true));
        setCurrentAnim(getAnimations()[getOrientation().ordinal()]);

        graveSprite = new RPGSprite("addedSprites/grave", 2, 2, this, new RegionOfInterest(0, 0, 32, 32),
                                    new Vector(-DELTA_ANCHOR, DELTA_ANCHOR), 1, 2.f);

        currentState = ZombieState.IDLE;
        inactionDuration = MIN_INACTION_DURATION
                           + RandomGenerator.getInstance().nextInt(MAX_INACTION_DURATION - MIN_INACTION_DURATION + 1);

        setHandler(new ZombieInteractionHandler());


        healer = new ZombieHealer();

        dmgVictims = new ArrayList<DamageReceiver>();
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return (currentState != ZombieState.GRAVE);
    }

    @Override
    protected void dropItem() {
        Heart heart = new Heart(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());

        if (getOwnerArea().canEnterAreaCells(heart, Collections.singletonList(getCurrentMainCellCoordinates()))) {
            getOwnerArea().registerActor(heart);
        }
    }

    @Override
    protected void movement() {
        orientate(Orientation.values()[RandomGenerator.getInstance().nextInt(4)]);
        setCurrentAnim(getAnimations()[getOrientation().ordinal()]);
        move(MOVE_DURATION);
    }

    @Override
    protected void animationUpdate(float deltaTime) {
        if (currentState == ZombieState.ATTACKING || isDisplacementOccurs() || getIsDead()) {
            getCurrentAnim().update(deltaTime);
        } else {
            getCurrentAnim().reset();
        }
    }

    private void action() {
        switch (currentState) {
            case IDLE:
                movement();
                inactionDuration =
                        MIN_INACTION_DURATION + RandomGenerator.getInstance().nextInt(MAX_INACTION_DURATION - MIN_INACTION_DURATION + 1);
                break;

            case GRAVE:
                currentState = ZombieState.IDLE;
                this.receiveDmg(healer);
                inactionDuration = 0;
                break;

            default:
                break;
        }
    }

    @Override
    public void update(float deltaTime) {
        --inactionDuration;

        if (currentState == ZombieState.ATTACKING) {
            --attackAnimCount;

            if (attackAnimCount <= 0) {
                currentState = ZombieState.IDLE;
            }

        } else if (inactionDuration <= 0) {
            action();
            dmgVictims.clear();
        }

        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if (currentState == ZombieState.GRAVE && !getIsDead()) {
            graveSprite.draw(canvas);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    public void receiveDmg(DmgInteractionVisitor v) {
        if (((currentState != ZombieState.GRAVE))
            || (RandomGenerator.getInstance().nextDouble() <= DAMAGE_PROBABILITY_GRAVE)) {

            super.receiveDmg(v);

            if (getHp() <= 2) {
                currentState = ZombieState.GRAVE;
                inactionDuration = GRAVE_DURATION;
            }
        }
    }

    /**
     * know if a DamageReceiver (victim) is in dmgVictims
     * @param victim
     * @return boolean
     */
    private boolean wasDmgDealt(DamageReceiver victim) {
        return dmgVictims.contains(victim);
    }

    private void dmgDealt(DamageReceiver victim) {
        dmgVictims.add(victim);
    }

    private enum ZombieState {
        IDLE,
        ATTACKING,
        GRAVE;
    }

    private class ZombieHealer implements DmgInteractionVisitor {

        @Override
        public float getDmg() {
            return (getHp() - MAX_HP);
        }

    }


    /** Inner interaction handler ZombieInteractionHandler */

    private class ZombieInteractionHandler extends MonsterInteractionHandler {

        @Override
        public void interactWith(ARPGPlayer player) {
            if (!wasDmgDealt(player)) {
                Zombie.this.currentState = ZombieState.ATTACKING;
                attackAnimCount = ATTACK_ANIM_DURATION;

                if (!ARPGPlayer.debugMode) {
                    player.receiveDmg(Zombie.this);
                    dmgDealt(player);
                }
            }
        }

    }


}
