package com.noradltd.wumpus;

public class Random {
    private java.util.Random randomizer = new java.util.Random(0);

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

