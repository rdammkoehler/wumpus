package com.noradltd.wumpus;

public interface Occupant extends Comparable {
    default String getDescription() { return this.getClass().getSimpleName(); }

    default int compareTo(Object o) { return 0; }
}
