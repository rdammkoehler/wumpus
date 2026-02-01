package com.noradltd.wumpus;

import java.io.Serial;

class Hunter extends Room.Occupant {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void initInteractions() {
        super.initInteractions();
        interactions.put(Wumpus.class, this::kill);
    }

    interface Quiver {
        boolean isEmpty();

        Arrow next();

        String arrowsRemaining();

        void add(Arrow arrow);
    }

    private static final Quiver NULL_QUIVER = new Quiver() {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Arrow next() {
            return Arrow.NULL_ARROW;
        }

        @Override
        public String arrowsRemaining() {
            return "0";
        }

        @Override
        public void add(Arrow arrow) {
        }
    };

    private Integer kills = 0;
    private final Quiver quiver;

    Hunter() {
        quiver = NULL_QUIVER;
    }

    Hunter(Quiver quiver) {
        this.quiver = quiver;
    }

    Integer kills() {
        return kills;
    }

    void shoot(Integer exitNumber) {
        if (validExitNumber(exitNumber)) {
            // TODO: Law of Demeter Principle violation.
            // This logic should be moved to a method on the Room class,
            // such as `getExit(exitNumber)`.
            // A similar issue exists in the `moveTo` method.
            Room target = getRoom().exits().get(exitNumber);
            Arrow arrow = quiver.next();
            if (arrow == Arrow.NULL_ARROW) {
                Logger.info("You have no more arrows");
            } else {
                Logger.info("Your arrow hurtles down tunnel " + (exitNumber + 1));
                // TODO: Law of Demeter Principle violation.
                // This logic should be moved to a method on the Room class,
                // such as `hasWumpus()`.
                if (target.occupants().stream().anyMatch(Wumpus.class::isInstance)) {
                    Logger.info("There is a Wumpus in the room!");
                } else {
                    Logger.info("There is no Wumpus there");
                }
                arrow.moveTo(target);
                if (arrow.killedAWumpus()) {
                    kills++;
                }
            }
        } else {
            Logger.info("You can't shoot that way");
        }
    }

    void moveTo(Integer exitNumber) {
        if (validExitNumber(exitNumber)) {
            super.moveTo(getRoom().exits().get(exitNumber));
        } else {
            Logger.debug("invalid exitNumber (" + exitNumber + ") in Hunter.moveTo()");
        }
    }

    private boolean validExitNumber(int exitNumber) {
        // TODO: This method mixes validation with UI concerns (logging).
        // It should only be responsible for validation and return a boolean.
        // The caller should handle logging.
        int limit = getRoom().exits().size() - 1;
        if (exitNumber < 0 || exitNumber > limit) {
            Logger.error("Invalid Choice: Pick from 1 to " + (limit + 1));
            return false;
        }
        return true;
    }

    void kill(Room.Occupant wumpus) {
        // TODO: Law of Demeter Principle violation.
        // This logic should be moved to a method on the Occupant class,
        // such as `isInSameRoomAs(Occupant other)`.
        if (getRoom().equals(wumpus.getRoom())) {
            // need to check that the wumpus has not fled, hence the room check
            if (Random.getRandomizer().nextBoolean()) {
                Logger.info("With a slash of your knife you eviscerate a Wumpus; it's corpse slides to the floor");
                wumpus.die();
                kills += 1;
            } else {
                Logger.info("You slash you knife at the Wumpus as it's slimy tentacles wrap around you, trying to crush the life out of your body");
                wumpus.respondTo(this);
            }
        }
    }

    void takeArrow() {
        // TODO: Law of Demeter Principle violation.
        // This logic should be moved to a method on the Room class,
        // such as `findArrow()`.
        getRoom().occupants().forEach(occupant -> {
            if (occupant instanceof Arrow arrow) {
                if (arrow.isBroken()) {
                    Logger.info("The broken arrow crumbles in your hand.");
                } else {
                    arrow.addToQuiver(quiver);
                }
            }
        });
    }

    @Override
    public String describe() {
        if (isDead()) {  // TODO test
            return "You smell the mouldering of a corpse";
        }
        return "You sense the presence of death";
    }

    String inventory() {
        return "Inventory:\n\tArrows: " +
                quiver.arrowsRemaining() +
                "\n\tWumpus Scalps: " + kills() +
                "\n";
    }

    @Override
    public String toString() {
        if (isDead()) {
            return "the corpse of an unfortunate soul";
        }
        return "a genuine specimen of Wumpus murdering prowess";
    }

}
