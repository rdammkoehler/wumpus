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
                return null;
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
            arrow.moveTo(target);
            if (arrow.killedAWumpus()) {
                kills++;
            }
        } else {
            System.out.println("You can't shoot that way");
        }
    }

    public void moveTo(Integer exitNumber) {
        if (validExitNumber(exitNumber)) {
            super.moveTo(getRoom().exits().get(exitNumber));
        } else {
            System.err.println("WTF?");
        }
    }

    private boolean validExitNumber(int exitNumber) {
        final int limit = getRoom().exits().size() - 1;
        if (exitNumber < 0 || exitNumber > limit) {
            System.out.println("Invalid Choice: Pick from 1 to " + (limit + 1));
            return false;
        }
        return true;
    }

    protected void kill(Wumpus wumpus) {
        if (getRoom().equals(wumpus.getRoom())) {
            System.out.println("With a slash of your knife you eviscerate a Wumpus; it's corpse slides to the floor");
            wumpus.die();
            kills += 1;
        } else {
            System.out.println("The Wumpus escapes your violent assault");
        }

    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Wumpus.class.isAssignableFrom(visitor.getClass())) {
            Wumpus wumpus = (Wumpus) visitor;
            kill(wumpus);
        }
    }

    @Override
    public String describe() {
        return "You sense the presence of death";
    }

    public void inventory() {
        System.out.println(new StringBuilder()
                .append("Inventory:\n\tArrows: ")
                .append(quiver.arrowsRemaining())
                .append("\n\tWumpus Scalps: " + kills())
                .append("\n")
                .toString());
    }

    public String toString() {
        if (isDead()) {
            return "The corpse of an unfortunate soul lies here";
        }
        return "A genuine specimen of Wumpus murdering prowess";
    }
}

