package ch.epfl.cs107.play.game.arpg.actor.monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.Coin;
import ch.epfl.cs107.play.game.arpg.actor.Vulnerability;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;

public class LogMonster extends Monster {

    //static variables
    private final static Vulnerability[] VULNERABILITIES = {Vulnerability.PHYSICAL, Vulnerability.FIRE};

    private final static float MAX_HP = 6.0f;

    private final static int CELL_VIEW_NB = 8;

    private final static float DAMAGE = 2.f;

    private final static int MAX_INACTION_TIME = 50;
    private final static float MIN_SLEEPING_DURATION = 100;
    private final static float MAX_SLEEPING_DURATION = 150;

    private final static int FRAMES_MOVE = 10;
    private final static int FRAMES_ATTACKING = 5;

    private final static int ANIMATION_DURATION = 5;

    private final static float DELTA = -0.5f;

    //attributes
    private LogMonsterState currentState;
    private float inactionDuration;
    private float sleepInaction;

    private Sprite[][] moveSprites;
    private Sprite[] sleepingSprites;
    private Sprite[] wakingUpSprites;

    private Animation[] moveAnim;
    private Animation sleepingAnim;
    private Animation wakingUpAnim;

    private int cellAttackCount;

    /**
     * Constructor of AreaEntity LogMonster
     * @param area
     * @param orientation
     * @param position
     */
    public LogMonster(Area area, Orientation orientation, DiscreteCoordinates position) {

        super(area, orientation, position, MAX_HP, VULNERABILITIES, DAMAGE);

        currentState = LogMonsterState.IDLE;
        inactionDuration = 0;


        setHandler(new LogMonsterInteractionHandler());

        moveSprites = RPGSprite.extractSprites("zelda/logMonster", 4, 2, 2, this, 32, 32,
                new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT});
        setSpriteArraysAnchor(moveSprites, DELTA, 0);

        sleepingSprites = createSpritesColumn("zelda/logMonster.sleeping", 0, 4, 32, 32, 2, 2);
        setSpritesAnchor(sleepingSprites, DELTA, 0);

        wakingUpSprites = createSpritesColumn("zelda/logMonster.wakingUp", 0, 3, 32, 32, 2, 2);
        setSpritesAnchor(wakingUpSprites, DELTA, 0);

        moveAnim = createAnimationArray(ANIMATION_DURATION, moveSprites);

        sleepingAnim = new Animation(ANIMATION_DURATION, sleepingSprites);
        wakingUpAnim = new Animation(ANIMATION_DURATION, wakingUpSprites, false);

        chooseAnim();

        cellAttackCount = -1;

    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return currentState.canInteract;
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList
                (getCurrentMainCellCoordinates()) ;
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        switch(currentState) {
            case IDLE :
                return viewCells();

            case ATTACKING :
                return Collections.singletonList
                        (getCurrentMainCellCoordinates().jump(getOrientation().toVector()));

            default :
                return null;
        }
    }

    /**
     * @return List containing the field of view cells corresponding to the LogMonster's Orientation when attacking
     */
    private List<DiscreteCoordinates> viewCells() {

        List<DiscreteCoordinates> cells = new ArrayList<DiscreteCoordinates>();
        cells.add(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));

        for (int i = 1 ; i <= CELL_VIEW_NB ; ++i) {
            cells.add(cells.get(i-1).jump(getOrientation().toVector()));
        }

        return cells;
    }

    @Override
    public void update(float deltaTime) {
        if(getIsDead()) {
            currentState = LogMonsterState.DEAD;
        } else {
            chooseAnim();
        }

        if(getCurrentAnim().isCompleted() && !getIsDead()) {
            currentState = LogMonsterState.IDLE;
        }

        ++inactionDuration;

        if((inactionDuration >= (currentState != LogMonsterState.SLEEPING ? MAX_INACTION_TIME : sleepInaction)) && !getIsDead()) {
            inactionDuration = 0;
            action();
        }

        super.update(deltaTime);
    }

    @Override
    public void animationUpdate(float deltaTime) {
        if(isDisplacementOccurs()
                || (currentState == LogMonsterState.DEAD  ||
                (currentState != LogMonsterState.IDLE && currentState != LogMonsterState.FALLING_ASLEEP))) {

            getCurrentAnim().update(deltaTime);
        } else {
            getCurrentAnim().reset();
        }
    }

    /**
     * determines how LogMonster reacts depending on its state
     */
    public void action() {
        switch(currentState) {
            case IDLE :
                if (!isDisplacementOccurs()) {
                    orientate(Orientation.values()[RandomGenerator.getInstance().nextInt(4)]);
                    movement();
                }
                return;

            case ATTACKING :
                if (isTargetReached()) {
                    ++cellAttackCount;
                }

                if(cellAttackCount <= CELL_VIEW_NB) {
                    move(FRAMES_ATTACKING);
                } else {
                    currentState = LogMonsterState.FALLING_ASLEEP;
                    cellAttackCount = -1; //starts at -1 to prevent considering the cell where he is standing before starting moving
                }

                inactionDuration = MAX_INACTION_TIME;
                return;

            case FALLING_ASLEEP :
                orientate(Orientation.DOWN);
                sleepInaction = MIN_SLEEPING_DURATION + RandomGenerator.getInstance().nextFloat()*
                        (MAX_SLEEPING_DURATION - MIN_SLEEPING_DURATION);

                currentState = LogMonsterState.SLEEPING;
                return;


            case SLEEPING :
                currentState = LogMonsterState.WAKING_UP;
                return;

            case WAKING_UP :
                if(getCurrentAnim().isCompleted()) {
                    currentState = LogMonsterState.IDLE;
                }
                return;

            case DEAD :
                return;
        }
    }

    @Override
    protected void dropItem() {
        getOwnerArea().registerActor(new Coin(getOwnerArea(), getCurrentMainCellCoordinates()));
    }

    @Override
    protected void movement() {
        if (!getIsDead()) {
            orientate(Orientation.values()[RandomGenerator.getInstance().nextInt(4)]);
            move(FRAMES_MOVE);
        }


    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    private void chooseAnim() {
        switch(currentState) {
            case ATTACKING :
                setCurrentAnim(moveAnim[getOrientation().ordinal()]);
                getCurrentAnim().setSpeedFactor(3);
                break;

            case SLEEPING :
                setCurrentAnim(sleepingAnim);
                break;

            case WAKING_UP :
                setCurrentAnim(wakingUpAnim);
                break;

            default :
                setCurrentAnim(moveAnim[getOrientation().ordinal()]);
                getCurrentAnim().setSpeedFactor(1);
                break;
        }

    }

    /** Inner private Enum for the LogMonster states **/
    private enum LogMonsterState {
        IDLE(true),
        ATTACKING(true),
        FALLING_ASLEEP(false),
        SLEEPING(false),
        WAKING_UP(false),
        DEAD(false);

        private boolean canInteract;

        private LogMonsterState(boolean interact) {
            canInteract = interact;
        }
    }

    /**	Inner class LogMonsterInteractionHandler	 **/
    private class LogMonsterInteractionHandler extends MonsterInteractionHandler {

        @Override
        public void interactWith(ARPGPlayer player) {
            if (currentState == LogMonsterState.ATTACKING) {
                player.receiveDmg(LogMonster.this);
                currentState = LogMonsterState.FALLING_ASLEEP;

            } else {
                currentState = LogMonsterState.ATTACKING;
            }
        }
    }

}
