package com.noradltd.wumpus;

import java.util.stream.Collectors;

class Hunter extends Room.Occupant {

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
    private Quiver quiver;

    Hunter() {
        this.quiver = NULL_QUIVER;
    }

    Hunter(Quiver quiver) {
        this.quiver = quiver;
    }

    // TODO only used in testing, can we drop this?
    public boolean canShoot() {
        return !quiver.isEmpty();
    }

    public Integer kills() {
        return kills;
    }

    public void shoot(Integer exitNumber) {
        if (validExitNumber(exitNumber)) {
            Room target = getRoom().exits().get(exitNumber);
            Arrow arrow = quiver.next();
            Logger.info("Your arrow hurtles down tunnel " + (exitNumber + 1));
            if (target.occupants().stream().anyMatch(Wumpus.class::isInstance)) {
                Logger.info("There is a Wumpus in the room!");
            } else {
                Logger.info("There is no Wumpus there");
            }
            arrow.moveTo(target);
            if (arrow.killedAWumpus()) {
                kills++;
            }
        } else {
            Logger.info("You can't shoot that way");
        }
    }

    public void moveTo(Integer exitNumber) {
        if (validExitNumber(exitNumber)) {
            super.moveTo(getRoom().exits().get(exitNumber));
        } else {
            Logger.debug("invalid exitNumber (" + exitNumber + ") in Hunter.moveTo()");
        }
    }

    private boolean validExitNumber(int exitNumber) {
        final int limit = getRoom().exits().size() - 1;
        if (exitNumber < 0 || exitNumber > limit) {
            Logger.error("Invalid Choice: Pick from 1 to " + (limit + 1));
            return false;
        }
        return true;
    }

    protected void kill(Wumpus wumpus) {
        if (!isDead() && !wumpus.isDead() && getRoom().equals(wumpus.getRoom())) {
            if (Random.getRandomizer().nextBoolean()) {
                Logger.info("With a slash of your knife you eviscerate a Wumpus; it's corpse slides to the floor");
                wumpus.die();
                kills += 1;
            } else {
                Logger.info("You slash you knife at the Wumpus as it's slimy tentacles wrap around you, trying to crushing the life out of your body");
                wumpus.respondTo(this);
            }
        } else {
            if (isDead() && !wumpus.isDead()) {
                Logger.info("The Wumpus escapes your violent assault");
            }
        }

    }

    void takeArrow() {
        getRoom().occupants().stream()
                .filter(occupant -> occupant instanceof Arrow).collect(Collectors.toList())
                .forEach(occupant -> {
                    if (occupant instanceof Arrow) {
                        Arrow arrow = (Arrow) occupant;
                        if (arrow.isBroken()) {
                            Logger.info("The broken arrow crumbles in your hand.");
                        } else {
                            arrow.getRoom().remove(arrow);
                            Logger.info("You collect an unbroken arrow off the floor.");
                            quiver.add(arrow);
                        }
                    }
                });
    }

    @Override
    public void respondTo(Room.Occupant interloper) {
        if (interloper instanceof Wumpus) {
            Wumpus wumpus = (Wumpus) interloper;
            kill(wumpus);
        }
    }

    @Override
    public String describe() {
        if (isDead()) {  // TODO test
            return "You smell the mouldering of a corpse";
        }
        return "You sense the presence of death";
    }

    public String inventory() {
        return "Inventory:\n\tArrows: " +
                quiver.arrowsRemaining() +
                "\n\tWumpus Scalps: " + kills() +
                "\n";
    }

    public String toString() {
        if (isDead()) {
            return "The corpse of an unfortunate soul lies here";
        }
        return "A genuine specimen of Wumpus murdering prowess";
    }
}

