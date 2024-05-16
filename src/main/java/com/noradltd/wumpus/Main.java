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

    private static final Pattern USER_COMMAND = Pattern.compile("\\s*(\\S+)\\s*?(\\d*)?\\s*");
    private Game game;
    private final BufferedReader input;

    private interface Command {
        void execute(String arg);
    }

    private final Map<String, Command> COMMANDS = unmodifiableMap(new HashMap<>() {
        @Override
        public Command get(Object key) {
            return super.getOrDefault(key, arg -> Logger.info("What?"));
        }

        private void addCommands(Command command, String... inputStrings) {
            Arrays.asList(inputStrings).forEach(inputString -> put(inputString, command));
        }

        {
            addCommands(arg -> game.quit(), "q", "x", "quit", "exit");
            addCommands(arg -> game.move(exitNumber(arg)), "m", "move");
            addCommands(arg -> game.shoot(exitNumber(arg)), "s", "shoot");
            addCommands(arg -> Logger.info(game.inventory()), "i", "inv", "inventory");
            addCommands(arg -> showHelp(), "?", "h", "help");
            addCommands(arg -> Logger.info(game.toString()), "l", "look");
            addCommands(arg -> game.takeArrow(), "t", "take");
        }

        private Integer exitNumber(String arg) {
            try {
                return Integer.parseInt(arg) - 1;
            } catch (Exception ex) {
                return -1;
            }
        }
    });
    private final Command ASK_USER_WHAT = COMMANDS.get("what");

    private Main(BufferedReader input) {
        this.input = input;
    }

    private void play(String... options) throws IOException {
        Logger.info("Welcome to Hunt The Wumpus!");
        try {
            game = new Game(options);
            while (game.isPlaying()) {
                promptUser();
                executeUserCommand(nextCommand());
            }
        } finally {
            Logger.info(game.getScore());
        }
    }

    private String nextCommand() throws IOException {
            return input.readLine();
    }

    private void executeUserCommand(String command) {
        Matcher matcher = USER_COMMAND.matcher(command);
        if (matcher.matches()) {
            COMMANDS.get(matcher.group(1).toLowerCase()).execute(matcher.group(2).toLowerCase());
        } else {
            ASK_USER_WHAT.execute(null);
        }
    }

    private void promptUser() {
        Logger.info("i|l|m|s|t?");
    }

    private void showHelp() {
        Logger.info("""
                Instructions:
                (i|inv|inventory)\tShow inventory
                (l|look)\t\t\tLook around
                (m|move) #\t\t\tMove through tunnel #
                (s|shoot) #\t\t\tShoot through tunnel #
                (t|take)\t\t\tTake (an unbroken arrow)
                (?|h|help)\t\t\tShow help
                (q|x|quit|exit)\t\tQuit the game
                """);
    }

    public static void main(String[] args) {
        try (final BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            do new Main(input).play(args); while (promptToPlayAgain(input));
        } catch (IOException ioException) {
            Logger.debug("System Failure", ioException);
        } finally {
            Logger.info("Goodbye");
        }
    }

    private static boolean promptToPlayAgain(BufferedReader input) throws IOException {
        Logger.info("Play again? (yes/[no])");
        String yesOrNo = input.readLine();
        return yesOrNo.toLowerCase().charAt(0) == 'y';
    }
}
