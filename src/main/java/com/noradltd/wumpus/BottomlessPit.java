package com.noradltd.wumpus;

public class BottomlessPit extends Room.Occupant {
    @Override
    public void respondTo(Room.Occupant interloper) {
        if (interloper instanceof Hunter) {
            Hunter hunter = (Hunter) interloper;
            Logger.info(new Room.RoomDescriber(getRoom()).description());
            Logger.info("You've stumbled into a bottomless pit and died!");
            hunter.die();
        }
    }

    @Override
    public String describe() {
        return "You feel a cold draft";
    }

    public String toString() {
        return "a gaping maw, filled with darkness and emitting an icy breath of despair";
    }
}
