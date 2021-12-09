package com.noradltd.wumpus;

import java.util.List;

import static java.util.Arrays.asList;

public class Random extends java.util.Random {

    @SafeVarargs
    final <T> T[] shuffle(T... objs) {
        return shuffle(asList(objs)).toArray(objs);
    }

    <T> List<T> shuffle(List<T> list) {
        java.util.Collections.shuffle(list, getRandomizer());
        return list;
    }

    static Random getRandomizer() {
        return (Random) Game.getThreadLocalBag().get("randomizer");
    }
}
