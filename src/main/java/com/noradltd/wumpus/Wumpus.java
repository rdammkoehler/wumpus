package com.noradltd.wumpus;

class Wumpus extends Occupier implements Room.Occupant {

    private boolean fed = false;

    public Boolean isFed() {
        return fed;
    }

    protected Wumpus flee(Hunter hunter) {
        int exitCount = getRoom().exits().size();
        if (exitCount > 0) {
            Room exitRoom = getRoom().exits().get(randomizer.nextInt(exitCount));
            moveTo(exitRoom);
        } else {
            eat(hunter);
        }
        return this;
    }

    protected Wumpus eat(Hunter hunter) {
        hunter.die();
        fed = true;
        return this;
    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Hunter.class.isAssignableFrom(visitor.getClass())) {
            Hunter hunter = (Hunter) visitor;
            // The original game gave 0.75 chance to move and 0.25 to stay
            // if the Wumpus and the Hunter are in the same room after that,
            // the wumpus eats the hunter. Is this logic more or less the same (without the weighted chance?)
            if (randomizer.nextBoolean()) {
                eat(hunter);
            } else {
                flee(hunter);
            }
        }
    }
}
