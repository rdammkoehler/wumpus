package com.noradltd.wumpus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoveHunterTest {

    private Room firstRoom;
    private Room secondRoom;
    private Hunter hunter;
    private Game game;

    @BeforeEach
    public void beforeEach() {
        firstRoom = new Room();
        secondRoom = new Room();
        firstRoom.attachRoom(secondRoom);
        hunter = new Hunter(firstRoom);
        game = new Game(hunter);
    }

    @Test
    public void hunterMovesFromOneRoomToAnother() {
        game.moveHunterThroughExit(0);

        assertThat(hunter.getRoom(), equalTo(secondRoom));
    }

    @Test
    public void hunterMovesFromOneRoomToAnotherAndBack() {
        game.moveHunterThroughExit(0);
        game.moveHunterThroughExit(0);

        assertThat(hunter.getRoom(), equalTo(firstRoom));
    }

    @Test
    public void huntersDontMoveThroughNonExistentExits() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.moveHunterThroughExit(1));

        assertThat(hunter.getRoom(), equalTo(firstRoom));
    }

    @Test
    public void huntersDontMoveThroughNonExistentExitsWhenGivenManyChoices() {
        secondRoom.attachRoom(new Room());

        assertThrows(IndexOutOfBoundsException.class, () -> game.moveHunterThroughExit(2));

        assertThat(hunter.getRoom(), equalTo(firstRoom));
    }

    @Test
    public void huntersCantMoveThroughUnnaturalExits() {
        assertThrows(IndexOutOfBoundsException.class, () -> game.moveHunterThroughExit(-1));

        assertThat(hunter.getRoom(), equalTo(firstRoom));
    }
}
