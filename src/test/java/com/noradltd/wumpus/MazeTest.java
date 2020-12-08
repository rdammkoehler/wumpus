package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MazeTest {
    @Test
    public void testMazeDefaultsTo20Rooms() {
        Maze maze = MazeBuilder.build();

        assertThat(countRooms(maze.entrance()), is(equalTo(20)));
    }

    @Test
    public void testMazeRoomsCanBeLimitedWithOptions() {
        Integer roomLimit = 5;
        String[] options = {"--rooms", roomLimit.toString()};
        Maze maze = MazeBuilder.build(options);

        assertThat(countRooms(maze.entrance()), is(equalTo(roomLimit)));
    }

    @Test
    public void testMazeBuildersRandomSeedCanBeSetWithOptions() {
        final boolean[] seedSetDetected = {false};
        Game.getThreadLocalBag().replace("randomizer", new Random() {
            @Override
            void setSeed(long seed) {
                super.setSeed(seed);
                seedSetDetected[0] = true;
            }
        });
        String[] options = {"--seed", Integer.toString(0)};

        MazeBuilder.build(options);

        assertThat(seedSetDetected[0], is(true));
    }

    @Test
    public void testMazeBuilderIgnoresSingleOptionWithNoValue() {
        String[] options = {"--blarg"};

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderIgnoresOptionsItDoesntRecognize() {
        String[] options = {"--blarg", "hack"};

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderIgnoresTheLastOddOption() {
        String[] options = {"--rooms", "2", "--blarg"};

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderIgnoresExtraOptions() {
        String[] options = {"--rooms", "2", "--blarg", "100"};

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderPrintsHelpWhenAsked() {
        ByteArrayOutputStream stdout = captureStdout();
        String[] options = {"--help"};
        String helpStatement = "\t--rooms #\t\tLimit the number or rooms\n" +
                "\t--seed  #\t\tSet the Randomizer seed\n" +
                "\t--format $\t\tSet the output format (human, dot, neato)" +
                "\n";

        MazeBuilder.build(options);

        assertThat(stdout.toString(), is(equalTo(helpStatement)));

        resetStdout();
    }

    @Test
    public void testToStringReturnsOutputFormattedForHumansByDefault() {
        String[] options = {"--rooms", "2"};
        String humanReadableOutput = "Room 30: Has exits 31\n" +
                "*****\n" +
                "Room 31: Has exits 30\n" +
                "*****\n";

        assertThat(MazeBuilder.build(options).toString(), is(equalTo(humanReadableOutput)));
    }

    @Test
    public void testToStringReturnsOutputFormattedForHumansByWhenRequested() {
        String[] options = {"--format", "human", "--rooms", "2"};
        String humanReadableOutput = "Room 34: Has exits 35\n" +
                "*****\n" +
                "Room 35: Has exits 34\n" +
                "*****\n";

        assertThat(MazeBuilder.build(options).toString(), is(equalTo(humanReadableOutput)));
    }

    @Test
    public void testToStringReturnsDOTFormatWhenRequested() {
        String[] options = {"--format", "dot", "--rooms", "2"};
        String dotOutput = "digraph G {\n" +
                "\t28 -> 29;\n" +
                "}\n";

        assertThat(MazeBuilder.build(options).toString(), is(equalTo(dotOutput)));
    }

    @Test
    public void testToStringReturnsNEATOFormatWhenRequested() {
        String[] options = {"--format", "neato", "--rooms", "2"};
        String neatoOutput = "graph G {\n" +
                "\t22 -- 23;\n" +
                "}\n";

        assertThat(MazeBuilder.build(options).toString(), is(equalTo(neatoOutput)));
    }
}
