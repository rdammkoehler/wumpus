package com.noradltd.wumpus;

class Hunter extends Occupier implements Room.Occupant {
    private boolean dead = false;

    public Boolean isDead() {
        return dead;
    }

    public void die() {
        dead = true;
        if (getRoom() != null ) {
            getRoom().remove(this);  // TODO untested experiment
        }
    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Wumpus.class.isAssignableFrom(visitor.getClass())) {
            Wumpus wumpus = (Wumpus) visitor;
            System.out.println("AHHHAHAHKKKKK ! A Wumpus!");
        }
    }
}
