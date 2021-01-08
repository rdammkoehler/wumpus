package com.noradltd.wumpus;

import java.util.Arrays;
import java.util.Optional;

public class Arrow extends Room.Occupant {
    static final Arrow NULL_ARROW = new Arrow() {
        @Override
        protected void respondTo(Wumpus wumpus) {
        }

        @Override
        public String toString() {
            return "this arrow does nothing";
        }
    };

    private boolean killedAWumpus = false;
    private boolean justLayingAbout = false;

    @Override
    protected void respondTo(Wumpus wumpus) {
        if (!justLayingAbout) {
            wumpus.die();
            killedAWumpus = true;
            Logger.info("Your arrow drives itself deep into the hideous beast; it's life force escaping from the hole in it's leathery hide");
            die();
        }
    }

    @Override
    public void moveTo(Room newRoom) {
        super.moveTo(newRoom);
        if (isInFlight()) {
            if (Random.getRandomizer().nextBoolean()) {
                Logger.info("You hear the sharp crack of your arrow as it splitters against the cave wall");
                die();
            } else {
                Logger.info("You hear a clattering sound in the distance");
                justLayingAbout = true;
            }
        }
        scareNearByWumpi(newRoom);
    }

    private boolean isInFlight() {
        return !isBroken() && !justLayingAbout;
    }

    private void scareNearByWumpi(Room room) {
        Arrays.stream(room.exits().stream()
                .flatMap(exit -> exit.occupants().stream())
                .filter(Wumpus.class::isInstance)
                .toArray(Wumpus[]::new)
        ).forEach(Wumpus::flee);
    }

    public boolean isBroken() {
        return isDead();
    }

    public boolean killedAWumpus() {
        return killedAWumpus;
    }

    @Override
    public String describe() {
        return "--arrows should not be described--";
    }

    @Override
    public String toString() {
        if (isDead()) {
            return "a shattered arrow";
        }
        return "a nasty looking arrow with a viciously barbed point lies here";
    }

    void setRoom(ArrowQuiver quiver) {
        room = quiver; // TODO not sure I like the impact of making room protected here
    }
}

class ArrowQuiver extends Hunter.Quiver {

    ArrowQuiver(Integer initialArrowCount) {
        while (occupants().size() < initialArrowCount) {
            Arrow arrow = new Arrow();
            arrow.setRoom(this);
            super.occupants.add(arrow);
        }
    }

    @Override
    public boolean isEmpty() {
        return occupants().isEmpty();
    }

    @Override
    public Arrow next() {
        Optional<Occupant> first = occupants().stream().findFirst();
        if (first.isPresent()) {
            return (Arrow) first.get();
        } else {
            Logger.info("No more arrows in the quiver. You'll have to use your wits!");
            return Arrow.NULL_ARROW;
        }
    }

    @Override
    public String arrowsRemaining() {
        return Integer.toString(occupants().size());
    }

    @Override
    void add(Occupant occupant) {
        super.occupants.add(occupant);
    }

    @Override
    public void add(Arrow arrow) {
        if (!arrow.isBroken()) {
            arrow.moveTo(this);
        }
    }

}
