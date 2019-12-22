package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.ARPGSign;
import ch.epfl.cs107.play.game.arpg.actor.Bridge;
import ch.epfl.cs107.play.game.arpg.actor.Grass;
import ch.epfl.cs107.play.game.arpg.actor.Orb;
import ch.epfl.cs107.play.game.arpg.actor.Waterfall;
import ch.epfl.cs107.play.game.arpg.actor.monster.FlameSkull;
import ch.epfl.cs107.play.game.arpg.actor.monster.LogMonster;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class Route extends ARPGArea {

    /**
     * To what area leads the door ?
     */
    final String[] destinations = {"zelda/Ferme", "zelda/Village", "zelda/RouteChateau", "zelda/RouteTemple"};

    /**
     * Where do you arrive, once you  passed the door
     */
    final DiscreteCoordinates[] arrivCoords = {new DiscreteCoordinates(18, 15),
            new DiscreteCoordinates(29, 18), new DiscreteCoordinates(9, 1), new DiscreteCoordinates(1, 5)};


    /**
     * Where is the door in the area (usually 2 coordinates : mainCords and otherCords)
     */
    final DiscreteCoordinates[] mainCoords = {new DiscreteCoordinates(0, 15),
            new DiscreteCoordinates(9, 0), new DiscreteCoordinates(10, 19), new DiscreteCoordinates(19, 10)};

    final DiscreteCoordinates[] otherCoords = {new DiscreteCoordinates(0, 16),
            new DiscreteCoordinates(10, 0), new DiscreteCoordinates(9, 19), new DiscreteCoordinates(19, 11)};


    /**
     * Orientation once you've entered the new area
     */
    final Orientation[] orientations = {Orientation.UP, Orientation.DOWN, Orientation.UP, Orientation.RIGHT};


    private Orb orb = new Orb(this, Orientation.UP, new DiscreteCoordinates(19, 8));
    private Bridge bridge = new Bridge(this, Orientation.UP, new DiscreteCoordinates(16, 10));

    @Override
    public void update(float deltaTime) {

        if (orb.getSignalState()) {
            bridge.setConstruct(true);
        } else {
            bridge.setConstruct(false);
        }
        super.update(deltaTime);
    }

    @Override
    public void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        //Register Doors
        for (int i = 0; i < 4; i++) {
            registerActor(new Door(destinations[i], arrivCoords[i], Logic.TRUE,
                    this, orientations[i], mainCoords[i], otherCoords[i]));
        }

        //Register Grass
        for (int i = 5; i <= 7; ++i) {
            for (int j = 6; j <= 11; ++j) {
                registerActor(new Grass(this, Orientation.UP, new DiscreteCoordinates(i, j)));
            }
        }

        //Register Waterfall
        registerActor(new Waterfall(this, new DiscreteCoordinates(13, 5)));

        //Register Orb
        registerActor(orb);

        //Register Bridge
        registerActor(bridge);

        //Register ARPGSign
        registerActor(new ARPGSign("hint", this, Orientation.RIGHT, new DiscreteCoordinates(15, 7) ));

        //Register LogMonsters
        for (int i = 0 ; i < 2 ; i++){
            registerActor(new LogMonster(this, Orientation.UP, new DiscreteCoordinates(9 + 2*i, 8+i)));
        }

        //Register FlameSkull
        for (int i = 0 ; i < 5 ; i++){
            registerActor(new FlameSkull(this, Orientation.UP, new DiscreteCoordinates(4 + 2 * i, 10+ i)));
        }
    }


    @Override
    public String getTitle() {
        return "zelda/Route";
    }

}