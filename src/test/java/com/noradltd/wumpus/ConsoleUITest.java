package com.noradltd.wumpus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ConsoleUITest {

    private ByteArrayOutputStream stdout;

    @BeforeEach
    void setup() {
        stdout = Helpers.captureStdout();
    }

    @AfterEach
    void cleanup() {
        Helpers.resetStdout();
    }

    @Test
    public void showWelcomeDisplaysWelcomeMessage() {
        BufferedReader input = new BufferedReader(new StringReader(""));
        ConsoleUI ui = new ConsoleUI(input);

        ui.showWelcome();

        assertThat(stdout.toString(), containsString("Welcome to Hunt The Wumpus!"));
    }

    @Test
    public void showPromptDisplaysCommandOptions() {
        BufferedReader input = new BufferedReader(new StringReader(""));
        ConsoleUI ui = new ConsoleUI(input);

        ui.showPrompt();

        assertThat(stdout.toString(), containsString("i|l|m|s|t?"));
    }

    @Test
    public void showHelpDisplaysHelpText() {
        BufferedReader input = new BufferedReader(new StringReader(""));
        ConsoleUI ui = new ConsoleUI(input);

        ui.showHelp();

        assertThat(stdout.toString(), containsString("Instructions:"));
        assertThat(stdout.toString(), containsString("move"));
        assertThat(stdout.toString(), containsString("shoot"));
    }

    @Test
    public void showGoodbyeDisplaysGoodbyeMessage() {
        BufferedReader input = new BufferedReader(new StringReader(""));
        ConsoleUI ui = new ConsoleUI(input);

        ui.showGoodbye();

        assertThat(stdout.toString(), containsString("Goodbye"));
    }

    @Test
    public void showScoreDisplaysScore() {
        BufferedReader input = new BufferedReader(new StringReader(""));
        ConsoleUI ui = new ConsoleUI(input);

        ui.showScore("Score: Hunter 1 Wumpus 0");

        assertThat(stdout.toString(), containsString("Score: Hunter 1 Wumpus 0"));
    }

    @Test
    public void showMessageDisplaysArbitraryMessage() {
        BufferedReader input = new BufferedReader(new StringReader(""));
        ConsoleUI ui = new ConsoleUI(input);

        ui.showMessage("Test message");

        assertThat(stdout.toString(), containsString("Test message"));
    }

    @Test
    public void readCommandReturnsUserInput() throws IOException {
        BufferedReader input = new BufferedReader(new StringReader("move 1\n"));
        ConsoleUI ui = new ConsoleUI(input);

        String command = ui.readCommand();

        assertThat(command, is("move 1"));
    }

    @Test
    public void promptPlayAgainReturnsTrueForYes() throws IOException {
        BufferedReader input = new BufferedReader(new StringReader("yes\n"));
        ConsoleUI ui = new ConsoleUI(input);

        boolean result = ui.promptPlayAgain();

        assertThat(result, is(true));
    }

    @Test
    public void promptPlayAgainReturnsFalseForNo() throws IOException {
        BufferedReader input = new BufferedReader(new StringReader("no\n"));
        ConsoleUI ui = new ConsoleUI(input);

        boolean result = ui.promptPlayAgain();

        assertThat(result, is(false));
    }

    @Test
    public void promptPlayAgainReturnsTrueForYPrefix() throws IOException {
        BufferedReader input = new BufferedReader(new StringReader("y\n"));
        ConsoleUI ui = new ConsoleUI(input);

        boolean result = ui.promptPlayAgain();

        assertThat(result, is(true));
    }
}
