package com.noradltd.wumpus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.matchesRegex;

public class MainTest {

    public static final String QUIT_Q = "q";
    public static final String QUIT_X = "x";
    public static final String QUIT_QUIT = "quit";
    public static final String QUIT_EXIT = "exit";
    public static final String MOVE_M = "m";
    public static final String MOVE_MOVE = "move";
    public static final String SHOOT_S = "s";
    public static final String SHOOT_SHOOT = "shoot";
    public static final String INVENTORY_I = "i";
    public static final String INVENTORY_INV = "inv";
    public static final String INVENTORY_INVENTORY = "inventory";
    public static final String HELP_QMARK = "?";
    public static final String HELP_H = "h";
    public static final String HELP_HELP = "help";
    public static final String LOOK_L = "l";
    public static final String LOOK_LOOK = "look";

    @BeforeAll
    public static final void beforeAllTests() {
    }

    @BeforeEach
    public void beforeEach() {
        Helpers.resetRandomizer();
        Helpers.restartRoomNumberer();
    }

    private String playInstructions(String instructions) {
        InputStream originalStdin = System.in;
        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            System.setIn(new ByteArrayInputStream(instructions.getBytes()));
            Main.main(new String[]{});
            return stdout.toString();
        } finally {
            Helpers.resetStdout();
            System.setIn(originalStdin);
        }
    }

    private void assertThat(String reOutput, String... instructions) {
        Pattern pattern = Pattern.compile(reOutput, Pattern.DOTALL);
        String allInstructions = String.join("\n", instructions) + "\n";

        String actual = playInstructions(allInstructions);

        org.hamcrest.MatcherAssert.assertThat(actual, matchesRegex(pattern));
    }

    @Test
    public void theGameWelcomesThePlayer() {
        assertThat("^- Welcome to Hunt The Wumpus.*", QUIT_QUIT);
    }

    @Test
    public void theGameStartsInRoomTwelve() {
        assertThat(".*You are in room #12.*", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {QUIT_Q, QUIT_X, QUIT_QUIT, QUIT_EXIT})
    public void quitingShowsAGoodbyeMessage(String quitCommand) {
        assertThat(".*Goodbye\\n$", quitCommand);
    }

    @ParameterizedTest
    @ValueSource(strings = {MOVE_M, MOVE_MOVE})
    public void theHunterCanMoveAround(String moveCommand) {
        assertThat(".*You are in room #10.*", moveCommand + " 1", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {SHOOT_S, SHOOT_SHOOT})
    public void theHunterCanShootArrows(String shootCommand) {
        assertThat(".*- Your arrow hurtles down tunnel 1.*", shootCommand + " 1", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {INVENTORY_I, INVENTORY_INV, INVENTORY_INVENTORY})
    public void thePlayerCanInspectTheHuntersInventory(String inventoryCommand) {
        assertThat(".*Inventory.*", inventoryCommand, QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {HELP_QMARK, HELP_H, HELP_HELP})
    public void thePlayerCanAskForHelp(String helpCommand) {
        assertThat(".*Instructions:.*", helpCommand, QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {LOOK_L, LOOK_LOOK})
    public void thePlayerCanRepeatTheRoomDescription(String lookCommand) {
        assertThat(".*You are in room #12.*You are in room #12.*", lookCommand, QUIT_QUIT);
    }

    @Test
    public void badCommandsGetAWhatPrompt() {
        assertThat(".*- What?.*", "bad command", QUIT_QUIT);
    }

    @Test
    public void nonNumericArgumentsGetAWhatPrompt() {
        assertThat(".*- What?.*", "move faraway", QUIT_QUIT);
    }
}
