package com.noradltd.wumpus;

public class Random {
    private final java.util.Random randomizer = new java.util.Random();

    int nextInt(int bound) {
        return randomizer.nextInt(bound);
    }

    boolean nextBoolean() {
        return randomizer.nextBoolean();
    }

    void setSeed(long seed) {
        randomizer.setSeed(seed);
    }

    static Random getRandomizer() {
        return (Random) Game.getThreadLocalBag().get("randomizer");
    }
    java.util.Random getJavaRandomizer() {
        return randomizer;
    }
}
