package com.noradltd.wumpus;

class Wumpus extends Room.Occupant {

    @Override
    protected void respondTo(Hunter hunter) {
        Logger.debug("Wumpus is responding to Hunter");
        if (Random.getRandomizer().nextBoolean()) {
            eat(hunter);
        } else {
            startle();
        }
    }

    private void startle() {
        Logger.info("The startled Wumpus runs away!");
        flee();
    }

    void flee() {
        moveTo(getRoom().getRandomExit());
    }

    private void eat(Hunter hunter) {
        Logger.info("Nom Nom Nom, a Wumpus has eaten you.");
        hunter.die();
    }

    @Override
    public String describe() {
        return "You smell something foul";
    }

    @Override
    public String toString() {
        if (isDead()) {
            return "the rotting corpse of a Wumpus that has made the floor slick with ichor";
        }
        return "a horrifying mass of tentacles, suckers, and eyes, that writhes before you";
    }
}
