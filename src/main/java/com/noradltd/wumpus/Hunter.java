package com.noradltd.wumpus;

class Hunter {
    enum InventoryItem {WumpusScalp, Arrow}

    private int scalpCount = 0;
    private int arrowCount = 5;

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
