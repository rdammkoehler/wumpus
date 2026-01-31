package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aRoomWithAnExitTreeTwoDeepReturnsFirstBecauseEmtpy() {
        Room aRoom = new Room();
        Room anExit = new Room();
        Room anotherExit = new Room();
        anExit.add(aRoom);
        anotherExit.add(anExit);
        // Program randomizer: navigate to anExit (empty) on first try
        // nextInt(10)=0 (1 push), nextInt(1)=0 (select anExit which is empty)
        Helpers.programRandomizer(0, 0);

        Room result = findRandomRoom(aRoom);

        assertThat(result, is(equalTo(anExit)));
    }

    // and there it is, this narrowly demonstrats the issue
    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aRoomWithAnExitTreeTwoDeepReturnsSecondBecauseNotEmtpy() {
        Room aRoom = new Room();
        Room anExit = new Room();
        Room anotherExit = new Room();
        anExit.add(aRoom);
        anotherExit.add(anExit);
        ColonyOfBats colony = new ColonyOfBats();
        colony.moveTo(anExit);
        // Program randomizer: first iteration goes to anExit (occupied),
        // second iteration navigates to anotherExit (empty)
        // nextInt(10)=0 (1 push), nextInt(1)=0 (select anExit),
        // nextInt(10)=0 (1 push), nextInt(2)=1 (select anotherExit)
        Helpers.programRandomizer(0, 0, 0, 1);

        Room result = findRandomRoom(aRoom);

        assertThat(result, is(equalTo(anotherExit)));
    }

}
