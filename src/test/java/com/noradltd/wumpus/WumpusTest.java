package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
public class WumpusTest {
    // TODO this is weak sauce, how to make it better?
    class PermissiveWumpus extends Wumpus {
        public PermissiveWumpus flee(Hunter hunter) {
            super.flee(hunter);
            return this;
        }
    }
    @Test
    public void wumpusMovesFromOneRoomToAnotherWhenFleeing() {
        Wumpus wumpus = new PermissiveWumpus();
        Room initialRoom = new Room();
        Room secondRoom = new Room();

        initialRoom.add(secondRoom);
        wumpus.moveTo(initialRoom);

        wumpus.flee(null);

        assertThat(initialRoom.occupants(), not(hasItem(wumpus)));
        assertThat(secondRoom.occupants(), hasItem(wumpus));
    }

    @Test
    public void wumpusOnlyMovesOneRoomAtATimeWhenFleeing() {
        Wumpus wumpus = new PermissiveWumpus();
        Room initialRoom = new Room();
        Room secondRoom = new Room();
        Room thirdRoom = new Room();

        initialRoom.add(secondRoom);
        secondRoom.add(thirdRoom);
        wumpus.moveTo(initialRoom);

        wumpus.flee(null);

        assertThat(initialRoom.occupants(), not(hasItem(wumpus)));
        assertThat(secondRoom.occupants(), hasItem(wumpus));
        assertThat(thirdRoom.occupants(), not(hasItem(wumpus)));
    }

}
