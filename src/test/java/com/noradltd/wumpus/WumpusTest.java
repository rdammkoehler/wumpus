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

        wumpus.flee(null);

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

        wumpus.flee(null);

        assertThat(initialRoom.occupants(), not(hasItem(wumpus)));
        assertThat(secondRoom.occupants(), hasItem(wumpus));
        assertThat(thirdRoom.occupants(), not(hasItem(wumpus)));
    }

    @Test
    public void wumpusEatsHunterWhenGivenAChance() {
        Wumpus wumpus = new Wumpus();
        Hunter hunter = new Hunter();

        wumpus.respondTo(hunter);

        assertThat(wumpus.isFed(), is(true));
        assertThat(hunter.isDead(), is(true));
    }

    @Test
    public void wumpusWillEatHunterIfThereIsNoEscape() {
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);
        Hunter hunter = new Hunter();
        Helpers.programRandomizer(false);

        wumpus.respondTo(hunter);

        Helpers.resetRandomizer();

        assertThat(wumpus.isFed(), is(true));
        assertThat(hunter.isDead(), is(true));
        assertThat(room.occupants().size(), equalTo(1));
    }
}
