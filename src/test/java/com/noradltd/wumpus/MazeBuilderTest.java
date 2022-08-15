package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.noradltd.wumpus.Helpers.getAllRooms;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        System.out.println(idx);
        Room currentRoom = room;
        List<Room> exits = room.getAdjacentRooms().stream().toList();
        // does not account for zero exits (exhaustion)
        int lastExitIdx = exits.size() - 1;
        if (lastExitIdx < 0) {
            //exhaustion
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
        return currentRoom;
    }

    private Room buildAlgo0(int initialRoomCount) {
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
//        return buildAlgo0(initialRoomCount);
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

public class MazeBuilderTest {

    @Test
    public void aMazeBuilderReturnsARoomReference() {
        Room entrance = new MazeBuilder().build();

        assertThat(entrance, is(not(nullValue())));
    }

    @Test
    public void aMazeBuilderBuildsAMazeAsBigAsRequested() {
        int initialRoomCount = 5;
        Room entrance = new MazeBuilder().withRoomCount(initialRoomCount).build();

        assertThat(Helpers.countRooms(entrance), is(initialRoomCount));
    }

    @Test
    public void aMazeBuilderRejectsRequestsForZeroRooms() {
        int initialRoomCount = 0;

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new MazeBuilder().withRoomCount(initialRoomCount).build());

        assertThat(runtimeException.getMessage(), equalTo("A maze must have at least one room"));
    }

    // TODO potentially flaky test
    @Test
    public void aMazeBuilderBuildsNonLinearMazes() {
        Room entrance = new MazeBuilder().withRoomCount(20).build();
        List<Room> rooms = getAllRooms(entrance);
        int maxExits = rooms.stream().mapToInt(room -> room.getAdjacentRooms().size()).max().getAsInt();
        assertThat(maxExits, greaterThan(2));
    }

    @Test
    public void aMazeBuilderNeverBuildsAnOrphanedRoom() {
        int initialRoomCount = 20;
        Room entrance = new MazeBuilder().withRoomCount(initialRoomCount).build();
        List<Room> rooms = getAllRooms(entrance).stream().sorted((o1, o2) -> {
            int roomNumber1 = Integer.parseInt(o1.getName().substring(10));
            int roomNumber2 = Integer.parseInt(o2.getName().substring(10));
            return roomNumber1 - roomNumber2;
        }).toList();
        assertThat(rooms.size(), equalTo(initialRoomCount));
        long minExits = rooms.stream().mapToLong(room -> room.getAdjacentRooms().size()).min().getAsLong();
        assertThat(minExits, greaterThan(0L));
    }  //TODO this test can/should do more better

    @Test
    public void aMazeBuilderCanHaveLimitsOnRoomExits() {
        int exitLimit = 5;
        Room entrance = new MazeBuilder().withExitLimit(exitLimit).withRoomCount(20).build();
        List<Room> rooms = getAllRooms(entrance);
        int maxExits = rooms.stream().mapToInt(room -> room.getAdjacentRooms().size()).max().getAsInt();
        assertThat(maxExits, lessThanOrEqualTo(exitLimit));
    }

    @Test
    public void aMazeBuilderRejectsExitLimitsLessThanOne() {
        int exitLimit = 0;
        assertThrows(IllegalArgumentException.class, () -> new MazeBuilder().withExitLimit(exitLimit));
    }

    @Test
    public void aMazeBuilderWithExitLimitOneCanOnlyMakeMazesOfUpToTwoRooms() {
        assertThrows(IllegalArgumentException.class, () -> new MazeBuilder().withExitLimit(1).withRoomCount(3).build());
    }
}
