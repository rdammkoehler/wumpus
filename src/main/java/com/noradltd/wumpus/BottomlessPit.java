package com.noradltd.wumpus;

import java.io.Serial;

public class BottomlessPit extends Room.Occupant {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void initInteractions() {
        super.initInteractions();
        interactions.put(Hunter.class, interloper -> {
            Logger.info("You've stumbled into a bottomless pit and died!");
            interloper.die();
        });
    }

    @Override
    public String describe() {
        return "You feel a cold draft";
    }

    @Override
    public String toString() {
        return "a gaping maw, filled with darkness and emitting an icy breath of despair";
    }
}
