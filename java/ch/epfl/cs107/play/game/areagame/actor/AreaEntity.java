package ch.epfl.cs107.play.game.areagame.actor;

import java.util.List;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;


/**
 * Area Entities are assigned to at least one Area Cell which make them Interactable
 */
public abstract class AreaEntity extends Entity implements Interactable {

    /// AreaEntity are disposed inside an Area
    private Area ownerArea;
    /// Orientation in the Area
    private Orientation orientation;
    /// Coordinate of the main Cell linked to the entity
    private DiscreteCoordinates currentMainCellCoordinates;

    /**
     * Default AreaEntity constructor
     *
     * @param area (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity in the Area. Not null
     * @param position (DiscreteCoordinate): Initial position of the entity in the Area. Not null
     */
    public AreaEntity(Area area, Orientation orientation, DiscreteCoordinates position) {

        super(position.toVector());

        if (area == null) {
            throw new NullPointerException();
        }

        this.ownerArea = area;
        this.orientation = orientation;
        this.currentMainCellCoordinates = position;
    }

    /**
     * Getter for the owner area
     *
     * @return (Area)
     */
    protected Area getOwnerArea() {
        return ownerArea;
    }

    /**
     * Set the owner area with new value
     *
     * @param newArea (Area): the new value. Not null
     */
    protected void setOwnerArea(Area newArea) {
        this.ownerArea = newArea;
    }

    /**
     * Getter for the orientation
     *
     * @return (Orientation): current orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Orientate the AreaEntity to a new orientation
     *
     * @param orientation (Orientation): The new orientation. Not null
     * @return (boolean): if the orientation change happens, by default always true
     */
    protected boolean orientate(Orientation orientation) {
        this.orientation = orientation;
        return true;
    }

    /**
     * Getter for the coordinates of the main cell occupied by the AreaEntity
     *
     * @return (DiscreteCoordinates)
     */
    protected DiscreteCoordinates getCurrentMainCellCoordinates() {
        return currentMainCellCoordinates;
    }

    /**
     * Tell if the mouse is over any of the currentCells of the entity
     *
     * @return (boolean)
     */
    protected boolean isMouseOver() {
        List<DiscreteCoordinates> cells = getCurrentCells();
        DiscreteCoordinates mouseCoordinate = ownerArea.getRelativeMouseCoordinates();
        for (DiscreteCoordinates cell : cells) {
            if (cell.equals(mouseCoordinate)) {
                return true;
            }
        }
        return false;
    }

    /// AreaEntity extends Entity

    @Override
    protected void setCurrentPosition(Vector v) {
        // When updating the current position, also check if we need to update the main cell coordinates
        if (DiscreteCoordinates.isCoordinates(v)) {
            this.currentMainCellCoordinates = new DiscreteCoordinates(Math.round(v.x), Math.round(v.y));
            v = v.round();
        }
        super.setCurrentPosition(v);
    }

    //Useful sprites methods added so to extend the possibilities of the RPGSprite methods

    /**
     * Creates an array of sprites used to animate a monster
     *
     * @param spriteName String - name of the file containing the sprite image
     * @param rowIndex int - index of the row where the sprites are located in the image
     * @param nbSprites int - number of sprites in the array
     * @param spriteWidth int
     * @param spriteHeight int
     * @param gameWidth float - width of the monster's graphic representation ingame
     * @param gameHeight float - height of the monster's graphic representation ingame
     * @param depth float
     * @return 1-dimensional array of sprites
     */
    public Sprite[] createSprites(String spriteName, int rowIndex, int nbSprites, int spriteWidth, int spriteHeight, float gameWidth,
                                  float gameHeight, float depth) {
        Sprite[] sprites = new Sprite[nbSprites];

        for (int i = 0 ; i < sprites.length ; ++i) {
            sprites[i] = new RPGSprite(spriteName, gameWidth, gameHeight, this,
                                       new RegionOfInterest(i * spriteWidth, rowIndex * spriteHeight, spriteWidth, spriteHeight),
                                       new Vector(0, 0), 1, depth);
        }

        return sprites;
    }

