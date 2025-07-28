package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static com.noradltd.wumpus.RandomRoomFinder.findRandomRoom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RandomRoomFinderTest {

    @Test
    public void nullThrowsException() {
        assertThrows(
                RuntimeException.class,
                () -> findRandomRoom(null)
        );

    }

    @Test
    public void aRoomWithoutExitsReturnsTheRoom() {
        Room aRoom = new Room();

        Room result = findRandomRoom(aRoom);

        assertThat(result, is(equalTo(aRoom)));
    }

    @Test
    public void aRoomWithOneExitReturnsTheExit() {
        Room aRoom = new Room();
        Room anExit = new Room();
        anExit.add(aRoom);

        Room result = findRandomRoom(aRoom);

        assertThat(result, is(equalTo(anExit)));
    }

    @Test
    public void aRoomWithAnExitTreeTwoDeepReturnsFirstBecauseEmtpy() {
        Room aRoom = new Room();
        Room anExit = new Room();
        Room anotherExit = new Room();
        anExit.add(aRoom);
        anotherExit.add(anExit);

        Room result = findRandomRoom(aRoom);

        assertThat(result, is(equalTo(anExit))); // quite possibly just lucky
    }

    // and there it is, this narrowly demonstrats the issue
    @Test
    public void aRoomWithAnExitTreeTwoDeepReturnsSecondBecauseNotEmtpy() {
        Room aRoom = new Room();
        Room anExit = new Room();
        Room anotherExit = new Room();
        anExit.add(aRoom);
        anotherExit.add(anExit);
        ColonyOfBats colony = new ColonyOfBats();
        colony.moveTo(anExit);

        Room result = findRandomRoom(aRoom);

        assertThat(result, is(equalTo(anotherExit))); // quite possibly just lucky
    }

}
