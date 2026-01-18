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
        Game game = createOrLoadGame();
        if (game == null) {
            return;
        }
        CommandParser parser = new CommandParser(game, ui);
        try {
            while (game.isPlaying()) {
                ui.showPrompt();
                parser.execute(parser.parse(ui.readCommand()));
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
