package ch.epfl.cs107.play.game.arpg.actor;

import java.util.Collections;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;

public class Box extends LogicalObject {

    Sprite[] sprites;

    CollectableAreaEntity[] content;

    boolean isOpen;

    /**
     * Box constructor
     * @param area
     * @param position
     * @param items (CollectableAreaEntity[]
     */
    public Box(Area area, DiscreteCoordinates position, CollectableAreaEntity[] items) {
        super(area, Orientation.DOWN, position);

        sprites = new Sprite[2];
        sprites[0] = new RPGSprite("addedSprites/chest", 1, 1, this, new RegionOfInterest(0,0,16,16));
        sprites[1] = new RPGSprite("addedSprites/chest", 1, 1, this, new RegionOfInterest(64,0,16,16));

        isOpen = false;

        content = items;

    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return !isOpen;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ARPGInteractionVisitor) v).interactWith(this);
    }

    @Override
    public void draw(Canvas canvas) {
        (isOpen ? sprites[1] : sprites[0]).draw(canvas);

    }

    /**
     * release the content of the chest in a line
     * starting just below the box
     */
    private void releaseContent() {
        for (int i = 0 ; i < content.length ; ++i) {
            DiscreteCoordinates position = getCurrentMainCellCoordinates().jump(i+1, 0);
            if(getOwnerArea().canEnterAreaCells(content[i],
                    Collections.singletonList(position))) {

                content[i].setCollectablePosition(position.x, position.y);
                getOwnerArea().registerActor(content[i]);

            }

        }

    }

    /**
     * set the box open and release its content
     */
    public void open() {
        if(getSignalState()) {
            isOpen = true;
            releaseContent();
        }
    }

    /**
     * close the box, allows it to interact and release content again
     */
    public void close() {
        isOpen = false;
    }



}
