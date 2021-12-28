package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private List<Room> adjacentRooms = new ArrayList<>();

    void attachRoom(Room otherRoom) {
        adjacentRooms.add(otherRoom);
        otherRoom.adjacentRooms.add(this);
    }

    List<Room> getAdjacentRooms() {
        return adjacentRooms;
    }
}