    /**
     * Creates an array of sprites used to animate a monster
     *
     * @param spriteName String - name of the file containing the sprite image
     * @param rowIndex int - index of the row where the sprites are located in the image
     * @param nbSprites int - number of sprites in the array
     * @param spriteWidth int
     * @param spriteHeight int
     * @param gameWidth float - width of the monster's graphic representation ingame
     * @param gameHeight float - height of the monster's graphic representation ingame
     * @return 1-dimensional array of sprites
     */
    public Sprite[] createSprites(String spriteName, int rowIndex, int nbSprites, int spriteWidth, int spriteHeight, float gameWidth,
                                  float gameHeight) {
        return createSprites(spriteName, rowIndex, nbSprites, spriteWidth, spriteHeight, gameWidth, gameHeight, 0.5f);
    }

    /**
     * Creates an array of sprites used to animate an AreaEntity
     *
     * @param spriteName String - name of the file containing the sprite image
     * @param columnIndex int - index of the row where the sprites are located in the image
     * @param nbSprites int - number of sprites in the array
     * @param spriteWidth int
     * @param spriteHeight int
     * @param gameWidth float - width of the monster's graphic representation ingame
     * @param gameHeight float - height of the monster's graphic representation ingame
     * @return 1-dimensional array of sprites
     */
    public Sprite[] createSpritesColumn(String spriteName, int columnIndex, int nbSprites, int spriteWidth, int spriteHeight, float gameWidth,
                                        float gameHeight) {
        Sprite[] sprites = new Sprite[nbSprites];

        for (int i = 0 ; i < sprites.length ; ++i) {
            sprites[i] = new RPGSprite(spriteName, gameWidth, gameHeight, this,
                                       new RegionOfInterest(columnIndex * spriteWidth, i * spriteHeight, spriteWidth, spriteHeight),
                                       new Vector(0, 0), 1, 0.5f);
        }

        return sprites;
    }

    /**
     * * Creates an array of sprites and orders it, according to the Orientation array given in arguments
     *
     * @param spriteName String
     * @param columnIndex int
     * @param nbSprites int
     * @param spriteWidth int
     * @param spriteHeight int
     * @param gameWidth float
     * @param gameHeight float
     * @param orientations Orientation
     * @return 1-dimensional array of sprites
     */
    public Sprite[] createSpritesColumn(String spriteName, int columnIndex, int nbSprites, int spriteWidth, int spriteHeight, float gameWidth,
                                        float gameHeight, Orientation[] orientations) {

        Sprite[] sprites = createSpritesColumn(spriteName, columnIndex, nbSprites, spriteWidth, spriteHeight, gameWidth, gameHeight);

        Sprite[] returnSprites = new Sprite[sprites.length];
        for (int i = 0 ; i < 4 ; ++i) {
            returnSprites[orientations[i].ordinal()] = sprites[i];
        }

        return returnSprites;
    }

    /**
     * Set the anchor for the array of sprite
     *
     * @param sprite (Sprite[])
     * @param deltaX (float)
     * @param deltaY (float)
     */
    public static void setAnchor(Sprite[] sprite, float deltaX, float deltaY) {
        for (Sprite i : sprite) {
            i.setAnchor(new Vector(deltaX, deltaY));
        }
    }


    /**
     * Set the anchor for a multidimension array of sprite
     *
     * @param sprite (Sprite[][])
     * @param deltaX (float)
     * @param deltaY (float)
     */
    public static void setAnchor(Sprite[][] sprite, float deltaX, float deltaY) {
        for (Sprite[] i : sprite) {

            for (Sprite j : i) {
                j.setAnchor(new Vector(deltaX, deltaY));

            }
        }
    }

