package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.rpg.actor.Dialog;

 interface Readable extends Interactable {
	
	/**
	 * quit dialog by calling resetting it to the 1st part
	 */
	void quitDialog();

	 /**
	  * Increment, goes to next to dialog ( the following)
	  */
	 void toNextDialog();

	 /**
	  * @return true if the readable is being read, false otherWise
	  */
	 boolean getIsBeingRead();

	 /**
	  * Method that the reader calls to set the state isBeingRead and isFinished of the readable
	  */
	void toggleIsBeingRead();

	 /**
	  * @return True if the readable has nothing more to say (but can still return true if the player hasn't quit
	  * the dialog but the reader has nothing more to say)
	  */
	 boolean getIsDone();

	/**
	 * "create a dialog where (String dialog) contains the names of the file to be used
	 * @param dialog (String) Text to read seperated in small part to be readable in the "reading window"
	 * @param area (Area) : area to display the dialogs
	 */
	 Dialog createDialog(String dialog, Area area);
}
