package com.noradltd.wumpus;

class Wumpus extends Room.Occupant {

    private void flee(Hunter hunter) {
        int exitCount = getRoom().exits().size();
        if (exitCount > 0) {  // TODO this should never be possible, minimum maze size is 2 isn't it?
            Room exitRoom = getRoom().exits().get(Random.getRandomizer().nextInt(exitCount));
            moveTo(exitRoom);
            Logger.info("The startled Wumpus runs away!");
        } else {
            eat(hunter);
        }
    }

    private void eat(Hunter hunter) {
        if (hunter.getRoom().equals(getRoom())) {
            Logger.info("Nom Nom Nom, a Wumpus has eaten you.");
            hunter.die();
        }
    }

    @Override
    public void respondTo(Room.Occupant interloper) {
        if (!isDead() && !interloper.isDead()) {
            if (interloper instanceof Hunter hunter) {
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

    @Override
    public String toString() {
        if (isDead()) {
            return "the rotting corpse of a Wumpus that has made the floor slick with ichor";
        }
        return "a horrifying mass of tentacles, suckers, and eyes, that writhes before you";
    }
}
