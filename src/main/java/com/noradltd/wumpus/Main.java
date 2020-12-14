package com.noradltd.wumpus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


// TODO all untested speculative shit
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
            addCommands((game, arg) -> game.hunter().moveTo(exitNumber(arg)), "m", "move");
            addCommands((game, arg) -> game.hunter().shoot(exitNumber(arg)), "s", "shoot");
            addCommands((game, arg) -> Logger.info(game.hunter().inventory()), "i", "inv", "inventory");
            addCommands((game, arg) -> showHelp(), "?", "h", "help");
            addCommands((game, arg) -> game.isPlaying(), "l", "look");
            // TODO remove the following commands after play-testing
            addCommands((game, arg) -> game.diagnostics(), "show");
            addCommands((game, arg) -> game.hunter().moveTo(findRoom(game.maze(), Integer.parseInt(arg))), "g", "goto");
        }
    };

    private void play(String... options) {
        Game game = null;
        final List<String> record = new ArrayList<>();
        try {
            Logger.info("Welcome to Hunt The Wumpus!");
            showHelp();
            final Pattern pattern = Pattern.compile("\\s*(\\S+)\\s*?(\\d*)?\\s*");
            BufferedReader terminal = new BufferedReader(new InputStreamReader(System.in));
            game = new Game(options);
            while (game.isPlaying()) {
                Command command = COMMANDS.get("what");
                String arg = null;
                Logger.info(game.describe());
                Logger.info("move/shoot?");
                try {
                    final String input = terminal.readLine();
                    record.add(input);
                    Matcher matcher = pattern.matcher(input);
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
            Logger.info("something went terribly wrong.");
        } finally {
            // TODO display score!
            Logger.info("Game Play Record: \n" + record.stream().collect(Collectors.joining("\n")));
        }
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

    private Room findRoom(Maze maze, Integer roomNumber) {
        List<Room> allRooms = collectRoom(maze.entrance(), new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
        return allRooms.stream().filter(room -> room.number() == roomNumber).distinct().collect(Collectors.toList()).get(0);
    }

    private Set<Room> collectRoom(Room room, Set<Room> rooms) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            for (Room exit : room.exits()) {
                if (!rooms.contains(exit)) {
                    collectRoom(exit, rooms);
                }
            }
        }
        return rooms;
    }

    public static void main(String[] args) {
        new Main().play(args);
        Logger.info("Goodbye");
    }
}
