package com.noradltd.wumpus;

class Hunter extends Occupier implements Room.Occupant {
    private boolean dead = false;

    public Boolean isDead() {
        return dead;
    }

    public void die() {
        dead = true;
    }
}
