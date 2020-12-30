package com.noradltd.wumpus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableMap;

public class Main {

    public static final Pattern USER_COMMAND = Pattern.compile("\\s*(\\S+)\\s*?(\\d*)?\\s*");
    private Game game;
    private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    interface Command {
        void execute(Game game, String arg);
    }

    private final Map<String, Command> COMMANDS = unmodifiableMap(new HashMap<>() {
        @Override
        public Command get(Object key) {
            return super.getOrDefault(key, (game, arg) -> Logger.info("What?"));
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

        private Integer exitNumber(String arg) {
            return Integer.parseInt(arg) - 1;
        }
    });

    private void play(String... options) {
        try {
            Logger.info("Welcome to Hunt The Wumpus!");
            game = new Game(options);
            while (game.isPlaying()) {
                executeUserCommands();
            }
        } catch (Throwable thrown) {
            Logger.error("something went terribly wrong.");
            thrown.printStackTrace(System.err);
        } finally {
            // TODO display score!
            Logger.info("Score: Hunter ? Wumpus ?");
        }
    }

    class CommandArg {
        Command command;
        String arg;

        CommandArg(Command command, String arg) {
            this.command = command;
            this.arg = arg;
        }
    }

    private void executeUserCommands() {
        promptUser();
        CommandArg commandArg = getUserCommand();
        commandArg.command.execute(game, commandArg.arg);
    }

    private CommandArg getUserCommand() {
        Command command = COMMANDS.get("what");
        String arg = null;
        Matcher matcher = USER_COMMAND.matcher(nextCommand());
        if (matcher.matches()) {
            String action = matcher.group(1);
            arg = matcher.group(2);
            command = COMMANDS.get(action);
        }
        return new CommandArg(command, arg);
    }

    private void promptUser() {
        Logger.info(game.toString() + "\n" + "move/shoot?");
    }

    private String nextCommand() {
        try {
            return input.readLine();
        } catch (IOException e) {
            return "help";
        }
    }

    private void showHelp() {
        // TODO what about command line args?
        Logger.info("Instructions:\n" +
                "(m|move) #\t\t\tMove through tunnel #\n" +
                "(s|shoot) #\t\t\tShoot through tunnel #\n" +
                "(l|look)\t\t\tLook around\n" +
                "(i|inv|inventory)\tShow inventory\n" +
                "(?|h|help)\t\t\tShow help\n" +
                "(q|x|quit|exit)\t\tQuit the game\n");
    }

    public static void main(String[] args) {
        new Main().play(args);
        // TODO play again?
        Logger.info("Goodbye");
    }
}
