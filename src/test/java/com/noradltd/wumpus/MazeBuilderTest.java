package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        assertThat(countRooms(entrance), is(initialRoomCount));
    }

    @Test
    public void aMazeBuilderRejectsRequestsForZeroRooms() {
        int initialRoomCount = 0;

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new MazeBuilder().withRoomCount(initialRoomCount).build());

        assertThat(runtimeException.getMessage(), equalTo("A maze must have at least one room"));
    }

    @Test
    public void aMazeBuilderBuildsNonLinearMazes() {
        Room entrance = new MazeBuilder().withRoomCount(20).build();

        List<Room> rooms = getAllRooms(entrance);
        int maxExits = getMaxExits(rooms);

        assertThat(maxExits, greaterThan(2));
    }

    @Test
    public void aMazeBuilderNeverBuildsAnOrphanedRoom() {
        int initialRoomCount = 20;
        Room entrance = new MazeBuilder().withRoomCount(initialRoomCount).build();

        List<Room> rooms = getAllRooms(entrance);
        int minExits = getMinExits(rooms);

        assertThat(rooms.size(), equalTo(initialRoomCount));
        assertThat(minExits, greaterThan(0));
    }  //TODO this test can/should do more better


    @Test
    public void aMazeBuilderCanHaveLimitsOnRoomExits() {
        int exitLimit = 5;
        Room entrance = new MazeBuilder().withExitLimit(exitLimit).withRoomCount(20).build();

        List<Room> rooms = getAllRooms(entrance);
        int maxExits = getMaxExits(rooms);

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
