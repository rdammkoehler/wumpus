package com.noradltd.wumpus;

class Wumpus extends Room.Occupant {

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
                System.out.println("The startled Wumpus runs away! (" + exitRoom.number() + ")");
            } else {
                eat(hunter);
            }
    }

    private void eat(Hunter hunter) {
        System.out.println("Nom Nom Nom, the Wumpus has eaten you.");
        hunter.die();
        fed = Boolean.TRUE;
    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (!isDead()) {
            if (Hunter.class.isAssignableFrom(visitor.getClass())) {
                Hunter hunter = (Hunter) visitor;
                if (Random.getRandomizer().nextBoolean()) {
                    eat(hunter);
                } else {
                    flee(hunter);
                }
            }
        }
    }

    @Override
    public String describe() {
        return "You smell something foul";
    }

    public String toString() {
        if (isDead()) {
            return "The rotting corpse of a Wumpus has made the floor slick with ichor";
        }
        return "A horrifying mass of tentacles, suckers, and eyes, writhes before you";
    }
}
