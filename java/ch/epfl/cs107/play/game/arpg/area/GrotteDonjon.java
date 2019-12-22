package ch.epfl.cs107.play.game.arpg.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.arpg.actor.ARPGItem;
import ch.epfl.cs107.play.game.arpg.actor.Box;
import ch.epfl.cs107.play.game.arpg.actor.CollectableItem;
import ch.epfl.cs107.play.game.arpg.actor.Lever;
import ch.epfl.cs107.play.game.arpg.actor.Teleporter;
import ch.epfl.cs107.play.game.arpg.actor.monster.Zombie;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

public class GrotteDonjon extends ARPGArea {

    private Lever lever = new Lever(this, Orientation.UP, new DiscreteCoordinates(9, 3));

    private Teleporter teleporter =
            new Teleporter("zelda/Temple", new DiscreteCoordinates(4, 3), this, Orientation.UP, new DiscreteCoordinates(8, 3));

    private Zombie[] zombies = {new Zombie(this, Orientation.UP, new DiscreteCoordinates(7, 8)),
            new Zombie(this, Orientation.DOWN, new DiscreteCoordinates(8, 8)),
            new Zombie(this, Orientation.LEFT, new DiscreteCoordinates(9, 5)),
            new Zombie(this, Orientation.RIGHT, new DiscreteCoordinates(6, 5)),
            new Zombie(this, Orientation.RIGHT, new DiscreteCoordinates(7, 10))};

    private Box staffChest = new Box(this, new DiscreteCoordinates(9, 7), new CollectableItem[]{
            new CollectableItem(this, new DiscreteCoordinates(8, 5), ARPGItem.STAFF, 1)});

    public void update(float deltaTime) {

        if (lever.getSignalState()) {
            teleporter.setSignal(Logic.TRUE);
        } else {
            teleporter.setSignal(Logic.FALSE);
        }

        if (isZombiesDead() && !exists(staffChest)) {
            staffChest.setStateOn();
            registerActor(staffChest);
        }

        super.update(deltaTime);
    }

    private boolean isZombiesDead() {
        for (Zombie z : zombies) {
            if (z.getHp() != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void createArea() {
        registerActor(new Background(this));

        registerActor(teleporter);

        registerActor(lever);

        for (Zombie z : zombies) {
            registerActor(z);
        }


    }

    @Override
    public String getTitle() {
        return "zelda/GrotteDonjon";
    }

}
