package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ARPGPlayerStatusGUI implements Graphics {
	private static final float DEPTH = 1000.f;
 	
	private float depth;
	private String spriteName;
	private RegionOfInterest spriteRegion;
	private float scaleWidth;
	private float scaleHeight;
	private float deltaX;
	private float deltaY;
	private boolean isUp;
	private ImageGraphics display;
	
	/**
	 * Constructor of Status Graphics UI for ARPGPlayer
	 * @param spriteName (String)
	 * @param roi (RegionOfInterest)
	 * @param scaleWidth (float)
	 * @param scaleHeight (float)
	 * @param isUp (boolean)
	 * @param deltaX (float)
	 * @param deltaY (float)
	 * @param depth (float)
	 */
	protected ARPGPlayerStatusGUI(String spriteName, RegionOfInterest roi, float scaleWidth,
			float scaleHeight, boolean isUp, float deltaX, float deltaY, float depth) {
		
		this.spriteName = spriteName;
		spriteRegion = roi;
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.isUp = isUp;
		this.depth = depth;
	}
	
	/**
	 * Constructor of Status Graphics UI for ARPGPlayer
	 * @param spriteName (String)
	 * @param roi (RegionOfInterest)
	 * @param scaleWidth (float)
	 * @param scaleHeight (float)
	 * @param isUp (boolean)
	 * @param deltaX (float)
	 * @param deltaY (float)
	 */
	protected ARPGPlayerStatusGUI(String spriteName, RegionOfInterest roi, float scaleWidth,
			float scaleHeight, boolean isUp, float deltaX, float deltaY) {
		
		this(spriteName, roi, scaleWidth, scaleHeight, isUp, deltaX, deltaY, DEPTH);
	}
	
	/**
	 * Constructor of Status Graphics UI for ARPGPlayer (for Squared objects)
	 * scaleWidth = scaleHeight
	 * @param spriteName (String)
	 * @param roi (RegionOfInterest)
	 * @param scale (float)
	 * @param isUp (boolean)
	 * @param deltaX (float)
	 * @param deltaY (float)
	 */
	protected ARPGPlayerStatusGUI(String spriteName, RegionOfInterest roi, float scale,
			boolean isUp, float deltaX, float deltaY) {
		
		this(spriteName, roi, scale, scale, isUp, deltaX, deltaY);
	}
	
	
	//======================================
	
	/**
	 * Set the name of the sprites
	 * (Change the Sprites)
	 * @param name (String)
	 */
	public void setSpriteName(String name) {
		spriteName = name;
	}
	
	/**
	 * Set the RegionOfInterest
	 * @param region (RegionOfInterest)
	 */
	public void setROI(RegionOfInterest region) {
		spriteRegion = region;
	}
	
	/**
	 * Set the RegionOfInterest
	 * @param x (int)
	 * @param y (int)
	 */
	public void setROI(int x, int y) {
		spriteRegion.x = x;
		spriteRegion.y = y;
	}
	
	/**
	 * Set the x coordinate of the RegionOfInterest
	 * @param x (int)
	 */
	public void setROIX(int x) {
		spriteRegion.x = x;
	}

	/**
	 * Set the y coordinate of the RegionOfInterest
	 * @param y (int)
	 */
	public void setROIY(int y) {
		spriteRegion.y = y;
	}
	
	/**
	 * Getter for the name of the Sprite
	 * @return spriteName
	 */
	public String getSpriteName() {
		return spriteName;
	}
	
	@Override
	public void draw(Canvas canvas) {
		float width = canvas.getScaledWidth();
		float height = canvas.getScaledHeight();
		
		Vector anchor = canvas.getTransform().getOrigin().sub(new
		Vector(width/2, height/2));
		
		float posY = deltaY;
		
		if (isUp) {
			posY += height;
		}
		
		display = new ImageGraphics(ResourcePath.getSprite(spriteName),
		scaleWidth, scaleHeight, spriteRegion,
		anchor.add(new Vector(deltaX, posY)), 1, depth);
		display.draw(canvas);
	}

	
}
