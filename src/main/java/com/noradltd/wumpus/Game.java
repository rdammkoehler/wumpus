package com.noradltd.wumpus;

class Game {
    private final Hunter hunter;
    private int moveCount = 0;
    private int hunterDeaths = 0;

    Game(Hunter hunter) {
        this.hunter = hunter;
    }

    void moveHunterThroughExit(int exitIndex) {
        moveCount++;
        Room currentRoom = hunter.getRoom();
        Room roomExit = currentRoom.getAdjacentRooms().get(exitIndex);
        hunter.setRoom(roomExit);
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
