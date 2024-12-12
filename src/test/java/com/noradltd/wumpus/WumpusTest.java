package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.noradltd.wumpus.Helpers.programRandomizer;
import static com.noradltd.wumpus.Helpers.resetRandomizer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WumpusTest {

    @ExtendWith(ResetRandomizerExtension.class)
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

    @ExtendWith(ResetRandomizerExtension.class)
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

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void wumpusEatsHunterWhenGivenAChance() {
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);
        Hunter hunter = new Hunter();
        programRandomizer(true, true); // choose wumpus first, then choose eat(hunter)
        hunter.moveTo(room);

        wumpus.respondTo(hunter);

        resetRandomizer();
        assertThat(hunter.isDead(), is(true));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void wumpusWillEatHunterIfThereIsNoEscape() {
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);
        Hunter hunter = new Hunter();
        hunter.moveTo(room);
        programRandomizer(false);

        wumpus.respondTo(hunter);

        resetRandomizer();

        assertThat(hunter.isDead(), is(true));
        assertThat(room.occupants().size(), equalTo(1));
    }
}
