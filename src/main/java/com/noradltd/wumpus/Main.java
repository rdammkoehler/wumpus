package com.noradltd.wumpus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            ConsoleUI ui = new ConsoleUI(input);
            do {
                new GameController(ui, args).play();
            } while (ui.promptPlayAgain());
        } catch (IOException ioException) {
            Logger.error("System Failure", ioException);
        } finally {
            Logger.info("Goodbye");
        }
    }
}
