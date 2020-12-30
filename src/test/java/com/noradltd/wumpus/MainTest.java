package com.noradltd.wumpus;

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
    public static final String TAKE_T = "t";
    public static final String TAKE_TAKE = "take";

    @BeforeEach
    public void beforeEach() {
        Helpers.resetRandomizer();
        Helpers.restartRoomNumberer();
    }

    private String playInstructions(String instructions) {
        ByteArrayOutputStream stdout = Helpers.captureStdout();
        InputStream originalStdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(instructions.getBytes()));
            Main.main(new String[]{"--seed", "0", "--arrows", "20"});
            return stdout.toString();
        } finally {
            System.setIn(originalStdin);
            Helpers.resetStdout();
        }
    }

    private void assertThat(String reOutput, String... instructions) {
        final String regex = preProcessRegularExpression(reOutput);
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        String allInstructions = String.join("\n", instructions) + "\n";

        String actual = playInstructions(allInstructions);

        org.hamcrest.MatcherAssert.assertThat(actual, matchesRegex(pattern));
    }

    private String preProcessRegularExpression(String reOutput) {
        final String regex;
        if (reOutput.startsWith("^") && reOutput.endsWith("$")) {
            regex = reOutput;
        } else if (reOutput.startsWith("^") && !reOutput.endsWith("$")) {
            regex = reOutput + ".*";
        } else if (!reOutput.startsWith("^") && reOutput.endsWith("$")) {
            regex = ".*" + reOutput;
        } else {
            regex = ".*" + reOutput + ".*";
        }
        return regex;
    }

    @Test
    public void theGameWelcomesThePlayer() {
        assertThat("^- Welcome to Hunt The Wumpus", QUIT_QUIT);
    }

    @Test
    public void theGameStartsInRoomTwelve() {
        assertThat("You are in room #12", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {QUIT_Q, QUIT_X, QUIT_QUIT, QUIT_EXIT})
    public void quitingShowsAGoodbyeMessage(String quitCommand) {
        assertThat("Goodbye\\n$", quitCommand);
    }

    @ParameterizedTest
    @ValueSource(strings = {MOVE_M, MOVE_MOVE})
    public void theHunterCanMoveAround(String moveCommand) {
        assertThat("You are in room #10", moveCommand + " 1", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {SHOOT_S, SHOOT_SHOOT})
    public void theHunterCanShootArrows(String shootCommand) {
        assertThat("- Your arrow hurtles down tunnel 1", shootCommand + " 1", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {INVENTORY_I, INVENTORY_INV, INVENTORY_INVENTORY})
    public void thePlayerCanInspectTheHuntersInventory(String inventoryCommand) {
        assertThat("Inventory", inventoryCommand, QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {HELP_QMARK, HELP_H, HELP_HELP})
    public void thePlayerCanAskForHelp(String helpCommand) {
        assertThat("Instructions:", helpCommand, QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {LOOK_L, LOOK_LOOK})
    public void thePlayerCanRepeatTheRoomDescription(String lookCommand) {
        assertThat("You are in room #12.*You are in room #12", lookCommand, QUIT_QUIT);
    }

    @Test
    public void badCommandsGetAWhatPrompt() {
        assertThat("- What?", "bad command", QUIT_QUIT);
    }

    @Test
    public void nonNumericArgumentsGetAWhatPrompt() {
        assertThat("- What?", "move faraway", QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"t", "take"})
    public void huntersCanPickupUnbrokenArrows(String takeCommand) {
        assertThat("You collect an unbroken arrow off the floor.", SHOOT_SHOOT + " 1", MOVE_MOVE + " 1", takeCommand, QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {TAKE_T, TAKE_TAKE})
    public void huntersCannotPickupBrokenArrows(String takeCommand) {
        // TODO the excessive number of shots is because we can't controll the randomizer!
        assertThat("The broken arrow crumbles in your hand.", SHOOT_SHOOT + " 1",
                SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1",
                SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1",
                SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1",
                SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", SHOOT_SHOOT + " 1", MOVE_MOVE + " 1",
                takeCommand, QUIT_QUIT);
    }

    @ParameterizedTest
    @ValueSource(strings = {HELP_QMARK, HELP_H, HELP_HELP, LOOK_L, LOOK_LOOK, TAKE_T, TAKE_TAKE, MOVE_M, MOVE_MOVE, SHOOT_S, SHOOT_SHOOT, QUIT_Q, QUIT_QUIT, QUIT_EXIT, QUIT_X, INVENTORY_I, INVENTORY_INV, INVENTORY_INVENTORY})
    public void upperCaseCommandsWorkToo(String command) {
        assertThat("[^What]", command, QUIT_QUIT);
    }
}
