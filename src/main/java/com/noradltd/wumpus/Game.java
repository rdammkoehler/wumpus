package com.noradltd.wumpus;

import java.util.List;

class Game {
    private final Hunter hunter;
    private int moveCount = 0;
    private int hunterDeaths = 0;

    Game(Hunter hunter) {
        this.hunter = hunter;
    }

    void moveHunterThroughExit(int exitIndex) {
        moveCount++;
        final List<Room> exits = hunter.getRoom().getExits();
        if (exitIndex >=0 && exitIndex < exits.size()) {
            Room exit = exits.get(exitIndex);
            hunter.setRoom(exit);
        }
    }

    void quit() {
        if (hunter.isDead()) {
            hunterDeaths++;
        }
        System.out.println("Moves Made:\t" + moveCount);
        System.out.println("Wumpi Scalps:\t" + hunter.getScalpCount());
        System.out.println("Arrows Remaining:\t" + hunter.getArrowCount());
        System.out.println("Hunters Killed:\t" + hunterDeaths);
        System.out.println("Game Over");
    }
}
