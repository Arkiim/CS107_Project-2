package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.game.rpg.actor.Sign;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGSign extends Sign implements Readable {
    /*
     Signs only have one dialog to show up so no need for arrays or
      count currentDialog etc...
     */
    private boolean isFinished;
    private Sprite sprite;
    private Dialog dialog;
    private boolean isBeingRead;
    public boolean test;

    /**
     * Default Sign constructor
     *
     * @param textMessage (String): Text message of the panel, not null
     * @param area        (Area): Owner area, not null
     * @param orientation (Orientation): Initial orientation of the entity, not null
     * @param position    (DiscreteCoordinate): Initial position of the entity, not null
     */
    public ARPGSign(String textMessage, Area area, Orientation orientation, DiscreteCoordinates position) {
        super(textMessage, area, orientation, position);
        sprite = new RPGSprite("addedSprites/Sign", 1, 1, this,
                new RegionOfInterest(0, 0, 17, 32), new Vector(0, 0), 1.f, -10);

        this.dialog = new Dialog(XMLTexts.getText(textMessage), "zelda/dialog", getOwnerArea());
        isBeingRead = false;
        isFinished = false;
    }

    /**
     * quit dialog by calling resetting it to the 1st part
     */
    @Override
    public void quitDialog() {

    }

    @Override
    public void toNextDialog() {

    }

    @Override
    public boolean getIsBeingRead() {
        return isBeingRead;
    }

    @Override
    public void toggleIsBeingRead() {
        if (isBeingRead) {
            isBeingRead = false;
            isFinished = true;
        } else {
            isBeingRead = true;
            isFinished = false;
        }
    }

    @Override
    public boolean getIsDone() {
        return true;
    }

    /**
     * "create a dialog where (String dialog) contains the names of the file to be used
     *
     * @param dialog (String) Text to read seperated in small part to be readable in the "reading window"
     * @param area   (Area) : area to display the dialogs
     */
    @Override
    public Dialog createDialog(String dialog, Area area) {
        return null;
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
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
        if (getIsBeingRead()) {
            dialog.draw(canvas);
        }
    }
}
