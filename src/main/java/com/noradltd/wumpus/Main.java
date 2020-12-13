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

    private static int exitNumber(String arg) {
        return Integer.parseInt(arg) - 1;
    }

    private static Room findRoom(Maze maze, Integer roomNumber) {
        List<Room> allRooms = collectRoom(maze.entrance(), new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
        return allRooms.stream().filter(room -> room.number() == roomNumber).distinct().collect(Collectors.toList()).get(0);
    }

    private static Set<Room> collectRoom(Room room, Set<Room> rooms) {
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

    private static final Command quitCommand = (game, arg) -> game.quit();
    private static final Command moveCommand = (game, arg) -> game.hunter().moveTo(exitNumber(arg));
    private static final Command shootCommand = (game, arg) -> game.hunter().shoot(exitNumber(arg));
    private static final Command whatCommand = (game, arg) -> System.err.println("What?");
    private static final Command showMeCommand = (game, arg) -> game.diagnostics();
    private static final Command inventoryCommand = (game, arg) -> game.hunter().inventory();
    private static final Command helpCommand = (game, arg) -> showHelp();
    private static final Command lookCommand = (game, arg) -> game.isPlaying();  // this is my no-op
    private static final Command gotoCommand = (game, arg) -> game.hunter().moveTo( findRoom(game.maze(), Integer.parseInt(arg)));
    private static final Map<String, Command> COMMANDS = new HashMap() {{
        for (String exit : new String[]{"q", "x", "quit", "exit"}) put(exit, quitCommand);
        for (String move : new String[]{"m", "move"}) put(move, moveCommand);
        for (String shoot : new String[]{"s", "shoot"}) put(shoot, shootCommand);
        for (String inventory : new String[]{"i", "inv", "inventory"}) put(inventory, inventoryCommand);
        for (String help : new String[]{"?", "h", "help"}) put(help, helpCommand);
        for (String look : new String[]{"l", "look"}) put(look, lookCommand);
        // TODO remove the following commands after play-testing
        for (String showMe : new String[]{"show"}) put(showMe, showMeCommand);
        for (String gotoo : new String[]{"g", "goto"}) put(gotoo, gotoCommand);
    }};

    private static final void showHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Instructions:\n");
        sb.append("(m|move) #\t\t\tMove through tunnel #\n");
        sb.append("(s|shoot) #\t\t\tShoot through tunnel #\n");
        sb.append("(l|look)\t\t\tLook around\n");
        sb.append("(i|inv|inventory)\tShow inventory\n");
        sb.append("(?|h|help)\t\t\tShow help\n");
        sb.append("(q|x|quit|exit)\t\tQuit the game\n");
        // TODO command line options
        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        Game game = null;
        final List<String> record = new ArrayList<>();
        try {
            System.out.println("Welcome to Hunt The Wumpus!");
            showHelp();
            final Pattern pattern = Pattern.compile("\\s*(\\S+)\\s*?(\\d*)?\\s*");
            BufferedReader terminal = new BufferedReader(new InputStreamReader(System.in));
            game = new Game(args);
            while (game.isPlaying()) {
                System.out.println(game.describe());
                System.out.println("move/shoot?");
                try {
                    final String input = terminal.readLine();
                    record.add(input);
                    Matcher matcher = pattern.matcher(input);
                    if (matcher.matches()) {
                        String action = matcher.group(1);
                        String arg = matcher.group(2);
                        COMMANDS.getOrDefault(action, whatCommand).execute(game, arg);
                    } else {
                        System.err.println("What?");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            System.out.println("Goodbye");
        } catch (Throwable thrown) {
            System.err.println("something went terribly wrong.");
        } finally {
            // we want to show the game result BUT this is cheap
            if (game != null ) {
                game.hunter().inventory();
            }
            System.out.println(record.stream().collect(Collectors.joining("\n")));
        }
    }
}
