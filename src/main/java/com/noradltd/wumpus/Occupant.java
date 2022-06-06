package com.noradltd.wumpus;

public interface Occupant {
    default String getDescription() { return this.getClass().getSimpleName(); }
}
