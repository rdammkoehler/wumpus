package com.noradltd.wumpus;

class Hunter extends Occupier implements Room.Occupant {
    interface Quiver {
        boolean isEmpty();

        Arrow next();// TODO what is Arrow, generically speaking? Projectile? Weapon? What do Quiver's contain?
    }

    private Integer kills = 0;
    private Quiver quiver;

    Hunter() {
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
        if (canShoot()) {
            Arrow arrow = quiver.next();
            Room target = getRoom().exits().get(exitNumber);
            arrow.moveTo(target);
        }
    }

    protected void kill(Wumpus wumpus) {
        wumpus.die();
        kills += 1;
    }

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Wumpus.class.isAssignableFrom(visitor.getClass())) {
            Wumpus wumpus = (Wumpus) visitor;
            kill(wumpus);
        }
    }
}

