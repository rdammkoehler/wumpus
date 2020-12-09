package com.noradltd.wumpus;

import org.junit.Test;

import static com.noradltd.wumpus.Helpers.countMazeOccupantsByType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MazePopulaterTest {

    private void checkWumpusPopulation(int roomCount) {
        String[] options = {"--rooms", Integer.toString(roomCount)};
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        assertThat(countMazeOccupantsByType(maze, Wumpus.class), is(Math.max(1, roomCount / 7)));
    }

    @Test
    public void addsThreeWumpiIntoAMazeOfTwentyOneRooms() {
        checkWumpusPopulation(21);
    }

    @Test
    public void addsTwoWumpiIntoAMazeOfFourteenRooms() {
        checkWumpusPopulation(14);
    }

    @Test
    public void addOneWumpiIntoAMazeOfTenRooms() {
        checkWumpusPopulation(10);
    }

    @Test
    public void addOneWumpiIntoAMazeOfFiveRooms() {
        checkWumpusPopulation(5);
    }

    // TODO there is probably some edge case around 1 room

    private void checkPitPopulation(int roomCount) {
        String[] options = {"--rooms", Integer.toString(roomCount)};
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        assertThat(countMazeOccupantsByType(maze, BottomlessPit.class), is(Math.max(1, roomCount / 5)));
    }

    @Test
    public void addsFourBottomlessPitsIntoAMazeOfTwentyRooms() {
        checkPitPopulation(20);
    }

    @Test
    public void addsOneBottomlessPitIntoAMazeOfThreeRooms() {
        checkPitPopulation(3);
    }

    // TODO figure out how to get into a situation with
}
