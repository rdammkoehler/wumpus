package com.noradltd.wumpus;

class Hunter {
    enum InventoryItem {WumpusScalp, Arrow}

    private int scalpCount = 0;
    private int arrowCount = 5;
    private boolean alive = true;

    Hunter(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    private Room room;


    void die() {
        alive = false;
    }

    boolean isDead() {
        return !alive;
    }

    void addInventory(InventoryItem item) {
        if (item == InventoryItem.WumpusScalp) {
            scalpCount++;
        }
    }

    void removeInventory(InventoryItem item) {
        if (item == InventoryItem.Arrow) {
            arrowCount--;
        }
    }

    public int getScalpCount() {
        return scalpCount;
    }

    public int getArrowCount() {
        return arrowCount;
    }
}
