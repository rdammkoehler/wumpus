package com.noradltd.wumpus;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.captureStdout;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ScoreDisplayTest {
    class Game {
        void quit() {
            System.out.println("Moves Made:\t\t\t60");
            System.out.println("Wumpi Scalps:\t\t2");
            System.out.println("Arrows Remaining:\t5");
            System.out.println("Hunters Killed:\t\t0");
            System.out.println("Game Over:");
        }
    }

    @Test
    public void scoreHeaderPresentAtEndOfGame() {
        ByteArrayOutputStream out = captureStdout();
        Game game = new Game();

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Game Over:"));
    }

    @Test
    public void scoreContainsWumpusScalpsCount() {
        ByteArrayOutputStream out = captureStdout();
        Game game = new Game();

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Wumpi Scalps:\t\t2"));
    }

    @Test
    public void scoreContainsArrowsRemaining() {
        ByteArrayOutputStream out = captureStdout();
        Game game = new Game();

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Arrows Remaining:\t5"));
    }

    @Test
    public void scoreContainsMovesMade() {
        ByteArrayOutputStream out = captureStdout();
        Game game = new Game();

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Moves Made:\t\t\t60"));
    }

    @Test
    public void scoreContainsHuntersKilled() {
        ByteArrayOutputStream out = captureStdout();
        Game game = new Game();

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Hunters Killed:\t\t0"));
    }
}
