package com.noradltd.wumpus;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

class Game implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String SAVE_FILE = "game_state";

    private final Hunter hunter;
    private final Maze maze;
    private boolean playing = true;

    Game(String[] options) {
        Game.Options gameOptions = new Game.Options(options);
        Logger.debug(gameOptions + "\n******");
        maze = MazeLoader.populate(MazeBuilder.build(gameOptions), gameOptions);
        hunter = new Hunter(new ArrowQuiver(gameOptions.getInitialArrowCount()));
        Logger.debug("Placing Hunter in room " + maze.entrance().number());
        hunter.moveTo(maze.entrance());
        Logger.info(toString());
    }

    void vizualize() {
        Visualizer.visualize(maze);
    }

    public void move(Integer exitIndex) {
        hunter.moveTo(exitIndex);
        Logger.info(toString());
    }

    public void shoot(Integer exitIndex) {
        hunter.shoot(exitIndex);
    }

    public String inventory() {
        return hunter.inventory();
    }

    public void takeArrow() {
        hunter.takeArrow();
    }

    @Override
    public String toString() {
        return hunter.getRoom().toString();
    }

    public boolean isPlaying() {
        return playing && !hunter.isDead();
    }

    public void quit() {
        Logger.debug("user quit");
        playing = false;
    }

    public void saveState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(this);
            Logger.info("Game state saved to " + SAVE_FILE);
        } catch (IOException e) {
            Logger.error("Failed to save game state", e);
        }
    }

    public String getScore() {
        List<Room> rooms = MazeTraverser.collectAllRoomsAsList(hunter.getRoom());
        Long huntersKilled = countDeadOccupants(rooms, Hunter.class);
        Long wumpiKilled = countDeadOccupants(rooms, Wumpus.class);
        return "Score: Hunter " + wumpiKilled + " Wumpus " + huntersKilled;
    }

    private Long countDeadOccupants(List<Room> rooms, Class<? extends Room.Occupant> occupantType) {
        return rooms.stream()
                .map(Room::occupants)
                .flatMap(Collection::stream)
                .filter(occupantType::isInstance)
                .filter(Room.Occupant::isDead)
                .count();
    }

    // TODO STRUCTURAL: Eliminate ThreadLocal state management - creates hidden dependencies, makes testing harder.
    //   Consider: explicit dependency injection - pass Random instance through constructors where needed.
    private static final ThreadLocal<Map<String, Object>> threadLocalBag = ThreadLocal.withInitial(() -> new HashMap<>() {{
        put("randomizer", new Random());
    }});

    static Map<String, Object> getThreadLocalBag() {
        return threadLocalBag.get();
    }

    public static class Options {
        public static final Options DEFAULT = new Options();
        public static final int DEFAULT_BAT_COUNT = 0;
        public static final int DEFAULT_PIT_COUNT = 0;
        public static final int DEFAULT_WUMPUS_COUNT = 1;
        public static final int DEFAULT_ROOM_COUNT = 20;
        public static final int DEFAULT_INITIAL_ARROW_COUNT = 5;
        public static final int DEFAULT_EXIT_COUNT = 3;
        private Integer roomCount = DEFAULT_ROOM_COUNT;
        private Long randomSeed = null;
        private Integer wumpiCount = DEFAULT_WUMPUS_COUNT;
        private Integer pitCount = DEFAULT_PIT_COUNT;
        private Integer batCount = DEFAULT_BAT_COUNT;
        private Integer initialArrowCount = DEFAULT_INITIAL_ARROW_COUNT;
        private Integer maxExitCount = DEFAULT_EXIT_COUNT;

        private final Map<String, Consumer<String>> optionSetters = Map.of(
                "--arrows", value -> initialArrowCount = Integer.valueOf(value),
                "--bats", value -> batCount = Integer.valueOf(value),
                "--pits", value -> pitCount = Integer.valueOf(value),
                "--rooms", value -> roomCount = Integer.valueOf(value),
                "--seed", value -> randomSeed = Long.valueOf(value),
                "--wumpi", value -> wumpiCount = Integer.valueOf(value),
                "--max_exits", value -> maxExitCount = Integer.valueOf(value)
        );

        // todo split the value determination out to an Options Adapter
        // todo add a 'visualize' option that allows you to output the graph of the maze
        protected Options(String... options) {
            if (isHelpRequested(options)) {
                printHelp();
            } else {
                processOptions(options);
            }
            Logger.debug("new " + this);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Options{");
            sb.append("roomCount=").append(roomCount);
            sb.append(", randomSeed=").append(randomSeed);
            sb.append(", wumpiCount=").append(wumpiCount);
            sb.append(", pitCount=").append(pitCount);
            sb.append(", batCount=").append(batCount);
            sb.append(", initialArrowCount=").append(initialArrowCount);
            sb.append(", maxExitCount=").append(maxExitCount);
            sb.append('}');
            return sb.toString();
        }

        private void processOptions(String... options) {
            Map<String, String> optionValues = mapOptionValues(options);
            Arrays.stream(options)
                    .filter(option -> option.startsWith("--"))
                    .forEach(option -> setOptionValue(option, optionValues.get(option)));
        }

        private static Map<String, String> mapOptionValues(String[] options) {
            Map<String, String> optionValues = new HashMap<>();
            for (int idx = 0; idx + 1 < options.length; idx += 2) {
                optionValues.put(options[idx], options[idx + 1]);
            }
            return optionValues;
        }

        private void setOptionValue(String optionName, String optionValue) {
            Consumer<String> setter = optionSetters.get(optionName);
            if (setter != null) {
                setter.accept(optionValue);
            } else {
                Logger.debug("cli: unknown argument " + optionName);
            }
        }

        private void printHelp() {
            Logger.info(
                    "\t--arrows #\t\tLimit the number of arrows\n" +
                            "\t--bats #\t\tLimit the number of colonies of bats\n" +
                            "\t--pits #\t\tLimit the number of bottomless pits\n" +
                            "\t--rooms #\t\tLimit the number of rooms\n" +
                            "\t--seed  #\t\tSet the Randomizer seed\n" +
                            "\t--wumpi  #\t\tLimit the number of wumpi\n" +
                            "\t--exits  #\t\tLimit the number of room exits\n"
            );
        }

        private boolean isHelpRequested(String... options) {
            return Arrays.asList(options).contains("--help");
        }

        public Integer getRoomCount() {
            return roomCount;
        }

        public boolean hasRandomSeed() {
            return randomSeed != null;
        }

        public Long getRandomSeed() {
            return randomSeed;
        }

        public Integer getWumpiCount() {
            return wumpiCount;
        }

        public Integer getPitCount() {
            return pitCount;
        }

        public Integer getBatCount() {
            return batCount;
        }

        public Integer getInitialArrowCount() {
            return initialArrowCount;
        }

        public Integer getMaxExitCount() {
            return maxExitCount;
        }
    }
}
