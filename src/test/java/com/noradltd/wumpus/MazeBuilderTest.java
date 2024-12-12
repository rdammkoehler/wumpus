package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.noradltd.wumpus.Helpers.getAllRooms;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MazeBuilderTest {

    @Test
    public void testRequiresOptions() {
        assertThrows(NullPointerException.class, () -> MazeBuilder.build(null));
    }

    @Test
    public void testBuildsMaze() {
        Maze maze = MazeBuilder.build();

        assertThat(maze, notNullValue(Maze.class));
    }

    @Test
    public void testDefaultRoomCount() {
        assertThat(getAllRooms(MazeBuilder.build()).size(), is(Game.Options.DEFAULT_ROOM_COUNT));
    }

    @Test
    public void testCustomRoomCount() {
        int customRoomCount = 10;
        Game.Options options = new Game.Options("--rooms", Integer.toString(customRoomCount));

        assertThat(getAllRooms(MazeBuilder.build(options)).size(), is(customRoomCount));
    }

    @Test
    public void testObeysMaxExits() {
        int exitLimit = 6;
        Game.Options options = new Game.Options("--max_exits", Integer.toString(exitLimit));

        List<Room> rooms = getAllRooms(MazeBuilder.build(options));

        int maxExits = -1;
        for (Room room : rooms) {
            List<Room> exits = room.exits();
            int exitCt = exits.size();

            assertThat(exitLimit, lessThanOrEqualTo(exitLimit));

            maxExits = Math.max(exitCt, maxExits);
        }
        assertThat(maxExits, is(exitLimit));
    }
}
