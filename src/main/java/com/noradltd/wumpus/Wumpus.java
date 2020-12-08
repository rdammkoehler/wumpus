package com.noradltd.wumpus;

class Wumpus extends Occupier implements Room.Occupant {

    private Boolean fed = Boolean.FALSE;

    protected void die() {
        super.die();
        if (getRoom() != null) {
            getRoom().remove(this);
        }
    }

    public Boolean isFed() {
        return fed;
    }

    private void flee(Hunter hunter) {
        int exitCount = getRoom().exits().size();
        if (exitCount > 0) {
            Room exitRoom = getRoom().exits().get(Random.getRandomizer().nextInt(exitCount));
            moveTo(exitRoom);
        } else {
            eat(hunter);
        }
    }

    private void eat(Hunter hunter) {
        hunter.die();
        fed = Boolean.TRUE;
    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Hunter.class.isAssignableFrom(visitor.getClass())) {
            Hunter hunter = (Hunter) visitor;
            // The original game gave 0.75 chance to move and 0.25 to stay
            // if the Wumpus and the Hunter are in the same room after that,
            // the wumpus eats the hunter. Is this logic more or less the same (without the weighted chance?)
            if (Random.getRandomizer().nextBoolean()) {
                eat(hunter);
            } else {
                flee(hunter);
            }
        }
    }
}
