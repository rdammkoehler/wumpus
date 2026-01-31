package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MazeTraverser {

    public static Set<Room> collectAllRooms(Room startingRoom) {
        return collectRoom(startingRoom, new HashSet<>());
    }

    public static List<Room> collectAllRoomsAsList(Room startingRoom) {
        return collectAllRooms(startingRoom).stream().toList();
    }

    private static Set<Room> collectRoom(Room room, Set<Room> visited) {
        if (!visited.contains(room)) {
            visited.add(room);
            room.exits().forEach(exit -> collectRoom(exit, visited));
        }
        return visited;
    }
}
