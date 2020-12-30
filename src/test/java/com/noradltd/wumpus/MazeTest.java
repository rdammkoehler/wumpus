package com.noradltd.wumpus;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MazeTest {
    @Test
    public void testMazeDefaultsTo20Rooms() {
        Maze maze = MazeBuilder.build();

        assertThat(countRooms(maze), is(equalTo(20)));
    }

    @Test
    public void testMazeRoomsCanBeLimitedWithOptions() {
        Integer roomLimit = 5;
        Game.Options options = new Game.Options("--rooms", roomLimit.toString());
        Maze maze = MazeBuilder.build(options);

        assertThat(countRooms(maze), is(equalTo(roomLimit)));
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
        Game.Options options = new Game.Options("--seed", Integer.toString(0));

        MazeBuilder.build(options);

        assertThat(seedSetDetected[0], is(true));
    }

    @Test
    public void testMazeBuilderIgnoresSingleOptionWithNoValue() {
        Game.Options options = new Game.Options("--blarg");

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderIgnoresOptionsItDoesntRecognize() {
        Game.Options options = new Game.Options("--blarg", "hack");

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderIgnoresTheLastOddOption() {
        Game.Options options = new Game.Options("--rooms", "2", "--blarg");

        MazeBuilder.build(options);
    }

    @Test
    public void testMazeBuilderIgnoresExtraOptions() {
        Game.Options options = new Game.Options("--rooms", "2", "--blarg", "100");

        MazeBuilder.build(options);
    }

    @Ignore("come back and update this when you finalize the output")
    public void testMazeBuilderPrintsHelpWhenAsked() {
        ByteArrayOutputStream stdout = captureStdout();
        Game.Options options = new Game.Options("--help");
        String helpStatement = "\t--arrows #\t\tLimit the number of arrows\n" +
                "\t--bats #\t\tLimit the number of colonies of bats\n" +
                "\t--format $\t\tSet the output format (human, dot, neato)" +
                "\t--pits #\t\tLimit the number of bottomless pits\n" +
                "\t--rooms #\t\tLimit the number of rooms\n" +
                "\t--seed  #\t\tSet the Randomizer seed\n" +
                "\t--wumpi  #\t\tLimit the number of wumpi\n";

        MazeBuilder.build(options);

        assertThat(stdout.toString(), is(equalTo(helpStatement)));

        resetStdout();
    }

    @Test
    public void aMazeMustHaveTwoRooms() {
        Game.Options options = new Game.Options("--rooms", "0");

        assertThat(countRooms(MazeBuilder.build(options)), is(equalTo(2)));
    }
}
