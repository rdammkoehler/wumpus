package com.noradltd.wumpus;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Helpers {

    public static final String RANDOMIZER_THREADLOCALBAGKEY = "randomizer";

    static Integer countRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>()).size();
    }

    static List<Room> getAllRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
    }

    static Integer countMazeOccupantsByType(Maze maze, Class<? extends Room.Occupant> occupantType) {
        int count = 0;
        List<Room> rooms = getAllRooms(maze);
        for (Room room : rooms) {
            for (Room.Occupant occupant : room.occupants()) {
                if (occupant.getClass().isAssignableFrom(occupantType)) {
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
        Game.getThreadLocalBag().replace(RANDOMIZER_THREADLOCALBAGKEY, new Helpers.ProgrammableRandom(bools));
    }

    public static void programRandomizer(int... ints) {
        Game.getThreadLocalBag().replace(RANDOMIZER_THREADLOCALBAGKEY, new Helpers.ProgrammableRandom(ints));
    }

    public static void programRandomizer(boolean[] bools, int[] ints) {
        Game.getThreadLocalBag().replace(RANDOMIZER_THREADLOCALBAGKEY, new Helpers.ProgrammableRandom(bools, ints));
    }

    public static void resetRandomizer() {
        Random random = new Random();
        random.setSeed(0L);
        Game.getThreadLocalBag().replace(RANDOMIZER_THREADLOCALBAGKEY, random);
    }

    static void restartRoomNumberer() {
        Room.roomNumberer = new Room.RoomNumberer() {
            private int instanceCounter = 1;

            @Override
            public Integer nextRoomNumber() {
                return instanceCounter++;
            }
        };
    }

    static class ProgrammableRandom extends Random {
        private final boolean[] bools;
        private int boolIdx = 0;
        private final int[] ints;
        private int intIdx = 0;

        ProgrammableRandom(boolean... bools) {
            this.bools = bools;
            ints = new int[0];
        }

        ProgrammableRandom(int... ints) {
            this.ints = ints;
            bools = new boolean[0];
        }

        ProgrammableRandom(boolean[] bools, int[] ints) {
            this.bools = bools;
            this.ints = ints;
        }

        @Override
        public boolean nextBoolean() {
            Logger.debug("nextBoolean (" + ((bools.length == 0 || boolIdx >= bools.length) ? "X" : bools[boolIdx]) + ")");
            if (bools.length > 0 && boolIdx < bools.length) {
                return bools[boolIdx++];
            }
            return super.nextBoolean();
        }

        @Override
        public int nextInt(int bound) {
            Logger.debug("nextInt (" + ((ints.length == 0 || intIdx >= ints.length) ? 0 : ints[intIdx]) + ")");
            if (ints.length > 0 && intIdx < ints.length) {
                return ints[intIdx++];
            }
            return super.nextInt(bound);
        }

        @Override
        public String toString() {
            return "ProgrammableRandom{" + "bools=" + Arrays.toString(bools) +
                    ", boolIdx=" + boolIdx +
                    ", ints=" + Arrays.toString(ints) +
                    ", intIdx=" + intIdx +
                    '}';
        }
    }

}
