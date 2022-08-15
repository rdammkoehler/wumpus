package com.noradltd.wumpus;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Helpers {
    public static ByteArrayOutputStream captureStdout() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdout));
        return stdout;
    }

    public static void resetStdout() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    static Integer countRooms(Room entrance) {
        return collectRoom(entrance, new HashSet<>()).size();
    }

    static List<Room> getAllRooms(Room entrance) {
        return collectRoom(entrance, new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
    }

    /* this is fuckin' broken */
    private static Set<Room> collectRoom(Room room, Set<Room> rooms) {
//        System.out.println(rooms);
        if (rooms.add(room)) {
            for (Room adjacentRoom : room.getAdjacentRooms()) {
                collectRoom(adjacentRoom, rooms);
            }
        }
//        else {
//            System.out.println("btdt " + room);
//        }
        return rooms;
    }
}
