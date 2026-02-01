package com.noradltd.wumpus;

import java.io.IOException;
import java.util.Arrays;

class GameController {

    private final ConsoleUI ui;
    private final String[] options;

    GameController(ConsoleUI ui, String[] options) {
        this.ui = ui;
        this.options = options;
    }

    void play() throws IOException {
        ui.showWelcome();
        // TODO: The Game and CommandParser are created within this method.
        // For better testability, these should ideally be injected as dependencies.
        Game game = createOrLoadGame();
        CommandParser parser = new CommandParser(game, ui);
        try {
            while (game.isPlaying()) {
                ui.showPrompt();
                parser.execute(parser.parse(ui.readCommand()));
                // TODO: Law of Demeter Principle violation and tight coupling.
                // The GameController is reaching into the Game object to get its internal state.
                // The Game class should instead notify the GameController about a pending load,
                // or provide a method to apply a loaded game state.
                // Check if a load was requested
                if (game.getPendingLoad() != null) {
                    game = game.getPendingLoad();
                    parser = new CommandParser(game, ui);
                }
            }
            Logger.debug("not playing");
        } finally {
            ui.showScore(game.getScore());
        }
    }

    private Game createOrLoadGame() {
        if (shouldLoadGame()) {
            Game loaded = Game.loadState();
            if (loaded != null) {
                return loaded;
            }
            Logger.info("Starting new game instead");
        }
        return new Game(options);
    }

    private boolean shouldLoadGame() {
        return Arrays.asList(options).contains("--load");
    }
}
