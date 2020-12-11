package com.noradltd.wumpus;

public class Arrow extends Occupier implements Room.Occupant {
    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Wumpus.class.isInstance(visitor)) {
            Wumpus wumpus = (Wumpus) visitor;
            wumpus.die();
        }
    }
}
