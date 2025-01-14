package com.noradltd.wumpus;

class Wumpus extends Room.Occupant {
    {
        interactions.put(Hunter.class, interloper -> {
            if (interloper.isAlive()) {
                if (Random.getRandomizer().nextBoolean()) {
                    eat(interloper);
                } else {
                    flee();
                }
            }
        });
    }

    private void flee() {
        int exitCount = getRoom().exits().size();
        Logger.debug("exit count: " + exitCount);
        Room exitRoom = getRoom().exits().get(Random.getRandomizer().nextInt(exitCount));
        moveTo(exitRoom);
        Logger.info("The startled Wumpus runs away!");
    }

    private void eat(Room.Occupant hunter) {
        if (hunter.getRoom().equals(getRoom())) {
            Logger.info("Nom Nom Nom, a Wumpus has eaten you.");
            hunter.die();
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
