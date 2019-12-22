package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.areagame.actor.Interactor;

public interface Reader extends Interactor{
	
	void startReading(Readable readable);
	
	void toNextDialog(Readable readable);
	
	void finishReading(Readable readable);
	
	boolean getIsReading();
	
}
