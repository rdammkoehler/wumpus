package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static com.noradltd.wumpus.MazeSteps.countRooms;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MazeTest {
    @Test
    public void testMazeDefaultsTo20Rooms() {
        MazeBuilder.Maze maze = MazeBuilder.build();
        assertThat(countRooms(maze.firstRoom()), is(equalTo(20)));
    }
}
