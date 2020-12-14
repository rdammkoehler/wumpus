package com.noradltd.wumpus;

public class BottomlessPit extends Room.Occupant {
    @Override
    public void respondTo(Room.Occupant actioned) {
        if (Hunter.class.isInstance(actioned)) {
            Hunter hunter = (Hunter) actioned;
            Logger.info("You've stumbled into a bottomless pit and died!");
            hunter.die();
        }
    }

    @Override
    public String describe() {
        return "You feel a cold draft";
    }

    public String toString() {
        return "A gaping maw, fillwed with darkeness and emitting an icy breath of despair";
    }
}
