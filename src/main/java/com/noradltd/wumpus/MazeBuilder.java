package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

class MazeBuilder {

    Random random = new Random();
    private int exitLimit = 5;  // TODO make this unofficial default configurable
    private int roomCount = 1;  // default it's not at all interesting

    public Room build() {
        if (exitLimit == 1 && roomCount > 2) {
            throw new IllegalArgumentException("A maze with exit limit one can only have 2 rooms");
        }
        List<Room> rooms = new ArrayList<>();
        Room newRoom = new Room(roomName());
        rooms.add(newRoom);
        while (rooms.size() < this.roomCount) addRoom(rooms);
        return selectRoomAtRandom(rooms);
    }

    private String roomName() {
        return "test room " + UUID.randomUUID();
    }

    private void addRoom(List<Room> rooms) {
        Room newRoom = new Room(roomName());
        Room oldRoom = selectRoomWithFewerThanExitLimitExits(rooms);
        newRoom.attachRoom(oldRoom);
        rooms.add(newRoom);
    }

    private Room selectRoomWithFewerThanExitLimitExits(List<Room> rooms) {
        Predicate<Room> hasAvailableExits = room -> room.getAdjacentRooms().size() != exitLimit;
        List<Room> viableRooms = rooms.stream().filter(hasAvailableExits).toList();
        return selectRoomAtRandom(viableRooms);
    }

    private Room selectRoomAtRandom(List<Room> rooms) {
        int roomIdx = (rooms.size() > 1) ? random.nextInt(rooms.size() - 1) : 0;
        return rooms.get(roomIdx);
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
