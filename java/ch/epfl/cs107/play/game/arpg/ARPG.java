package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.ARPGPlayer;
import ch.epfl.cs107.play.game.arpg.area.Chateau;
import ch.epfl.cs107.play.game.arpg.area.Ferme;
import ch.epfl.cs107.play.game.arpg.area.GrotteDonjon;
import ch.epfl.cs107.play.game.arpg.area.House;
import ch.epfl.cs107.play.game.arpg.area.Route;
import ch.epfl.cs107.play.game.arpg.area.RouteChateau;
import ch.epfl.cs107.play.game.arpg.area.RouteTemple;
import ch.epfl.cs107.play.game.arpg.area.Temple;
import ch.epfl.cs107.play.game.arpg.area.Village;
import ch.epfl.cs107.play.game.rpg.RPG;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ARPG extends RPG{ 

	private final static String areas[] = {"zelda/Village"};
	private final DiscreteCoordinates positions[] = {new DiscreteCoordinates(17,5)};//, new DiscreteCoordinates(6,7)
	private static int areaIndex = 0;
	private static boolean wantsReset=false;

	/**
	 * Add all the areas
	 */
	private void createAreas() {
		addArea(new Village());
		addArea(new Ferme());
		addArea(new Route());
		addArea(new House());
		addArea(new RouteChateau());
		addArea(new Chateau());
		addArea(new RouteTemple());
		addArea(new Temple());
		addArea(new GrotteDonjon());
	}

	@Override
	public void end(){};

	@Override
	public void update(float deltaTime){
		if(wantsReset){
			initPlayer(new ARPGPlayer(setCurrentArea(areas[areaIndex], true), Orientation.UP, positions[areaIndex]));
			wantsReset=false;
		}

		super.update(deltaTime);

	}

	public static void setWantsReset(boolean b){
		wantsReset = b;
	}

	@Override
	public String getTitle(){return "Gandalf the Red : Javassassinate the DarkLord";}

	@Override
	public boolean begin(Window window, FileSystem fileSystem) {
		if (super.begin(window, fileSystem)) {
			createAreas();
			initPlayer(new ARPGPlayer(setCurrentArea(areas[areaIndex], true), Orientation.UP, positions[areaIndex]));
			return true; 

		} else { 
			return false;
		}
	}
}