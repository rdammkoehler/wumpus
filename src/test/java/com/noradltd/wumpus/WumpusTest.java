package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
public class WumpusTest {
    @Test
    public void wumpusMovesFromOneRoomToAnotherWhenFleeing() {
        Wumpus wumpus = new Wumpus();
        Room initialRoom = new Room();
        Room secondRoom = new Room();

        initialRoom.add(secondRoom);
        wumpus.moveTo(initialRoom);

        wumpus.flee();

        assertThat(initialRoom.occupants(), not(hasItem(wumpus)));
        assertThat(secondRoom.occupants(), hasItem(wumpus));
    }

    @Test
    public void wumpusOnlyMovesOneRoomAtATimeWhenFleeing() {
        Wumpus wumpus = new Wumpus();
        Room initialRoom = new Room();
        Room secondRoom = new Room();
        Room thirdRoom = new Room();

        initialRoom.add(secondRoom);
        secondRoom.add(thirdRoom);
        wumpus.moveTo(initialRoom);

        wumpus.flee();

        assertThat(initialRoom.occupants(), not(hasItem(wumpus)));
        assertThat(secondRoom.occupants(), hasItem(wumpus));
        assertThat(thirdRoom.occupants(), not(hasItem(wumpus)));
    }

}
