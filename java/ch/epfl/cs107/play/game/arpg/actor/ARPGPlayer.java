package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.ARPG;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.monster.LogMonster;
import ch.epfl.cs107.play.game.arpg.actor.monster.Monster;
import ch.epfl.cs107.play.game.arpg.actor.monster.Zombie;
import ch.epfl.cs107.play.game.arpg.actor.weapon.Arrow;
import ch.epfl.cs107.play.game.arpg.actor.weapon.MagicWaterProjectile;
import ch.epfl.cs107.play.game.arpg.damage.DamageReceiver;
import ch.epfl.cs107.play.game.arpg.damage.DmgInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.damage.MonsterAttacker;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;
import ch.epfl.cs107.play.game.rpg.actor.Player;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class ARPGPlayer extends Player implements InventoryItem.Holder, DamageReceiver, MonsterAttacker, Reader {

    private final static int ANIMATION_DURATION = 4;
    private final static int BATTLE_ANIMATION_DURATION = 3;
    private final static float BATTLE_DELTA = -0.5f;

    private final static int ICON_SIZE = 16;
    private final static float ICON_SCALE = 0.85f;
    private final static float ICON_DELTA_X = 0.32f;
    private final static float ICON_DELTA_Y = -1.42f;
    private final static RegionOfInterest[] DIGITS_ROI = createDigitsROI();
    private final static int[] MOVEMENT = {Keyboard.W, Keyboard.D, Keyboard.S, Keyboard.A};

    private ARPGPlayerHandler handler;

    private final Sprite[][] defaultSprites;
    private final Sprite[] defaultGardSprites;
    private final Sprite[] gardBowSprites;
    private final Sprite[] gardSwordSprites;
    private final Sprite[] gardStaffSprites;
    private final Sprite graveSprite;


    private final Sprite[][] battleBowSprites;
    private final Sprite[][] battleSwordSprites;
    private final Sprite[][] battleStaffSprites;


    private Animation[] playerAnimationsBow;
    private Animation[] playerAnimationsSword;

    /** Order for the sprites of Bow, Staff_Animation and Sword */
    private final static Orientation[] orderBSAS = new Orientation[]{Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};

    /** Order for the sprites of Staff_Gard, and Player */
    private final static Orientation[] orderSGP = new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};

    private Animation battleAnimation;

    private Animation animation;
    private Animation[] defaultAnimations;

    public final static int MAX_HP = 6;
    private float hp;

    private ARPGInventory inventory;
    private static int currentItem;

    private final ARPGPlayerStatusGUI gearStatus;
    private ARPGPlayerStatusGUI currentItemStatus;
    private ARPGPlayerStatusGUI[] hearts;
    private ARPGPlayerStatusGUI goldDisplay;
    private ARPGPlayerStatusGUI[] goldCountDisplay;

    private boolean dontCutGrass;
    private boolean isJustCreated;
    private boolean debugMode;

    private State state;
    private State oldState;

    private Vulnerability currentAttackType;
    private float currentDmg;

    /**
     * Constructor of ARPGPlayer, initialize hp, sprites, animations, currentItem and the handler (of type ARPGHandler)
     *
     * @param owner (Area)
     * @param orientation (Orientation)
     * @param coordinates (DiscreteCoordinates)
     */
    public ARPGPlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates) {
        super(owner, orientation, coordinates);

        //Create Default Sprites
        defaultSprites = RPGSprite.extractSprites("zelda/player", 4, 1, 2, this, 16, 32, orderSGP);
        defaultGardSprites = createSpritesColumn("zelda/player", 0, 4, 16, 32, 1, 2, orderSGP);

        //Create Death Sprite
        graveSprite = new RPGSprite("addedSprites/grave", 2, 2, this);

        //Creates "Garde" Sprites
        gardBowSprites = createSpritesColumn("zelda/player.bow", 0, 4, 32, 32, 2, 2, orderBSAS);
        gardSwordSprites = createSpritesColumn("zelda/player.sword", 0, 4, 32, 32, 2, 2, orderBSAS);
        gardStaffSprites = createSpritesColumn("addedSprites/playerStaff", 0, 4, 16, 32, 1, 2, orderSGP);

        //Create battle Sprites used in the methode createAnimations
        battleBowSprites = RPGSprite.extractSprites("zelda/player.bow", 4, 2, 2, this, 32, 32, orderBSAS);
        battleSwordSprites = RPGSprite.extractSprites("zelda/player.sword", 4, 2, 2, this, 32, 32, orderBSAS);
        battleStaffSprites = RPGSprite.extractSprites("zelda/player.staff_water", 3, 2, 2, this, 32, 32, orderBSAS);

        defaultAnimations = RPGSprite.createAnimations(ANIMATION_DURATION, defaultSprites, true);
        //Battle / combat Animations
        playerAnimationsBow = RPGSprite.createAnimations(BATTLE_ANIMATION_DURATION, battleBowSprites, false);
        playerAnimationsSword = RPGSprite.createAnimations(BATTLE_ANIMATION_DURATION, battleSwordSprites, false);
        Animation[] playerAnimationsStaff;
        playerAnimationsStaff = RPGSprite.createAnimations(BATTLE_ANIMATION_DURATION, battleStaffSprites, false);

        //Correcting the Anchor of the Sprites / animations
        AreaEntity.setAnchor(gardBowSprites, BATTLE_DELTA, 0);
        AreaEntity.setAnchor(playerAnimationsBow, BATTLE_DELTA, 0);
        AreaEntity.setAnchor(gardSwordSprites, BATTLE_DELTA, 0);
        AreaEntity.setAnchor(playerAnimationsSword, BATTLE_DELTA, 0);
        AreaEntity.setAnchor(playerAnimationsStaff, BATTLE_DELTA, 0);


        this.handler = new ARPGPlayerHandler();

        hp = MAX_HP;

        inventory = new ARPGInventory();
        currentItem = 0;
        inventory.add(ARPGItem.SWORD, 1);

        battleAnimation = RPGSprite.createAnimations(BATTLE_ANIMATION_DURATION, chooseBattleSprites(), false)[getOrientation().ordinal()];

        gearStatus = new ARPGPlayerStatusGUI("zelda/gearDisplay", new RegionOfInterest(0, 0, 32, 32), 1.5f, true, 0, -1.75f);
        currentItemStatus =
                new ARPGPlayerStatusGUI(getCurrentItem().getSpriteName(), new RegionOfInterest(0, 0, ICON_SIZE, ICON_SIZE), ICON_SCALE,
                                        true, ICON_DELTA_X, ICON_DELTA_Y);

        hearts = new ARPGPlayerStatusGUI[MAX_HP];
        createHearts();

        goldDisplay = new ARPGPlayerStatusGUI("zelda/coinsDisplay", new RegionOfInterest(0, 0, 64, 32), 4.5f, 2.25f, false, 0, 0);
        goldCountDisplay = createGoldCount();
        setGoldCount();

        this.state = State.IDLE;
        this.oldState = State.IDLE;
        dontCutGrass = false;
        isJustCreated = true;
        debugMode = false;
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();

        if (!isReading() && !isDead()) {

            for (int i = 0 ; i < MOVEMENT.length ; ++i) {

                final Orientation orientation = Orientation.values()[i];
                final int direction = MOVEMENT[i];
                Button key = keyboard.get(direction);

                /* If the player walks straight ahead without turning
                 * => assign to the field animation, the "i"-th value in the array animations,
                 * where corresponds to the current index of the array Orientation.values()
                 */
                if (isSameDirection(orientation)) {

                    animation = defaultAnimations[i];
                }

                if (key.isDown() && key.isLastPressed() && state == State.IDLE) {
                    moveOrientate(orientation);
                }

            }
            //On tab => change the item
            if (keyboard.get(Keyboard.TAB).isPressed()) {
                toNextItem();
            }

            //on Space => use the Item and animate the usage or set the player to idle state
            if (keyboard.get(Keyboard.SPACE).isPressed()) {
                isJustCreated = false; //When player spawns he have his sword still sheathed
                battleAnimation =
                        RPGSprite.createAnimations(BATTLE_ANIMATION_DURATION, chooseBattleSprites(), false)[getOrientation().ordinal()];
                useItem();

            } else {
                state = State.IDLE;
            }

            battleAnimation.update(deltaTime);

            //if the currentItemStatus don't match the actual current item, resulting in a false icon => update the icon
            if (!currentItemStatus.getSpriteName().equals(getCurrentItem().getSpriteName())) {
                setIcon();
            }

            //update Walking animation
            animationUpdate(deltaTime);

            //Update Gold Count
            setGoldCount();

            if (getHp() <= 0) {
                state = State.DEAD;
            }

            //for testing purposes
            if (keyboard.get(Keyboard.SHIFT).isDown() && (keyboard.get(Keyboard.J).isPressed()) && (keyboard.get(Keyboard.O).isPressed())) {
                if (!debugMode) { debugMode = true; } else { debugMode = false; }
                System.out.println(debugMode);
            }

            if (debugMode) {
                //Makes Makes flameskull, LogMonster, bomb, fire or Zombie Spawn, uppon pressing respectively on K, L, B or F
                if (keyboard.get(Keyboard.K).isPressed()) {
                    getOwnerArea().registerActor(new FlameSkull(getOwnerArea(), Orientation.UP, getFieldOfViewCells().get(0)));
                }

                if (keyboard.get(Keyboard.L).isPressed()) {
                    getOwnerArea().registerActor(new LogMonster(getOwnerArea(), Orientation.UP, getFieldOfViewCells().get(0)));
                }

                if (keyboard.get(Keyboard.B).isPressed()) {
                    getOwnerArea().registerActor(new Bomb(getOwnerArea(), getFieldOfViewCells().get(0)));
                }

                if (keyboard.get(Keyboard.F).isPressed()) {
                    getOwnerArea().registerActor(new FireSpell(getOwnerArea(), getOrientation(), getFieldOfViewCells().get(0), 10));
                }

                if (keyboard.get(Keyboard.Z).isPressed()) {
                    getOwnerArea().registerActor(new Zombie(getOwnerArea(), getOrientation(), getFieldOfViewCells().get(0)));
                }

                if (keyboard.get(Keyboard.I).isPressed()) {
                    inventory.add(ARPGItem.STAFF, 1);
                    inventory.add(ARPGItem.SWORD, 1);
                    inventory.add(ARPGItem.CASTLEKEY, 1);
                    inventory.add(ARPGItem.BOW, 1);
                    inventory.add(ARPGItem.ARROW, 45);
                    inventory.add(ARPGItem.BOMB, 10);

                }
            }
        }
        if (keyboard.get(Keyboard.R).isPressed() && !isReading() && (isDead() || debugMode)) {
            ARPG.setWantsReset(true);
            Area.setWantsReset(true);
        }

        super.update(deltaTime);
    }


    /**
     * Get to the next inventory item, (in a closed circle, if the end is reached => goes back the 1st item)
     */
    private void toNextItem() {

        if (currentItem + 1 < inventory.getSize()) {
            ++currentItem;
            //arrow is not an item that should be used if the player does not have a bow equipped
            //He won't be able to "hold" an arrow
            if (inventory.getItem(currentItem).getName().equals("arrow")) {

                if (currentItem + 1 < inventory.getSize()) {
                    currentItem += 1;
                } else {
                    currentItem = 0;
                }

            }

        } else {
            currentItem = 0;
        }

        setIcon();
    }

    /**
     * Setter used in ARPGInventory to check and handle the limited cases and possible issues/bug that could happen when removing items, if
     * it's the item equipped or not etc... and modify currentItem when those possible issues happens
     */
    protected static void setCurrentItem(char c) {
        if (c == '-') {
            --currentItem;
        } else if (c == '+') {
            ++currentItem;
        } else if (c == '0') {
            currentItem = 0;
        }
    }

    /** @return true if the current state is State.IDLE, false otherwise */
    private boolean isIdle() {
        return state == State.IDLE;
    }

    /** @return true if the player is dead (if state == State.DEAD), false otherwise */
    private boolean isDead() {
        return state == State.DEAD;
    }

    /**
     * Determines what to do (what item to use) based on what the currentItem is Can only use one item at a time because, player can access
     * only one array index at a time (here array of ARPGInventory) And Since the usage of the items is based on the actual current Item,
     * the player won't be able to use / keep using an item if he hasn't equipped it
     */
    private void useItem() {

        if (inventory.getAmount(getCurrentItem()) > 0) {

            switch ((ARPGItem) getCurrentItem()) {

                case BOW:
                    useBow();
                    break;

                case BOMB:
                    useBomb();
                    break;

                case SWORD:
                    useSword();
                    break;

                case STAFF:
                    useStaff();
                    break;

                case CASTLEKEY:
				/* USage of the key must be "automatic" and not depend on
				whether the player has equipped it and wants to use it thus => no
				useCastleKey method */
                default:
                    break;
            }

        }

    }

    /**
     * Creates Bomb and register it in the OwnerArea, when used
     */
    private void useBomb() {

        List<DiscreteCoordinates> list = this.getFieldOfViewCells();
        DiscreteCoordinates cell = list.get(0);

        Bomb bomb = new Bomb(getOwnerArea(), cell);

        if (getOwnerArea().canEnterAreaCells(bomb, this.getNextCurrentCells())) {
            getOwnerArea().registerActor(bomb);
            inventory.remove(ARPGItem.BOMB, 1);

        }

    }

    /**
     * Creates an Arrow and register it in the OwnerArea, when bow is used
     */
    private void useBow() {
        if (isIdle()) {
            state = State.BOW;
        }

        if (inventory.getAmount(ARPGItem.ARROW) > 0) {

            Arrow arrow = new Arrow(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());
            getOwnerArea().registerActor(arrow);
            inventory.remove(ARPGItem.ARROW, 1);
        }
    }

    private void useStaff() {
        if (isIdle()) {
            state = State.STAFF;
        }
        MagicWaterProjectile magicWaterProjectile =
                new MagicWaterProjectile(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates());

        getOwnerArea().registerActor(magicWaterProjectile);
    }

    /**
     * Assign to the player, the damages of a sword, and it's attack type
     */
    private void useSword() {
        if (isIdle()) {
            state = State.SWORD;
        }

        currentAttackType = Vulnerability.PHYSICAL;
        currentDmg = 2;
    }


    /**
     * @return the index currentItem of the List part of the inventory => returns an InventoryItem
     */
    private InventoryItem getCurrentItem() {

        return inventory.getItem(currentItem);
    }

    /**
     * Getter used in ARPGInventory to check and handle the limited cases and possible issues/bug that could happen when removing items, if
     * it's the item equipped or not etc...
     *
     * @return currentItem (int)
     */
    protected static int getCItem() {
        return currentItem;
    }

    /**
     * return true if the item is in inventory / false otherwise
     */
    @Override
    public boolean possess(InventoryItem item) {
        return inventory.isInInventory(item);
    }

    /**
     * Test wether the player needs to re-orientate before moving or not
     */
    private void moveOrientate(Orientation orientation) {

        if (isSameDirection(orientation)) {
            move(ANIMATION_DURATION);
        } else {
            orientate(orientation);
        }

    }

    /**
     * Indicates if the player's current Orientation is the same as the given orientation
     *
     * @param orientation (Orientation)
     * @return true/false
     */
    private boolean isSameDirection(Orientation orientation) {
        return (getOrientation().equals(orientation));
    }

    /**
     * If the player is moving, update the animation to keep the right animation at all times. And if he isn't reset the animation so that
     * he don't have any animation while standing still.
     *
     * @param deltaTime (float)
     */
    private void animationUpdate(float deltaTime) {
        if (isDisplacementOccurs()) {
            animation.update(deltaTime);
        } else {
            animation.reset();
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
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
        Keyboard keyboard = getOwnerArea().getKeyboard();
        Button space = keyboard.get(Keyboard.SPACE);

        dontCutGrass = !(space.isDown() && (getCurrentItem() == ARPGItem.SWORD));

        return ((isIdle() || isReading()) && keyboard.get(Keyboard.E).isPressed()) || (state == State.SWORD && keyboard.get(Keyboard.SPACE).isPressed());

    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public float getHp() {
        return hp;
    }

    @Override
    public void receiveDmg(DmgInteractionVisitor dealer) {
        hp -= dealer.getDmg();

        if (hp > MAX_HP) {
            hp = MAX_HP;
        } else if (hp < 0) {
            hp = 0;
        }

        setHearts();
    }

    @Override
    public boolean takeCellSpace() {
        return true;
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
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }


    @Override
    public void startReading(Readable readable) {
        oldState = state;
        state = State.READING;
        readable.toggleIsBeingRead();
        /*
         * since reading always involves 2 actors, a reader and a readable
         * exemple a sign and the player, the mayor and the player etc...
         *
         * The management of booleans should be handled at the same moment, and thus explains why the reader always call
         * the boolean setters of the readable
         */
    }

    @Override
    public void toNextDialog(Readable readable) {
        readable.toNextDialog();
    }

    @Override
    public void finishReading(Readable readable) {
        state = oldState;
        oldState = State.READING;
        readable.toggleIsBeingRead();
    }


    /**
     * @return true if the player isReading false otherwise
     */
    @Override
    public boolean isReading() {
        return state == state.READING;
    }

    /**
     * Return the right Sprite[][] Arrays, corresponding to the item currentlyEquipped
     *
     * @return (Sprite[][])
     */
    private Sprite[][] chooseBattleSprites() {

        switch ((ARPGItem) getCurrentItem()) {
            case SWORD:
                return battleSwordSprites;

            case BOW:
                return battleBowSprites;

            case STAFF:
                return battleStaffSprites;

            default:
                return defaultSprites;
        }
    }

    /**
     * Return the right Sprite[] Arrays, corresponding to the item currentlyEquipped
     *
     * @return (Sprite[])
     */
    private Sprite[] chooseGardeSprites() {
        switch ((ARPGItem) getCurrentItem()) {
            case SWORD:
                return gardSwordSprites;

            case BOW:
                return gardBowSprites;

            case STAFF:
                return gardStaffSprites;

            default:
                return defaultGardSprites;
        }
    }


    /**
     * COnception because there are a lot of monsters, Player should always be "en garde", Thus, he should always be drawn with a weapon
     * "unsheathed". He has the first frame of animation not update if he's not attacking, if he attacks the animation starts to be updated
     */
    @Override
    public void draw(Canvas canvas) {

        if (isDead()) {
            graveSprite.draw(canvas);
            new RPGSprite("addedSprites/deathScreenRF", 13, 5, this, new RegionOfInterest(0, 0, 1350, 300), new Vector(-6.5f, -1.5f), 0.6f,
                          3005).draw(canvas);

        } else {
            gearStatus.draw(canvas);
            currentItemStatus.draw(canvas);

            for (ARPGPlayerStatusGUI heart : hearts) {
                heart.draw(canvas);
            }

            goldDisplay.draw(canvas);

            for (ARPGPlayerStatusGUI goldDigit : goldCountDisplay) {
                goldDigit.draw(canvas);
            }

            if (isJustCreated) {
                animation.draw(canvas);

            } else if ((!isDisplacementOccurs() && !isIdle()) || (!battleAnimation.isCompleted())) {
                battleAnimation.draw(canvas);

            } else if (!isDisplacementOccurs()) {
                chooseGardeSprites()[getOrientation().ordinal()].draw(canvas);

            } else {
                animation.draw(canvas);
            }
        }
    }

    /**
     * Sets the item icon to match the item currently used
     */
    private void setIcon() {
        currentItemStatus.setSpriteName(getCurrentItem().getSpriteName());
    }

    /**
     * Instantiates of the hearts
     */
    private void createHearts() {

        for (int i = 0 ; i < MAX_HP ; ++i) {
            hearts[i] = new ARPGPlayerStatusGUI("zelda/heartDisplay", new RegionOfInterest(32, 0, ICON_SIZE, ICON_SIZE), ICON_SCALE, true,
                                                1.5f + i * ICON_SCALE, ICON_DELTA_Y);
        }

    }

    /**
     * Used to set the hearts' sprites to match the remaining hp
     */
    private void setHearts() {

        int heartIndex = (int) hp;

        for (int i = 0 ; i < heartIndex ; ++i) {
            hearts[i].setROIX(32);
        }

        if (hp - Math.floor(hp) > 0) {
            hearts[heartIndex].setROIX(16);
            ++heartIndex;
        }

        for (int i = heartIndex ; i < MAX_HP ; ++i) {
            hearts[i].setROIX(0);
        }
    }

    /**
     * Creates the RegionOfInterest for each digit and returns them into an array
     */
    public static RegionOfInterest[] createDigitsROI() {
        RegionOfInterest[] digitsROI = new RegionOfInterest[10];
        int digitSize = 16;

        //adding the 0's ROI
        digitsROI[0] = new RegionOfInterest(digitSize, 2 * digitSize, digitSize, digitSize);

        //adding the ROIs of the digits 1 to 8
        int index = 1;
        for (int i = 0 ; i < 2 ; ++i) {
            for (int j = 0 ; j < 4 ; ++j) {
                digitsROI[index] = new RegionOfInterest(digitSize * j, digitSize * i, digitSize, digitSize);
                ++index;
            }
        }

        //adding the 9's ROI
        digitsROI[index] = new RegionOfInterest(0, digitSize * 2, digitSize, digitSize);
        return digitsROI;
    }

    /**
     * @return array containing the initial digits for the goldCount
     */
    private ARPGPlayerStatusGUI[] createGoldCount() {
        ARPGPlayerStatusGUI[] goldCount = new ARPGPlayerStatusGUI[3];
        for (int i = 0 ; i < 3 ; ++i) {
            goldCount[i] = new ARPGPlayerStatusGUI("zelda/digits", DIGITS_ROI[0], ICON_SCALE, false, 1.7f + i * ICON_SCALE, 0.77f);
        }
        return goldCount;
    }

    private void setGoldCount() {
        int money = inventory.getMoney();
        int hundreds = money / 100;
        int tens = (money - hundreds * 100) / 10;
        int units = money - hundreds * 100 - tens * 10;

        goldCountDisplay[0].setROI(DIGITS_ROI[hundreds]);
        goldCountDisplay[1].setROI(DIGITS_ROI[tens]);
        goldCountDisplay[2].setROI(DIGITS_ROI[units]);
    }

    @Override
    public float getDmg() {
        return (getOwnerArea().getKeyboard().
                get(Keyboard.SPACE).isDown()) ? currentDmg : 0;
    }

    @Override
    public Vulnerability getAttackType() {
        return currentAttackType;
    }


    /*********** Inner Private Class ARPGPlayerHandler of ARPGPlayer, handling interaction on the most "specific" level  **********/

    private class ARPGPlayerHandler implements ARPGInteractionVisitor {

        @Override
        public void interactWith(Door door) {
            setIsPassingADoor(door);
        }

        @Override
        public void interactWith(Grass grass) {
            if (!dontCutGrass) {
                grass.setIsCut();
            }
        }

        @Override
        public void interactWith(Coin coin) {
            inventory.addMoney(Coin.VALUE);
            coin.collect();
        }

        @Override
        public void interactWith(Heart heart) {
            heart.collect();
        }

        @Override
        public void interactWith(Bomb bomb) {
            bomb.setExplode();
        }

        @Override
        public void interactWith(Monster monster) {
            monster.receiveDmg(ARPGPlayer.this);
        }

        @Override
        public void interactWith(CastleKey key) {
            key.addKey(inventory);
            key.collect();
        }

        @Override
        public void interactWith(Lever lever) {
            if (lever.getSignalState()) {
                lever.setStateOf();
            } else {
                lever.setStateOn();
            }
        }

        public void interactWith(ARPGMayor mayor) {
            if (!mayor.getIsDone()) {

                if (mayor.getIsBeingRead() && isReading()) {

                    ARPGPlayer.this.toNextDialog(mayor);
                }

            } else if (isReading()) {
                //If player choose pathA resolve pathA and same for pathB
                if (mayor.getCurrentPath() == 1) {
                    ARPGPlayer.this.inventory.addMoney(mayor.pathAResolution());
                } else if (mayor.getCurrentPath() == 2) {
                    mayor.pathBResolution(ARPGPlayer.this);
                }

                ARPGPlayer.this.finishReading(mayor);


            }
        }

        @Override
        public void interactWith(ARPGSign sign) {
            if (!isReading()) {
                ARPGPlayer.this.startReading(sign);
            } else if (sign.getIsDone() && sign.getIsBeingRead()) {
                ARPGPlayer.this.finishReading(sign);
            }
        }

        @Override
        public void interactWith(Box box) {
            box.open();
        }

        @Override
        public void interactWith(CollectableItem item) {
            item.addItem(inventory);
            item.collect();
        }

        @Override
        public void interactWith(CastleDoor castleDoor) {

            //if player is in front of castleDoor and  it's closed opens it, if he has the key
            if (!castleDoor.isOpen() && wantsViewInteraction() && possess(ARPGItem.CASTLEKEY)) {
                castleDoor.setIsOpen();
            }

            //If player is "on" Castledoor and it's open, enters and closes it
            if (castleDoor.isOpen() && castleDoor.isCellInteractable() && wantsCellInteraction()) {
                setIsPassingADoor(castleDoor);
                castleDoor.setIsClosed();
            }

            //Once it's Opened passes through it (allow the update of the sprite to be door.open because
            //if player enters directly in the area castle we can't see the changement in the sprite of castleDoor.
            //But here player enters at the next "update" loop so that CastleDoor have the time to update it's sprite

        }

    }

    /** Inner Enum for ARPGPlayer's state */
    private enum State {
        IDLE,
        SWORD,
        BOW,
        STAFF,
        DEAD,
        READING,
        PAUSE
    }

}