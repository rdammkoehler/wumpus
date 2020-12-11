package com.noradltd.wumpus;

import io.cucumber.java.Before;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class Helpers {
    public static final Map<Class<? extends Room.Occupant>, String> OPTION_KEY_LOOKUP_BY_OCCUPANT_TYPE = new HashMap<>() {{
        put(Wumpus.class, "--wumpi");
        put(BottomlessPit.class, "--pits");
        put(ColonyOfBats.class, "--bats");
    }};

    public static String reInterpolateEscapedCharacters(String input) {
        return input.replaceAll("\\\\n", "\n");
    }

    static Integer countRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>()).size();
    }

    static List<Room> getAllRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
    }

    static Integer countMazeOccupantsByType(Maze maze, Class occupantType) {
        int count = 0;
        List<Room> rooms = getAllRooms(maze);
        for (Room room : rooms) {
            for (Room.Occupant occupant : room.occupants()) {
                if (occupantType.isAssignableFrom(occupant.getClass())) {
                    count++;
                }
            }
        }
        return count;
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

    public static void resetStdout() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    public static ByteArrayOutputStream captureStdout() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdout));
        return stdout;
    }

    public static void programRandomizer(boolean... bools) {
        Game.getThreadLocalBag().replace("randomizer", new Helpers.ProgrammableRandom(bools));
    }

    public static void resetRandomizer() {
        Game.getThreadLocalBag().replace("randomizer", new Random());
    }

    static class ProgrammableRandom extends Random {
        private final boolean[] bools;
        private int boolIdx = 0;

        ProgrammableRandom(boolean... bools) {
            this.bools = bools;
        }

        @Override
        boolean nextBoolean() {
            return bools[boolIdx++];
        }

        @Override
        public String toString() {
            return "ProgrammableRandom{bools=" + Arrays.toString(bools) + ", boolIdx=" + boolIdx + '}';
        }
    }

}
