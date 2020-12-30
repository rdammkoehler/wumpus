package com.noradltd.wumpus;

class Hunter extends Room.Occupant {
    interface Quiver {
        boolean isEmpty();

        Arrow next();

        String arrowsRemaining();
    }

    private Integer kills = 0;
    private Quiver quiver;

    Hunter() {
        this.quiver = new Quiver() {
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
        };
    }

    Hunter(Quiver quiver) {
        this.quiver = quiver;
    }

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
            Logger.error("invalid exitNumber (" + exitNumber +") in Hunter.moveTo()");
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
        if (getRoom().equals(wumpus.getRoom())) {
            if (Random.getRandomizer().nextBoolean()) {
                Logger.info("With a slash of your knife you eviscerate a Wumpus; it's corpse slides to the floor");
                wumpus.die();
                kills += 1;
            } else {
                Logger.info("You slash you knife at the Wumpus as it's slimy tenticales wrap around you, crushing the life out of your body");
                wumpus.respondTo(this);
            }
        } else {
            Logger.info("The Wumpus escapes your violent assault");  // TODO this means the wumpus fled from you!
        }

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