    /**
     * Set the anchor for an array of Animations
     *
     * @param animations (Animation[])
     * @param deltaX (float)
     * @param deltaY (float)
     */
    public static void setAnchor(Animation[] animations, float deltaX, float deltaY) {
        for (Animation a : animations) {
            a.setAnchor(new Vector(deltaX, deltaY));
        }

    }


    /**
     * Creates a 2-dimensional array of sprites used to animate a monster
     *
     * @param spriteName (String) - name of the file containing the sprite image
     * @param nbRow (int) - number of 1-dimensional sprite arrays
     * @param spritesPerRow (int) - number of sprites per array
     * @param spriteWidth (int)
     * @param spriteHeight (int)
     * @param gameWidth (float) - width of the monster's graphic representation ingame
     * @param gameHeight (float) - height of the monster's graphic representation ingame
     * @return 2-dimensional array of sprites
     */
    public Sprite[][] createSpritesArrays(String spriteName, int nbRow, int spritesPerRow, int spriteWidth, int spriteHeight, float gameWidth,
                                          float gameHeight) {
        Sprite[][] spritesArrays = new Sprite[nbRow][spritesPerRow];
        for (int i = 0 ; i < spritesArrays.length ; ++i) {
            spritesArrays[i] = createSprites(spriteName, i, spritesPerRow, spriteWidth, spriteHeight, gameWidth, gameHeight);
        }
        return spritesArrays;
    }

    /**
     * Creates an array of sprites used to animate a monster taking a space of a single cell
     *
     * @param spriteName (String) - name of the file containing the sprite image
     * @param nbSprites (int) - number of sprites in the array
     * @param spriteSize (int)
     * @return 1-dimensional array of sprites
     */
    public Sprite[] createSprites(String spriteName, int nbSprites, int spriteSize) {
        return createSprites(spriteName, 0, nbSprites, spriteSize, spriteSize, 1, 1);
    }

    /**
     * sets the anchor of all sprites in a 2-dimensional array
     *
     * @param sprites (Sprite[][]) 1-dimensional
     * @param deltaX (float)
     * @param deltaY (float)
     */
    public void setSpriteArraysAnchor(Sprite[][] sprites, float deltaX, float deltaY) {
        for (Sprite[] sprite : sprites) {
            setSpritesAnchor(sprite, deltaX, deltaY);
        }
    }

    /**
     * sets the anchor of all sprites in a 1-dimensional array
     *
     * @param sprites (Sprite)
     * @param deltaX (float)
     * @param deltaY (float)
     */
    public void setSpritesAnchor(Sprite[] sprites, float deltaX, float deltaY) {
        for (Sprite sprite : sprites) {
            sprite.setAnchor(new Vector(deltaX, deltaY));
        }
    }

    //Useful animation methods

    /**
     * creates an animation array from a 2-dimensional array
     *
     * @param duration (int)
     * @param sprites (Sprite)
     * @param repeat (boolean)
     * @return animation array
     */
    protected static Animation[] createAnimationArray(int duration, Sprite[][] sprites, boolean repeat) {
        Animation[] anims = new Animation[sprites.length];
        for (int i = 0 ; i < sprites.length ; ++i) {
            anims[i] = new Animation(duration, sprites[i], repeat);
        }
        return anims;
    }

    /**
     * creates a looping animation array from a 2-dimensional array
     *
     * @param duration (int)
     * @param sprites (Sprite)
     * @return animation array
     */
    protected static Animation[] createAnimationArray(int duration, Sprite[][] sprites) {
        return createAnimationArray(duration, sprites, true);
    }

    /**
     * makes a copy of a sprite array
     *
     * @param sprites (Sprite)
     * @return copy of sprite
     */
    public static Sprite[] spriteArrayCopy(Sprite[] sprites) {
        Sprite[] copy = new Sprite[sprites.length];
        System.arraycopy(sprites, 0, copy, 0, sprites.length);
        return copy;
    }

}
