package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ColonyOfBatsTest {
    @Test
    public void aColonyOfBatsMovesAHunterToAnotherRoom() {
        Room startingRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(finishingRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        Hunter hunter = new Hunter();

        hunter.moveTo(startingRoom);

        assertThat(hunter.getRoom(), is(not(equalTo(startingRoom))));
    }

    @Test
    public void aColonyOfBatsIgnoresAWumpus() {
        Room room = new Room();
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(room);
        Wumpus wumpus = new Wumpus();

        wumpus.moveTo(room);

        assertThat(wumpus.getRoom(), is(equalTo(room)));
    }
}
