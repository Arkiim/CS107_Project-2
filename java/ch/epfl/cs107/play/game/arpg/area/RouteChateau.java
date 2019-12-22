package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.CastleDoor;
import ch.epfl.cs107.play.game.arpg.actor.monster.DarkLord;
import ch.epfl.cs107.play.game.arpg.actor.monster.Zombie;
import ch.epfl.cs107.play.game.rpg.actor.Door;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class RouteChateau extends ARPGArea {

    @Override
    protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));

        //Register Door
        registerActor(new Door("zelda/Route", new DiscreteCoordinates(9, 18), Logic.TRUE,
                this, Orientation.DOWN, new DiscreteCoordinates(9, 0), new DiscreteCoordinates(10, 0)));

        //Register CastleDoor
        registerActor(new CastleDoor("zelda/Chateau", new DiscreteCoordinates(7, 3), Logic.FALSE,
                this, Orientation.UP, new DiscreteCoordinates(9, 13), new DiscreteCoordinates(10, 13)));

        //Register DarkLord
        registerActor(new DarkLord(this, Orientation.DOWN, new DiscreteCoordinates(9, 9)));

        //Register Zombies
        for (int i = 0 ; i < 4 ; i++) {
            registerActor(new Zombie(this, Orientation.DOWN, new DiscreteCoordinates(8 + 2 * i, 4 + 2 * i)));
        }
    }

    @Override
    public String getTitle() {
        return "zelda/RouteChateau";
    }

}