package com.noradltd.wumpus;

import java.util.HashMap;
import java.util.Map;

class Game {
    private final Hunter hunter;
    private boolean playing = true;

    Game(String[] options) {
        // TODO we construct options 3 times, once for builder, once for loader, and once for hunter; do this only once
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);
        hunter = new Hunter(new ArrowQuiver(new Maze.Options(options).getInitialArrowCount()));  // default to 5 arrows for the moment
        hunter.moveTo(maze.entrance());
    }

    public void move(Integer exitIndex) {
        hunter.moveTo(exitIndex);
    }

    public void shoot(Integer exitIndex) {
        hunter.shoot(exitIndex);
    }

    public String inventory() {
        return hunter.inventory();
    }

    public void take(String item) { hunter.take(item); }

    @Override
    public String toString() {
        return hunter.getRoom().toString();
    }

    public boolean isPlaying() {
        return playing && !hunter.isDead();
    }

    public void quit() {
        playing = false;
    }

    private static final ThreadLocal<Map<String, Object>> threadLocalBag = ThreadLocal.withInitial(() -> new HashMap<>() {{
        put("randomizer", new Random());
    }});

    static Map<String, Object> getThreadLocalBag() {
        return threadLocalBag.get();
    }

}

