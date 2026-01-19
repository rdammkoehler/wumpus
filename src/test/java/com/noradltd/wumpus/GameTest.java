package com.noradltd.wumpus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ResetRandomizerExtension.class)
public class GameTest {

    private static final String SAVE_FILE = "game_state.json";

    @BeforeEach
    void setup() {
        new File(SAVE_FILE).delete();
    }

    @AfterEach
    void cleanup() {
        new File(SAVE_FILE).delete();
        Helpers.restartRoomNumberer();
    }

    @Test
    public void saveStateCreatesJsonFile() {
        Game game = new Game(new String[]{"--rooms", "5", "--seed", "42"});

        game.saveState();

        File saveFile = new File(SAVE_FILE);
        assertTrue(saveFile.exists(), "Save file should exist");
        assertTrue(saveFile.length() > 0, "Save file should not be empty");
    }

    @Test
    public void loadStateRestoresGame() {
        Game original = new Game(new String[]{"--rooms", "5", "--seed", "42"});
        original.saveState();

        Game loaded = Game.loadState();

        assertNotNull(loaded, "Loaded game should not be null");
        assertThat(loaded.toString(), containsString("You are in room"));
    }

    @Test
    public void hasSavedGameReturnsTrueWhenSaveExists() {
        Game game = new Game(new String[]{"--rooms", "5", "--seed", "42"});

        assertFalse(Game.hasSavedGame());
        game.saveState();
        assertTrue(Game.hasSavedGame());
    }

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
