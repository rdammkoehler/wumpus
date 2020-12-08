package com.noradltd.wumpus;

import org.junit.Test;


import static com.noradltd.wumpus.Helpers.countMazeOccupantsByType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MazePopulaterTest {

    private void checkWumpusPopulation(int roomCount) {
        String[] options = {"--rooms", Integer.toString(roomCount)};
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        assertThat(countMazeOccupantsByType(maze, Wumpus.class), is(roomCount / 7));
    }
    @Test
    public void addsRoomsDivSevenWumpiToTheMaze_21_3() {
        checkWumpusPopulation(21);
    }
    @Test
    public void addsRoomsDivSevenWumpiToTheMaze_14_2() {
        checkWumpusPopulation(14);
    }

    @Test
    public void addsRoomsDivSevenWumpiToTheMaze_10_1() {
        checkWumpusPopulation(10);
    }
}
