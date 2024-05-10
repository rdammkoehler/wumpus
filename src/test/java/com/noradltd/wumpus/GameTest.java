package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.matchesRegex;

public class GameTest {

    @Test
    public void getScoreReportsTheScore() {
        Game game = new Game(new String[]{});

        assertThat(game.getScore(), matchesPattern("Score: Hunter \\d+ Wumpus \\d+"));
    }

    @Test
    public void getHelp() {
        String regex = ".*\\t--arrows #\\t\\tLimit the number of arrows\\n" +
                "\\t--bats #\\t\\tLimit the number of colonies of bats\\n" +
                "\\t--pits #\\t\\tLimit the number of bottomless pits\\n" +
                "\\t--rooms #\\t\\tLimit the number of rooms\\n" +
                "\\t--seed  #\\t\\tSet the Randomizer seed\\n" +
                "\\t--wumpi  #\\t\\tLimit the number of wumpi\\n.*";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            new Game(new String[]{"--help"});

            assertThat(stdout.toString(), matchesRegex(pattern));
        } finally {
            Helpers.resetStdout();
        }
    }

    @Test
    public void warnsAboutUnknownOptions() {
        String regex = ".*cli: unknown argument .*";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            new Game(new String[]{"--verbose", "true"});

            assertThat(stdout.toString(), matchesRegex(pattern));
        } finally {
            Helpers.resetStdout();
        }
    }
}
