package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/*
A maze should;
    * have at least one room
    * every room should have one or more exits
    * every room should have a unique name

 A maze builder should accept as arguments/parameters
    * Total room count

 Populating the maze will be done elsewhere!
 */
class MazeBuilder {

    Random random = new Random();
    private int exitLimit = 5;  // TODO make this unofficial default configurable
    private int roomCount = 1;  // default it's not at all interesting

    private Room bfsGet(Room room, int idx) {
        Room currentRoom = room;
        List<Room> exits = room.getAdjacentRooms().stream().toList();
        int lastExitIdx = exits.size() - 1;
        if (lastExitIdx < 0) {
            return null;
        } else {
            if (lastExitIdx < idx) {
                while (currentRoom == null && lastExitIdx >= 0) {
                    currentRoom = bfsGet(exits.get(lastExitIdx), idx - lastExitIdx);
                    lastExitIdx--;
                }
            } else {
                currentRoom = exits.get(idx);
            }
        }
        if (currentRoom != null) {
            if (currentRoom.getAdjacentRooms().size() == exitLimit) {
                currentRoom = bfsGet(currentRoom, idx + 1);
            }
        }
        return currentRoom;
    }

    private Room buildAlgo0(int initialRoomCount) {
        // TODO roomCount is ALWAYS True!
        int roomCount = 0;
        Room newRoom = new Room("test room " + ++roomCount);
        Room firstRoom = newRoom;
        while (roomCount < initialRoomCount) {
            newRoom = new Room("test room " + ++roomCount);
            int roomIdx = (roomCount > 1) ? random.nextInt(roomCount - 1) : 0;
            Room nextRoom = bfsGet(firstRoom, roomIdx);
            Objects.requireNonNullElse(nextRoom, firstRoom).attachRoom(newRoom);
        }
        return firstRoom;
    }

    private Room buildAlgo1(int initialRoomCount) {
        List<Room> rooms = new ArrayList<>();
        int roomCount = 0;
        Room newRoom = new Room("test room " + ++roomCount);
        rooms.add(newRoom);
        while (rooms.size() < initialRoomCount) {
            newRoom = new Room("test room " + ++roomCount);
            Room oldRoom;
            do {
                int roomIdx = (rooms.size() > 1) ? random.nextInt(rooms.size() - 1) : 0;
                oldRoom = rooms.get(roomIdx);
            } while (oldRoom.getAdjacentRooms().size() == exitLimit);
            oldRoom.attachRoom(newRoom);
            rooms.add(newRoom);
        }
//        System.out.println("I made " + rooms.size() + " rooms, and roomCount is " + roomCount);
        return rooms.get(0);
    }

    /* creates disconnected graphs */
    private Room buildAlgo2(int initialRoomCount) {
        List<Room> rooms = new ArrayList<>();
        for (int idx = 0; idx < initialRoomCount; idx++) {
            rooms.add(new Room("test room " + idx));
        }
        for (Room room : rooms) {
            int exitIdx = random.nextInt(rooms.size());
            room.attachRoom(rooms.get(exitIdx));
        }
        return rooms.get(random.nextInt(rooms.size()));
    }

    // TODO so the later the room is added the fewer exits it will have, how dull
    //      an alternative might be to create all the rooms disconnected, then start binding them
    //      runtime wise it will be slower but we should get a more even distribution of connections

    /* related thought, should there be a limit to the number of exits a room can have? */
    public Room build() {
        if (roomCount < 1) {
            throw new RuntimeException("A maze must have at least one room");
        }
        if (exitLimit == 1 && roomCount > 2) {
            throw new IllegalArgumentException("A maze with exit limit one can only have 2 rooms");
        }
//        return buildAlgo0(roomCount);  // This one is kinda trash
        return buildAlgo1(roomCount);
//        return buildAlgo2(initialRoomCount);
    }

    /* Builder bits */
    public MazeBuilder withRoomCount(int roomCount) {
        this.roomCount = roomCount;
        return this;
    }

    public MazeBuilder withExitLimit(int exitLimit) {
        if (exitLimit < 1) {
            throw new IllegalArgumentException("Exit Limit must be greater than zero");
        }
        this.exitLimit = exitLimit;
        return this;
    }
}
