package com.noradltd.wumpus;

class Hunter extends Occupier implements Room.Occupant {

    private Integer kills = 0;

    public Integer kills() {
        return kills;
    }

    protected void die() {
        super.die();
        if (getRoom() != null) {
            getRoom().remove(this);
        }
    }

    protected void kill(Wumpus wumpus) {
        wumpus.die();
        kills += 1;
    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Wumpus.class.isAssignableFrom(visitor.getClass())) {
            Wumpus wumpus = (Wumpus) visitor;
            kill(wumpus);
        }
    }
}
