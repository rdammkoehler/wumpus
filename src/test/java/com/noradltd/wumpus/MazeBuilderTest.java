package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    private Room bfsGet(Room room, int idx) {
        System.out.println(idx);
        int ct = 0;
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
            Room x = bfsGet(firstRoom, roomIdx);
            if (x == null) {
                firstRoom.attachRoom(newRoom);
            } else {
                x.attachRoom(newRoom);
            }
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
            Room oldRoom = null;
            do {
                int roomIdx = (rooms.size() > 1) ? random.nextInt(rooms.size() - 1) : 0;
                oldRoom = rooms.get(roomIdx);
            } while (oldRoom.getAdjacentRooms().size() == exitLimit);
            oldRoom.attachRoom(newRoom);
            rooms.add(newRoom);
        }
        System.out.println("I made " + rooms.size() + " rooms, and roomCount is " + roomCount);
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
    public Room build(int initialRoomCount) {
        if (initialRoomCount < 1) {
            throw new RuntimeException("A maze must have at least one room");
        }
        if (exitLimit == 1 && initialRoomCount > 2) {
            throw new IllegalArgumentException("A maze with exit limit one can only have 2 rooms");
        }
//        return buildAlgo0(initialRoomCount);
        return buildAlgo1(initialRoomCount);
//        return buildAlgo2(initialRoomCount);
    }

    /* Builder bits */
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
        Room entrance = new MazeBuilder().build(1);

        assertThat(entrance, is(not(nullValue())));
    }

    @Test
    public void aMazeBuilderBuildsAMazeAsBigAsRequested() {
        int initialRoomCount = 5;
        Room entrance = new MazeBuilder().build(initialRoomCount);

        assertThat(Helpers.countRooms(entrance), is(initialRoomCount));
    }

    @Test
    public void aMazeBuilderRejectsRequestsForZeroRooms() {
        int initialRoomCount = 0;

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            new MazeBuilder().build(initialRoomCount);
        });

        assertThat(runtimeException.getMessage(), equalTo("A maze must have at least one room"));
    }

    // TODO potentially flakey test
    @Test
    public void aMazeBuilderBuildsNonLinearMazes() {
        Room entrance = new MazeBuilder().build(20);
        List<Room> rooms = getAllRooms(entrance);
        int maxExits = 0;
        for (Room room : rooms) {
            maxExits = Math.max(maxExits, room.getAdjacentRooms().size());
        }
        assertThat(maxExits, greaterThan(2));
    }

    @Test
    public void aMazeBuilderNeverBuildsAnOrphanedRoom() {
        Room entrance = new MazeBuilder().build(5000);
        List<Room> rooms = getAllRooms(entrance).stream().sorted(new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                int roomNumber1 = Integer.parseInt(o1.getName().substring(10));
                int roomNumber2 = Integer.parseInt(o2.getName().substring(10));
                return roomNumber1 - roomNumber2;
            }
        }).toList();
        System.out.println("There are " + rooms.size() + " rooms in the test set");
        long minExits = Long.MAX_VALUE;
        for (Room room : rooms) {
            long exitCount = room.getAdjacentRooms().size();
            minExits = Math.min(minExits, exitCount);
            System.out.println(room.getDescription());
        }
        assertThat(minExits, greaterThan(0L));
    }  //TODO this test can/should do more better

    @Test
    public void aMazeBuilderCanHaveLimitsOnRoomExits() {
        int exitLimit = 5;
        Room entrance = new MazeBuilder().withExitLimit(exitLimit).build(5000);
        List<Room> rooms = getAllRooms(entrance);
        long maxExits = Long.MIN_VALUE;
        for (Room room : rooms) {
            long exitCount = room.getAdjacentRooms().size();
            maxExits = Math.max(maxExits, exitCount);
        }
        assertThat(maxExits, lessThanOrEqualTo((long) exitLimit));
    }

    @Test
    public void aMazeBuilderRejectsExitLimitsLessThanOne() {
        int exitLimit = 0;

        assertThrows(IllegalArgumentException.class, () -> new MazeBuilder().withExitLimit(exitLimit));
    }

    @Test
    public void aMazeBuilderWithExitLimitOneCanOnlyMakeMazesOfUpToTwoRooms() {
        assertThrows(IllegalArgumentException.class, () -> new MazeBuilder().withExitLimit(1).build(3));
    }
}
