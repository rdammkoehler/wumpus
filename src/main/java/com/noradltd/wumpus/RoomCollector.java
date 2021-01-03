package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RoomCollector {
    private final List<Room> allRooms;

    RoomCollector(Room startingRoom) {
        allRooms = collectRoom(startingRoom, new HashSet<>());
    }

    private List<Room> collectRoom(Room room, Set<Room> rooms) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            room.exits().stream().filter(exit -> !rooms.contains(exit)).forEach(exit -> collectRoom(exit, rooms));
        }
        return rooms.stream().collect(Collectors.toUnmodifiableList());
    }

    public List<Room> getAllRooms() {
        return allRooms;
    }
}
