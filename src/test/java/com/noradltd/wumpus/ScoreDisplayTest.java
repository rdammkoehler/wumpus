package com.noradltd.wumpus;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.captureStdout;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ScoreDisplayTest {
    class Game {
        void quit() {
            System.out.println("Moves Made:");
            System.out.println("Wumpi Scalps:");
            System.out.println("Arrows Remaining:");
            System.out.println("Hunters Killed:");
            System.out.println("Game Over");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Moves Made:", "Wumpi Scalps:", "Arrows Remaining:", "Hunters Killed:", "Game Over"})
    void scoreContainsString(String string) {
        ByteArrayOutputStream out = captureStdout();
        Game game = new Game();

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString(string));
    }
}
