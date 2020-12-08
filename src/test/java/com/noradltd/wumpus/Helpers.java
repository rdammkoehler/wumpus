package com.noradltd.wumpus;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Helpers {
    public static String reInterpolateEscapedCharacters(String input) {
        return input.replaceAll("\\\\n", "\n");
    }

    static Integer countRooms(Room room) {
        return collectRoom(room, new HashSet<>()).size();
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
