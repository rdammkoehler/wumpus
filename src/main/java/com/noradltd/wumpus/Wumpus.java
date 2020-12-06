package com.noradltd.wumpus;

import java.util.Random;

class Wumpus extends Occupier implements Room.Occupant {
    private boolean fed = false;

    public Boolean isFed() {
        return fed;
    }

    public Wumpus flee() {
        Room exitRoom = getRoom().exits().get(new Random().nextInt(getRoom().exits().size()));
        moveTo(exitRoom);
        return this;
    }

    public Wumpus eat(Hunter hunter) {
        hunter.die();
        fed = true;
        return this;
    }
}
