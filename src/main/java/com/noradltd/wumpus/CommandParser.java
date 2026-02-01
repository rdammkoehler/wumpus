package com.noradltd.wumpus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableMap;

class CommandParser {

    private static final Pattern USER_COMMAND = Pattern.compile("\\s*(\\S+)\\s*?(\\d*)?\\s*");

    interface Command {
        void execute(String arg);
    }

    record ParsedCommand(Command command, String arg) {
    }

    private final Map<String, Command> commands;
    private final Command askUserWhat;

    CommandParser(Game game, ConsoleUI ui) {
        this.commands = buildCommands(game, ui);
        this.askUserWhat = commands.get("what");
    }

    // TODO [Code Smell] Anonymous inner class with overridden get() and instance initializer block
    //   is clever but hard to read and maintain. Consider:
    //   Remediation: Use a simple CommandRegistry class with explicit register() method calls,
    //   or use Map.ofEntries() with a separate default command wrapper.
    private Map<String, Command> buildCommands(Game game, ConsoleUI ui) {
        return unmodifiableMap(new HashMap<>() {
            @Override
            public Command get(Object key) {
                return super.getOrDefault(key, arg -> ui.showMessage("What?"));
            }

            private void addCommands(Command command, String... inputStrings) {
                Arrays.asList(inputStrings).forEach(inputString -> put(inputString, command));
            }

            {
                addCommands(arg -> game.quit(), "q", "x", "quit", "exit");
                addCommands(arg -> game.move(exitNumber(arg)), "m", "move");
                addCommands(arg -> game.shoot(exitNumber(arg)), "s", "shoot");
                addCommands(arg -> ui.showMessage(game.inventory()), "i", "inv", "inventory");
                addCommands(arg -> ui.showHelp(), "?", "h", "help");
                addCommands(arg -> ui.showMessage(game.toString()), "l", "look");
                addCommands(arg -> game.takeArrow(), "t", "take");
                addCommands(arg -> game.vizualize(), "v", "viz"); // hidden
                addCommands(arg -> game.saveState(), "r", "remember");
                addCommands(arg -> game.requestLoad(), "load");
            }
        });
    }

    ParsedCommand parse(String input) {
        Matcher matcher = USER_COMMAND.matcher(input);
        if (matcher.matches()) {
            String cmd = matcher.group(1).toLowerCase();
            String arg = matcher.group(2).toLowerCase();
            return new ParsedCommand(commands.get(cmd), arg);
        }
        return new ParsedCommand(askUserWhat, null);
    }

    void execute(ParsedCommand cmd) {
        Logger.debug("executing command");
        cmd.command().execute(cmd.arg());
        Logger.debug("==========");
    }

    // TODO [Exception Handling] Catching generic Exception is too broad.
    //   Remediation: Catch NumberFormatException specifically since that's what parseInt throws.
    private static Integer exitNumber(String arg) {
        try {
            return Integer.parseInt(arg) - 1;
        } catch (Exception ex) {
            return -1;
        }
    }
}
