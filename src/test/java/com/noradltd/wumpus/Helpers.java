package com.noradltd.wumpus;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

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
        return collectRoom(entrance, new HashSet<>()).stream().toList();
    }

    private static Set<Room> collectRoom(Room room, Set<Room> rooms) {
        if (rooms.add(room)) {
            for (Room adjacentRoom : room.getAdjacentRooms()) {
                collectRoom(adjacentRoom, rooms);
            }
        }
        return rooms;
    }

    static int getMinExits(List<Room> rooms) {
        return getRoomExitCounts(rooms).min().orElseThrow();
    }

    static int getMaxExits(List<Room> rooms) {
        return getRoomExitCounts(rooms).max().orElseThrow();
    }

    static IntStream getRoomExitCounts(List<Room> rooms) {
        return rooms.stream().mapToInt(room -> room.getAdjacentRooms().size());
    }

    static void printMaze(Room mazeEntrance) {
        getAllRooms(mazeEntrance).stream().forEach(System.out::println);
    }

    static boolean hasWumpus(Room room) {
        return room.getOccupants().stream().anyMatch((occupant) -> occupant instanceof MazePopulatorTest.Wumpus);
    }

    static long countWumpi(Room mazeEntrance) {
        List<Room> rooms = getAllRooms(mazeEntrance);
        return rooms.stream().filter(Helpers::hasWumpus).count();
    }

    static boolean hasBats(Room room) {
        return room.getOccupants().stream().anyMatch((occupant) -> occupant instanceof MazePopulatorTest.ColonyOfBats);
    }

    static long countBats(Room mazeEntrance) {
        List<Room> rooms = getAllRooms(mazeEntrance);
        return rooms.stream().filter(Helpers::hasBats).count();
    }

    static boolean hasBottomlessPits(Room room) {
        return room.getOccupants().stream().anyMatch((occupant) -> occupant instanceof MazePopulatorTest.BottomlessPit);
    }

    static long countBottomlessPits(Room mazeEntrance) {
        List<Room> rooms = getAllRooms(mazeEntrance);
        return rooms.stream().filter(Helpers::hasBottomlessPits).count();
    }
}
