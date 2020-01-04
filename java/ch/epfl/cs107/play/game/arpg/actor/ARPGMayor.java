package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.monster.Zombie;
import ch.epfl.cs107.play.game.arpg.handler.ARPGInteractionVisitor;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;
import ch.epfl.cs107.play.game.rpg.actor.RPGSprite;
import ch.epfl.cs107.play.io.XMLTexts;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ARPGMayor extends MovableAreaEntity implements Readable, Interactor {
	public static final String[] tblAnswers = {"mayor_answer1", "mayor_answer2"} ;

	public static final String[] path0 = {"mayor_halt", "mayor_welcome", "mayor_welcome1", "mayor_welcome2", "mayor_welcome3", 
			"mayor_welcome4", "mayor_choice"};
	/** 
	 * First message is just the mayor yelling "adventurer!" because we want the discussion to happen only
	 * the player and the mayor or in adjacents cells (face to face) so since the mayor can "see" the player from afar
	 * he calls him/stop him but the player can't continue the dialog because it's the reader that calls the methode to
	 * go to the next dialog. And here the reader is the player who can interact with the mayor (and makes the dialog continue)
	 * only if he's in his fieldOfView wich is as regural 1 cell in front of him => only if is face to face with mayor
	 * */

	public static final String[] pathA = {tblAnswers[0]} ;

	public static final String[] pathB  = {tblAnswers[1]} ;

	public static final String[][] paths = {path0, pathA, pathB} ;

	public int maxLength;;
	public final int REACH = 4;
	private int nbCellsReached;

	private boolean askState;
	private boolean isChoosed;

	private Sprite[][] sprites;
	private Animation[] animations;
	private Animation animation;
	private boolean isTalking;
	private boolean isFinished;

	private Dialog mayorDialog;
	private int currentDialog;
	private int currentPath;

	private ARPGMayorInteractionHandler handler;

	/**
	 * Constructor for the AreaEntity ARPGMayor (mayor of the town)
	 * @param owner (Area)
	 * @param orientation (Orientation)
	 * @param position (DiscreteCoordinates)
	 */
	public ARPGMayor(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position);

		handler = new ARPGMayorInteractionHandler();

		Orientation[] orientations = {Orientation.DOWN, Orientation.LEFT, Orientation.RIGHT, Orientation.UP};

		sprites = RPGSprite.extractSprites("addedSprites/mayor", 4, 1.5f, 1.80f, this, 34, 32, orientations );
		animations = RPGSprite.createAnimations(5, sprites);

		maxLength =  (path0.length - 1);
		currentDialog = 0;
		mayorDialog = createDialog(path0[currentDialog], owner);


		isTalking = false;
		isFinished = false;
		askState = false;
		isChoosed = false;
		currentPath = 0;
		nbCellsReached = 0;
	}

	@Override
	public void update(float deltaTime) {

		movement();

		if (!isDisplacementOccurs()) {
			animation.reset();
		} else {	
			animation.update(deltaTime);
		}


		if (isTalking) {
			mayorDialog.resetDialog(XMLTexts.getText(paths[currentPath][currentDialog]));
		}

		askState = path0[currentDialog].equals("mayor_choice");

		makeChoice();
		super.update(deltaTime);
	}


	/**
	 * quit dialog by calling mayorDialog.resetDialog(pathA[0]), 
	 * where tbl Keys is the array with all dialogs.
	 * also set currentDialog to 0 and isTalking to false
	 */
	@Override
	public void quitDialog() {
		mayorDialog.resetDialog(XMLTexts.getText(paths[currentPath][0]));
		currentDialog = 0;
	}

	@Override
	public Dialog createDialog(String dialog, Area area) {
		return new Dialog(XMLTexts.getText(dialog), "zelda/dialog", area);
	}

	@Override
	public void toNextDialog() {
		if(!askState) { 
			currentDialog++; 
		}

	}

	@Override
	public boolean getIsBeingRead() {
		return isTalking;
	}

	@Override
	public void toggleIsBeingRead() {
		if(isTalking) {
			isTalking = false;
			isFinished = true;
			quitDialog();

		} else {
			isTalking = true;
			isFinished = false;
		}
	}
	//because its reader that sets when he has finished reading,
	//we want to know if readable is done and have the condition isTalking to true
	//because if its not to true then reader has already quit the dialog
	@Override
	public boolean getIsDone() {
		return isTalking && (currentDialog >= maxLength) ;
	}

	private void movement() {
		if(nbCellsReached < REACH-1) {

			if(isTargetReached() && isDisplacementOccurs()) {
				++nbCellsReached ; 
			}
			move(5);
			animation = animations[getOrientation().ordinal()];

		} else {
			this.abortCurrentMove();
		}
	}

	/**
	 * Enables the test on both a and b keys to allow the "registration" of the answer to a dialog question 
	 * if ARPGMayor is in AskState
	 */
	private void makeChoice() {
		Keyboard keyboard = getOwnerArea().getKeyboard();

		//If has not chosen and mayor in in ask state
		if(!isChoosed && askState) {
			if(keyboard.get(Keyboard.A).isPressed()) { 
				//can Answers by tapping A or B
				isChoosed = true ;
				newPath(pathA, 1);
				//goes to the new path (String[]) corresponding to paths[currentPath] (String[][])
				askState = false;
			}

			else if (keyboard.get(Keyboard.B).isPressed()) { 
				isChoosed = true ;
				newPath(pathB, 2);
				//goes to the new path (String[]) corresponding to paths[currentPath] (String[][])
				askState = false;

			}
		}
	}

	/**
	 * Resets the fields and constants to prepare the use of a new "Path" of Dialog
	 * @param newPath (String[]) : Path of dialog to be given
	 * @param index (int) : index / position of newPath in the arrays of paths "paths"
	 */
	private void newPath(String[] newPath, int index) {
		currentPath = index;
		currentDialog = 0;
		maxLength = newPath.length - 1;
	}

	/**
	 * The series of events that should happen if pathA is chosen
	 * @return rewardMoney (int)
	 */
	protected int pathAResolution() {
		//consist of a modification of the inventory (money)
		// => will be made in the interactWith(mayor) of ARPGPlayer because
		//Inventory must stay private
		int rewardMoney = 400;
		return rewardMoney;
	}

	/**
	 * The series of events that should happen if pathB is chosen
	 */
	protected void pathBResolution(ARPGPlayer player) {
		this.getOwnerArea().unregisterActor(this);
		this.getOwnerArea().registerActor(new Zombie(this.getOwnerArea(), Orientation.DOWN, player.getFieldOfViewCells().get(0)));
	}

	/**
	 * @return the currentPath (int)
	 */
	protected int getCurrentPath() {
		return currentPath;
	}

	@Override
	public void draw(Canvas canvas) {

		animation.draw(canvas);

		if(isTalking) {
			mayorDialog.draw(canvas);
		}
	}

	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList
				(getCurrentMainCellCoordinates()) ;
	}

	@Override
	public boolean takeCellSpace() {
	return  !(currentPath == 2);
	}

	@Override
	public boolean isCellInteractable() {
		return false;
	}


	@Override
	public void acceptInteraction(AreaInteractionVisitor v) {
		((ARPGInteractionVisitor) v).interactWith(this);
	}

	@Override
	public void interactWith(Interactable other) {
		other.acceptInteraction(handler); 
	}

	/**
	 * @return List containing the field of view cells corresponding to the Mayor field of view where he can "see" RANGE cells
	 * in front of him
	 */
	private List<DiscreteCoordinates> viewCells() {

		List<DiscreteCoordinates> cells = new ArrayList<DiscreteCoordinates>();
		cells.add(getCurrentMainCellCoordinates().jump((Orientation.DOWN).toVector()));

		for (int i = 1 ; i <= REACH ; ++i) {
			cells.add(cells.get(i-1).jump(Orientation.DOWN.toVector()));
		}

		return cells;
	}

	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return viewCells();
	}

	@Override
	public boolean wantsCellInteraction() {
		return false;
	}

	@Override
	public boolean isViewInteractable() {
		return !askState ;
	}

	@Override
	public boolean wantsViewInteraction() {

		return !isFinished ;
	}


	/** Inner class ARPGMayorInteractionHandler **/

	private class ARPGMayorInteractionHandler implements ARPGInteractionVisitor {

		@Override
		public void interactWith(Reader reader) {
			reader.startReading(ARPGMayor.this);
		}

		public void interactWith(ARPGPlayer player) {
			if(!player.getIsReading()) {
				player.startReading(ARPGMayor.this);
				System.out.println("mayor hasInteracted");
			}
		}
	}
}


//Block the player when he has started reading and the mayor is talking
//			//If the player is still reading and the mayor have finished talking  => unblock player
//			

