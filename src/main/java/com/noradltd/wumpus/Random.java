package com.noradltd.wumpus;


public class Random  {
    private java.util.Random randomizer;

    public Random() {
    }

    private java.util.Random getDelegateRandomizer() {
        if (randomizer == null) {
            randomizer = new java.util.Random();
        }
        return randomizer;
    }

    public int nextInt(int bound) {
        return getDelegateRandomizer().nextInt(bound);
    }

    public boolean nextBoolean() {
        return getDelegateRandomizer().nextBoolean();
    }

    public void setSeed(long seed) {
        getDelegateRandomizer().setSeed(seed);
    }

    static Random getRandomizer() {
        return (Random) Game.getThreadLocalBag().get("randomizer");
    }

}
