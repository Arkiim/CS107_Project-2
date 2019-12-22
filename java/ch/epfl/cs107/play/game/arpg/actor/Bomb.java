package ch.epfl.cs107.play.game.arpg.actor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.game.actor.TextGraphics;
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
import ch.epfl.cs107.play.game.arpg.damage.DmgInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.damage.MonsterAttacker;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Bomb extends AreaEntity implements Interactor, DmgInteractionVisitor, MonsterAttacker {

    private final static int ANIMATION_DURATION = 5;
    private static final float DAMAGE = 4;
    private final static Vulnerability ATTACK_TYPE = Vulnerability.PHYSICAL;

    private Sprite sprites[];
    private Sprite explosionSprites[];

    private int countDown;

    private boolean isExploding; //State of explosion

    private Animation animation;
    private Animation explosion;

    private BombInteractionHandler handler;

    private List<DamageReceiver> dmgVictims;

    private TextGraphics countText;

    /**
     * Bomb constructor, the orientation is always by convention Orientatoin.UP because it doesn't have any effect on the bomb's behavior
     * and therefore would make the code too verbous
     *
     * @param area Area
     * @param position DiscreteCoordinates
     */
    public Bomb(Area area, DiscreteCoordinates position) {
        super(area, Orientation.UP, position);

        countDown = 80;
        isExploding = false;

        sprites = createSprites("zelda/bomb", 0, 2, 16, 16, 1, 1, 0.6f);
        setSpritesAnchor(sprites, 0, 0.3f);
        explosionSprites = createSprites("zelda/explosion", 0, 6, 32, 32, 4, 4, 1.f);
        setSpritesAnchor(explosionSprites, -1.5f, -1.2f);
        //countSprites = createSprites("zelda/digits", );

        animation = new Animation(ANIMATION_DURATION, sprites, true);
        explosion = new Animation(ANIMATION_DURATION / 2, explosionSprites, false);

        handler = new BombInteractionHandler();

        dmgVictims = new ArrayList<DamageReceiver>();

    }

    /**
     * Update the text that displays on the bomb
     */
    private void updateCountText() {
        int countVal = countDown / 8;

        countText = new TextGraphics(Integer.toString(countVal), 0.5f, ((countVal > 4) ? Color.CYAN : Color.RED));
        countText.setDepth(0.01f);
        countText.setAnchor(this.getPosition().add(0.36f, 0.51f));
        countText.setBold(true);
    }


    @Override
    public void update(float deltaTime) {
        //bomb's life duration
        --countDown;

        if (countDown <= 0) {
            explode();
        }

        animation.update(deltaTime);
        updateCountText();
    }

    /**
     * explode the bomb and start the animation
     */
    private void explode() {
        isExploding = true;
        animation = explosion;
    }

    @Override
    public boolean takeCellSpace() {
        return !isExploding;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * used by other entities to make the bomb explode
     */
    public void setExplode() {
        countDown = 0;
        explode();
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
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        //gets all the surrounding cells' coordinates
        List<DiscreteCoordinates> viewCoord = new ArrayList<DiscreteCoordinates>();
        for (int i = -1 ; i < 2 ; ++i) {
            for (int j = -1 ; j < 2 ; ++j) {
                if (i != 0 || j != 0) {
                    viewCoord.add(getCurrentMainCellCoordinates().jump(i, j));
                }
            }
        }
        return viewCoord;
    }

    @Override
    public float getDmg() {
        return DAMAGE;
    }

    /**
     * adds a damageReceiver to the dmgVictims list to avoid the bomb to stack damage on it
     *
     * @param victim DamageReceiver
     */
    private void damageDealt(DamageReceiver victim) {
        dmgVictims.add(victim);
    }

    /**
     * used to determine if damage should be dealt to a potential victim (if so, then false is returned)
     *
     * @param victim DamageReceiver
     * @return true (victim is in dmgVictims), false (otherwise)
     */
    private boolean wasDamageDealt(DamageReceiver victim) {
        return dmgVictims.contains(victim);
    }

    @Override
    public Vulnerability getAttackType() {
        return ATTACK_TYPE;
    }

    @Override
    public boolean wantsCellInteraction() {
        return this.isExploding;
    }

    @Override
    public boolean wantsViewInteraction() {
        return this.isExploding;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    @Override
    public void draw(Canvas canvas) {

        if (animation.isCompleted()) {
            getOwnerArea().unregisterActor(this);

        } else {
            animation.draw(canvas);

        }
        if (!isExploding) {
            countText.draw(canvas);
        }

    }


    		/** Private Inner Class BombInteractionHandler **/

    private class BombInteractionHandler implements ARPGInteractionVisitor {

        @Override
        public void interactWith(Grass grass) {
            grass.setIsCut();
        }

        @Override
        public void interactWith(ARPGPlayer player) {
            if (!wasDamageDealt(player)) {
                player.receiveDmg(Bomb.this);
                damageDealt(player);
            }
        }

        @Override
        public void interactWith(Monster monster) {
            if (getAttackType().isInVulnerabilityArray(monster.getVulnerabilities()) && !wasDamageDealt(monster)) {
                monster.receiveDmg(Bomb.this);
                damageDealt(monster);
            }
        }

    }

}