package com.noradltd.wumpus;

// TODO [DIP] Random.getRandomizer() static accessor creates tight coupling throughout the codebase.
//   Every class that needs randomization depends on this static method, violating Dependency Inversion Principle.
//   Remediation: Make Random injectable - pass instances through constructors rather than using static accessor.
//   This also eliminates the need for ThreadLocal workarounds in tests.
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

    // TODO [Service Locator] This method implements Service Locator pattern which is considered an anti-pattern.
    //   It hides dependencies and makes code harder to test and reason about.
    //   Remediation: Remove this static accessor and inject Random instances via constructors.
    static Random getRandomizer() {
        return (Random) Game.getThreadLocalBag().get("randomizer");
    }

}
