package ch.epfl.cs107.play.game.rpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayerStatusGUI;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Player is a Specific RPG actor
 */
public abstract class Player extends MovableAreaEntity implements Interactor {

    private boolean isPassingADoor;
    private Door passedDoor;
    private String mapSpriteName;
    private static final String beginPath = "../miniMap/";
    private Area oldArea;

    /**
     * Default Player constructor
     * @param area (Area): Owner Area, not null
     * @param orientation (Orientation): Initial player orientation, not null
     * @param coordinates (DiscreteCoordinates): Initial position, not null
     */
    public Player(Area area, Orientation orientation, DiscreteCoordinates coordinates) {
        super(area, orientation, coordinates);
        passedDoor = null;
        isPassingADoor = false;
        mapSpriteName = beginPath + area.getTitle();
    }

    protected String getMapSpriteName() {
        return mapSpriteName;
    }

    /**
     * Leave an area by unregister this player
     */
    public void leaveArea() {
        oldArea = getOwnerArea();
        getOwnerArea().unregisterActor(this);
    }

    /**
     * @param area (Area): initial area, not null
     * @param position (DiscreteCoordinates) : initial position, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        area.setViewCandidate(this);

        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        updateMap(area);
        resetDoorStates();
        resetMotion();
    }

    protected Area getOldArea() {
        return oldArea;
    }

    /**
     * Reset the door state
     */
    private void resetDoorStates() {
        passedDoor = null;
        isPassingADoor = false;
    }

    /**
     * Update the mini map based on the Area
     * @param area (Area) Area towards where the player is currently heading
     */
    private void updateMap(Area area) {
        mapSpriteName = beginPath + area.getTitle();
    }

    /// Getter and setter for interaction

    /**
     * Indicate the player just passed a door
     * @param door (Door): the door passed, not null
     */
    protected void setIsPassingADoor(Door door) {
        this.passedDoor = door;
        isPassingADoor = true;
    }

    /** @return (boolean): true if the player is passing a door */
    public boolean isPassingADoor() {
        return isPassingADoor;
    }

    /**
     * Getter of the passing door
     * @return (Door)
     */
    public Door getPassedDoor() {
        return passedDoor;
    }


    /** Abstract Inner Class RedDot */
    protected abstract class RedDot extends MovableAreaEntity {

        /**
         * RedDot constructor
         * @param area (Area): Owner area. Not null
         * @param orientation (Orientation): Initial orientation of the entity. Not null
         * @param position (DiscreteCoordinates): Initial position of the entity. Not null
         */
        protected RedDot(Area area, Orientation orientation, DiscreteCoordinates position) {
            super(area, orientation, position);

        }

        @Override
        public boolean takeCellSpace() {
            return false;
        }

        @Override
        public boolean isCellInteractable() {
            return false;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v) {
        }

        /** Used to register the Actor RedDot on the miniMap at Player's instantiation */
        protected abstract void register();

        /** Used to unregister the Actor RedDot when the Area changes */
        protected abstract void unregister();

    }

}

