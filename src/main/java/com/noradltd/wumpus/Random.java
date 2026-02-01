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

    // TODO: Tight coupling to the Game class.
    // This static method makes this class difficult to reuse or test independently.
    // Consider using a proper dependency injection mechanism to decouple this class
    // from the Game class.
    static Random getRandomizer() {
        return (Random) Game.getThreadLocalBag().get("randomizer");
    }

}
