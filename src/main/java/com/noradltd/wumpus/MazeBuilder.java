package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MazeBuilder {

    Random random = new Random();
    private int exitLimit = 5;  // TODO make this unofficial default configurable
    private int roomCount = 1;  // default it's not at all interesting

    public Room build() {
        if (exitLimit == 1 && roomCount > 2) {
            throw new IllegalArgumentException("A maze with exit limit one can only have 2 rooms");
        }
        List<Room> rooms = new ArrayList<>();
        int roomCount1 = 0;
        Room newRoom = new Room("test room " + ++roomCount1);
        rooms.add(newRoom);
        while (rooms.size() < roomCount) {
            newRoom = new Room("test room " + ++roomCount1);
            Room oldRoom;
            do {
                int roomIdx = (rooms.size() > 1) ? random.nextInt(rooms.size() - 1) : 0;
                oldRoom = rooms.get(roomIdx);
            } while (oldRoom.getAdjacentRooms().size() == exitLimit);
            oldRoom.attachRoom(newRoom);
            rooms.add(newRoom);
        }
        return rooms.get(0);
    }

    /* Builder bits */
    public MazeBuilder withRoomCount(int roomCount) {
        if (roomCount < 1) {
            throw new RuntimeException("A maze must have at least one room");
        }
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
