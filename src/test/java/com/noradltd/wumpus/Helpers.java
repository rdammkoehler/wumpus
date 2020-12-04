package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.Set;

public class Helpers {
    public static String reinterpolatEscapedCharacters(String input) {
        return input.replaceAll("\\\\n", "\n");
    }

    static Integer countRooms(Room room) {
        return collectRoom(room, new HashSet<Room>()).size();
    }

    private static Set<Room> collectRoom(Room room, Set<Room> rooms) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            for (Room exit : room.exits()) {
                if (!rooms.contains(exit)) {
                    collectRoom(exit, rooms);
                }
            }
        }
        return rooms;
    }
}
