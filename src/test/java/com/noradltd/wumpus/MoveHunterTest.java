package com.noradltd.wumpus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MoveHunterTest {

    private Room firstRoom;
    private Room secondRoom;
    private Hunter hunter;

    @BeforeEach
    public void beforeEach() {
        firstRoom = new Room();
        secondRoom = new Room();
        firstRoom.attach(secondRoom);
        hunter = new Hunter(firstRoom);
    }

    @Test
    public void hunterMovesFromOneRoomToAnother() {
        Game game = new Game(hunter);

        game.moveHunterThroughExit(0);

        assertThat(hunter.getRoom(), equalTo(secondRoom));
    }
}
