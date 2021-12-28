package com.noradltd.wumpus;

class Game {
    private final Hunter hunter;
    private int moveCount = 0;

    Game(Hunter hunter) {
        this.hunter = hunter;
    }

    void moveHunterThroughExit(int exitId) {
        moveCount++;
    }

    void quit() {
        System.out.println("Moves Made:\t" + moveCount);
        System.out.println("Wumpi Scalps:\t" + hunter.getScalpCount());
        System.out.println("Arrows Remaining:\t" + hunter.getArrowCount());
        System.out.println("Hunters Killed:");
        System.out.println("Game Over");
    }
}
