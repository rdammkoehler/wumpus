package com.noradltd.wumpus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    interface Command {
        void execute(Game game, String arg);
    }

    private final Map<String, Command> COMMANDS = new HashMap() {
        @Override
        public Object get(Object key) {
            return super.getOrDefault(key, (Command) (game, arg) -> Logger.info("What?"));
        }

        private void addCommands(Command command, String... inputStrings) {
            Arrays.asList(inputStrings).forEach(inputString -> this.put(inputString, command));
        }

        {
            addCommands((game, arg) -> game.quit(), "q", "x", "quit", "exit");
            addCommands((game, arg) -> game.move(exitNumber(arg)), "m", "move");
            addCommands((game, arg) -> game.shoot(exitNumber(arg)), "s", "shoot");
            addCommands((game, arg) -> Logger.info(game.inventory()), "i", "inv", "inventory");
            addCommands((game, arg) -> showHelp(), "?", "h", "help");
            addCommands((game, arg) -> game.isPlaying(), "l", "look");
        }
    };

    void play(InputStream inputStream, String... options) {
        try {
            Logger.info("Welcome to Hunt The Wumpus!");
            final Pattern pattern = Pattern.compile("\\s*(\\S+)\\s*?(\\d*)?\\s*");
            Game game = new Game(options);
            while (game.isPlaying()) {
                Command command = COMMANDS.get("what");
                String arg = null;
                Logger.info(game.toString());
                Logger.info("move/shoot?");
                try {
                    Matcher matcher = pattern.matcher(nextCommand(inputStream));
                    if (matcher.matches()) {
                        String action = matcher.group(1);
                        arg = matcher.group(2);
                        command = COMMANDS.get(action);
                    }
                } catch (IOException ex) {
                    Logger.info("IO error");
                } finally {
                    command.execute(game, arg);
                }
            }
        } catch (Throwable thrown) {
            thrown.printStackTrace(System.err);
            Logger.info("something went terribly wrong.");
        } finally {
            // TODO display score!
        }
    }

    private String nextCommand(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int data = inputStream.read();
        while (data != 10) {
            sb.append((char) data);
            data = inputStream.read();
        }
        return sb.toString();
    }

    private void showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Instructions:\n");
        sb.append("(m|move) #\t\t\tMove through tunnel #\n");
        sb.append("(s|shoot) #\t\t\tShoot through tunnel #\n");
        sb.append("(l|look)\t\t\tLook around\n");
        sb.append("(i|inv|inventory)\tShow inventory\n");
        sb.append("(?|h|help)\t\t\tShow help\n");
        sb.append("(q|x|quit|exit)\t\tQuit the game\n");
        // TODO command line options
        Logger.info(sb.toString());
    }

    private int exitNumber(String arg) {
        return Integer.parseInt(arg) - 1;
    }


    public static void main(String[] args) {
        new Main().play(System.in, args);
        // TODO play again?
        Logger.info("Goodbye");
    }
}
