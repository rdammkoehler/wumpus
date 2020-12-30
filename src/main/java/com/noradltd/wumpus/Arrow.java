package com.noradltd.wumpus;

public class Arrow extends Room.Occupant {
    static final Arrow NULL_ARROW = new Arrow() {
        @Override
        public void respondTo(Room.Occupant occupant) {
            // no-op
        }

        @Override
        public String toString() {
            return "this arrow does nothing";
        }
    };

    private static int nextArrowId = 0;
    private int arrowId = nextArrowId++;
    private boolean killedAWumpus = false;

    @Override
    public void respondTo(Room.Occupant interloper) {
        if (!isBroken() && interloper instanceof Wumpus) {
            Wumpus wumpus = (Wumpus) interloper;
            wumpus.die();
            killedAWumpus = true;
            Logger.info("Your arrow drives itself deep into the hideous beast; it's life force escaping from the hole in it's leathery hide");
            shatter();
        }
    }

    @Override
    public void moveTo(Room newRoom) {
        super.moveTo(newRoom);
        if (!isBroken()) {
            if (Random.getRandomizer().nextBoolean()) {
                shatter();
            } else {
                Logger.info("You hear a clattering sound in the distance");
            }
        }
    }

    public boolean isBroken() {
        return isDead();
    }

    private void shatter() {
        if (!killedAWumpus()) {
            Logger.info("You hear the sharp crack of your arrow as it splitters against the cave wall");
        }
        die();
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
}

class ArrowQuiver implements Hunter.Quiver {

    private int arrowCount;

    ArrowQuiver(Integer initialArrowCount) {
        arrowCount = initialArrowCount;
    }

    @Override
    public boolean isEmpty() {
        return arrowCount < 1;
    }

    @Override
    public Arrow next() {
        if (isEmpty()) {
            Logger.info("No more arrows in the quiver. You'll have to use your wits!");
            return Arrow.NULL_ARROW;
        }
        arrowCount--;
        return new Arrow();
    }

    @Override
    public String arrowsRemaining() {
        return Integer.toString(arrowCount);
    }

    @Override
    public void add(Arrow arrow) {
        if (!arrow.isBroken()) {
            arrowCount++;
        }
    }

}