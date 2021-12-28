package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<Room> exits = new ArrayList<>();

    void attach(Room otherRoom) {
        exits.add(otherRoom);
        otherRoom.exits.add(this);
    }

    List<Room> getExits() {
        return exits;
    }
}
