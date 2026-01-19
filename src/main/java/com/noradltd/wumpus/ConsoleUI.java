package com.noradltd.wumpus;

import java.io.BufferedReader;
import java.io.IOException;

class ConsoleUI {

    private static final String HELP_TEXT = """
            Instructions:
            (i|inv|inventory)\tShow inventory
            (l|look)\t\t\tLook around
            (m|move) #\t\t\tMove through tunnel #
            (s|shoot) #\t\t\tShoot through tunnel #
            (t|take)\t\t\tTake (an unbroken arrow)
            (?|h|help)\t\t\tShow help
            (r|remember)\t\t\tSave game state
            (l|load)\t\t\tReload game state
            (q|x|quit|exit)\t\tQuit the game
            """;

    private final BufferedReader input;

    ConsoleUI(BufferedReader input) {
        this.input = input;
    }

    void showWelcome() {
        Logger.info("Welcome to Hunt The Wumpus!");
    }

    void showPrompt() {
        Logger.info("i|l|m|s|t?");
    }

    void showHelp() {
        Logger.info(HELP_TEXT);
    }

    void showGoodbye() {
        Logger.info("Goodbye");
    }

    void showScore(String score) {
        Logger.info(score);
    }

    void showMessage(String message) {
        Logger.info(message);
    }

    String readCommand() throws IOException {
        Logger.debug("waiting for input");
        return input.readLine();
    }

    boolean promptPlayAgain() throws IOException {
        Logger.info("Play again? (yes/[no])");
        String yesOrNo = input.readLine();
        Logger.debug("Player responded with \"" + yesOrNo + "\"");
        return yesOrNo.toLowerCase().charAt(0) == 'y';
    }
}
