package com.noradltd.wumpus;

import java.util.HashMap;
import java.util.Map;

public class Random {
    private java.util.Random randomizer = new java.util.Random(0); //will this help at all?

    public int nextInt() {
        return randomizer.nextInt();
    }

    int nextInt(int bound) {
        return randomizer.nextInt(bound);
    }

    boolean nextBoolean() {
        return randomizer.nextBoolean();
    }

    void setSeed(long seed) {
        randomizer.setSeed(seed);

    }

    public static Random getRandomizer() {
        return (Random) Game.getThreadLocalBag().get("randomizer");
    }
}

