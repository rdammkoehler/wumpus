package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static com.noradltd.wumpus.Helpers.programRandomizer;
import static com.noradltd.wumpus.Helpers.resetRandomizer;
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
        programRandomizer(false);

        wumpus.respondTo(new Hunter());

        resetRandomizer();
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
        programRandomizer(false);

        wumpus.respondTo(new Hunter());

        resetRandomizer();
        assertThat(initialRoom.occupants(), not(hasItem(wumpus)));
        assertThat(secondRoom.occupants(), hasItem(wumpus));
        assertThat(thirdRoom.occupants(), not(hasItem(wumpus)));
    }

    @Test
    public void wumpusEatsHunterWhenGivenAChance() {
        Wumpus wumpus = new Wumpus();
        Hunter hunter = new Hunter();
        programRandomizer(true);

        wumpus.respondTo(hunter);

        resetRandomizer();
        assertThat(hunter.isDead(), is(true));
    }

    @Test
    public void wumpusWillEatHunterIfThereIsNoEscape() {
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);
        Hunter hunter = new Hunter();
        programRandomizer(false);

        wumpus.respondTo(hunter);

        resetRandomizer();

        assertThat(hunter.isDead(), is(true));
        assertThat(room.occupants().size(), equalTo(1));
    }
}
