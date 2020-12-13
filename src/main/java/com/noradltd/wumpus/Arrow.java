package com.noradltd.wumpus;

public class Arrow extends Room.Occupant {

    private static int nextArrowId=0;
    private int arrowId = nextArrowId++;
    private boolean killedAWumpus = false;

    @Override
    public void respondTo(Room.Occupant visitor) {
        if (Wumpus.class.isInstance(visitor)) {
            Wumpus wumpus = (Wumpus) visitor;
            wumpus.die();
            killedAWumpus = true;
            System.out.println("Your arrow drives itself deep into the hideous beast; it's life force escaping from the hole");
            die(); // not a totally appropriate verb
        } else {
            if(Random.getRandomizer().nextBoolean()) {
                System.out.println("You hear the sharp crack of your arrow as it splitters against the cave wall");
                die(); // not a totally appropriate verb
            } else {
                System.out.println("You hear a clattering sound in the distance");
            }
        }
    }

    public boolean killedAWumpus() {
        return killedAWumpus;
    }

    @Override
    public String describe() {
        return "--arrows should not be described--";
    }

    static final Arrow NULL_ARROW = new Arrow() {
        @Override
        public void respondTo(Room.Occupant visitor) {
            // no-op
        }
    };

    public String toString() {
        if (isDead()) {
            return "A shattered arrow (" + arrowId + ")";
        }
        return "A nasty arrow with a viciously barbed point (" + arrowId + ")";
    }
}

class ArrowQuiver implements Hunter.Quiver {

    private final Integer arrowCount;

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
            System.out.println("No more arrows in the quiver. You'll have to use your wits!");
            return Arrow.NULL_ARROW;
        }
        return new Arrow();
    }

    @Override
    public String arrowsRemaining() {
        return arrowCount.toString();
    }


}