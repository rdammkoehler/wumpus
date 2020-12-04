package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Stream;

interface Maze {
    Room currentRoom();
}

class MazeBuilder {
    static class MazeStruct implements Maze {
        private Room currentRoom;
        private Set<Room> rooms;

        private MazeStruct(Set<Room> rooms) {
            this.rooms = rooms;
            // todo make sure the room is empty!
            currentRoom = getRandomRoom(rooms);
        }

        public Room currentRoom() {
            return currentRoom;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Room room : rooms) {
                sb.append("Room ").append(room.hashCode()).append(": ");
                sb.append(new Game.RoomDescriber(room).description()).append("\n*****\n");
            }
            return sb.toString();
        }
    }

    private static final Random random = new Random(0);

    private Set<Room> rooms = new HashSet<Room>();

    private MazeBuilder() {
    }

    private Set<Room> createRooms() {
        boolean forceLinking = false;
        while (needsMoreRooms()) {
            addRoom(forceLinking);
            forceLinking = needsMoreRooms();
        }
        return rooms;
    }

    private void addRoom(boolean forceLinking) {
        Room room = new Room();
        rooms.add(room);
        addExits(room, forceLinking || hasEnoughRooms());
    }

    private void addExits(Room room, boolean forceLinking) {
        if (needsMoreExits(room)) {
            randomLengthIntegerStream(3)
                    .forEach(integer -> addExit(room, forceLinking));
            room.exits().stream()
                    .filter(exit -> random.nextBoolean())
                    .forEach(exit -> addExits(exit, hasEnoughRooms()));
        }
    }

    private Stream<Integer> randomLengthIntegerStream(Integer upperBound) {
        Integer number = random.nextInt(upperBound - 1) + 1;
        return Arrays.asList(new Integer[number]).stream();
    }

    private void addExit(Room room, boolean forceLinking) {
        if (!forceLinking && needsMoreRooms()) {
            addExit(room);
        } else {
            linkExit(room);
        }
    }

    private void addExit(Room room) {
        rooms.add(new Room().add(room));
    }

    private void linkExit(Room room) {
        Room exit;
        for (exit = room; exit == room; exit = getRandomRoom(rooms)) ;
        room.add(exit);
    }

    public static Room getRandomRoom(Collection<Room> rooms) {
        return rooms.toArray(new Room[]{})[random.nextInt(rooms.size())];
    }

    private boolean needsMoreRooms() {
        return !hasEnoughRooms();
    }

    private boolean hasEnoughRooms() {
        return rooms.size() > 19;
    }

    private boolean needsMoreExits(Room room) {
        return room.exits().size() < 2;
    }

    private Maze buildMaze() {
        return new MazeStruct(createRooms());
    }

    static Maze build() {
        return new MazeBuilder().buildMaze();
    }
}
