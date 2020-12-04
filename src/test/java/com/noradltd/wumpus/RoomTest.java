package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoomTest {
    @Test
    public void roomsHaveExits() {
        assertThat(new Room().exits(), is(notNullValue()));
    }

    @Test
    public void roomsAcceptAdditionalExits() {
        Room room = new Room();
        Room exit = new Room();

        room.add(exit);

        assertThat(room.exits(), hasItem(exit));
    }

    @Test
    public void roomExitCanExitToRoom() {
        Room room = new Room();
        Room exit = new Room();

        room.add(exit);

        assertThat(exit.exits(), hasItem(room));
    }

    @Test
    public void roomCanHaveManyExits() {
        Room room = new Room();
        List<Room> exits = new ArrayList<Room>();
        for (int idx = 0; idx < 10; idx++) {
            Room exit = new Room();
            exits.add(exit);
            room.add(exit);
        }

        exits.forEach(exit -> {
            assertThat(room.exits().contains(exit), is(true));
            assertThat(exit.exits().contains(room), is(true));
        });
    }

    @Test
    public void roomsHaveRoomNumbers() {
        assertThat(new Room().number(), is(greaterThan(0)));
    }

    @Test
    public void roomNumbersAreSequential() {
        Room one = new Room(), two = new Room();

        assertThat(two.number() - one.number(), is(1));
    }
}