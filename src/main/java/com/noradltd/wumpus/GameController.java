package com.noradltd.wumpus;

import java.io.IOException;

class GameController {

    private final ConsoleUI ui;
    private final String[] options;

    GameController(ConsoleUI ui, String[] options) {
        this.ui = ui;
        this.options = options;
    }

    void play() throws IOException {
        ui.showWelcome();
        Game game = new Game(options);
        CommandParser parser = new CommandParser(game, ui);
        try {
            while (game.isPlaying()) {
                ui.showPrompt();
                parser.execute(parser.parse(ui.readCommand()));
            }
            Logger.debug("not playing");
        } finally {
            ui.showScore(game.getScore());
        }
    }
}
