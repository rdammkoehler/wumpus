package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static com.noradltd.wumpus.Helpers.countRooms;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MazeTest {
    @Test
    public void testMazeDefaultsTo20Rooms() {
        Maze maze = MazeBuilder.build();
        System.out.println(maze);
        assertThat(countRooms(maze.currentRoom()), is(equalTo(20)));
    }
}
