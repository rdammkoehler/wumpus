package com.noradltd.wumpus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class Game {
    private final Hunter hunter;
    private boolean playing = true;

    Game(String[] options) {
        Game.Options gameOptions = new Game.Options(options);
        Logger.debug(gameOptions + "\n******");
        Maze maze = MazeLoader.populate(MazeBuilder.build(gameOptions), gameOptions);
        hunter = new Hunter(new ArrowQuiver(gameOptions.getInitialArrowCount()));
        Logger.debug("Placing Hunter in room " + maze.entrance().number());
        hunter.moveTo(maze.entrance());
        Logger.info(toString());
    }

    public void move(Integer exitIndex) {
        hunter.moveTo(exitIndex);
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

    public String getScore() {
        class MazeOccupantCounter {
            private Set<Room> rooms = null;

            Long count(Class<? extends Room.Occupant> occupantType) {
                return getRooms().stream()
                        .map(Room::occupants)
                        .flatMap(Collection::stream)
                        .filter(occupantType::isInstance)
                        .filter(Room.Occupant::isDead)
                        .count();
            }

            private List<Room> getRooms() {
                if (rooms == null) {
                    rooms = collectRoom(hunter.getRoom(), new HashSet<>());
                }
                return rooms.stream().toList();
            }

            private Set<Room> collectRoom(Room room, Set<Room> rooms) {
                if (!rooms.contains(room)) {
                    rooms.add(room);
                    room.exits().forEach(exit -> collectRoom(exit, rooms));
                }
                return rooms;
            }

        }
        MazeOccupantCounter counter = new MazeOccupantCounter();
        Long huntersKilled = counter.count(Hunter.class);
        Long wumpiKilled = counter.count(Wumpus.class);
        return "Score: Hunter " + wumpiKilled + " Wumpus " + huntersKilled;
    }

    private static final ThreadLocal<Map<String, Object>> threadLocalBag = ThreadLocal.withInitial(() -> new HashMap<>() {{
        put("randomizer", new Random());
    }});

    static Map<String, Object> getThreadLocalBag() {
        return threadLocalBag.get();
    }

    public static class Options {
        private static final Map<String, String> optionNameAttrMap = new HashMap<>() {{
            put("--arrows", "initialArrowCount");
            put("--bats", "batCount");
            put("--format", "displayFormat");
            put("--pits", "pitCount");
            put("--rooms", "roomCount");
            put("--seed", "randomSeed");
            put("--wumpi", "wumpiCount");
            put("--max_exits", "maxExitCount");
        }};
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


        // todo split the value determination out to an Options Adapter
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
                    .forEach(option -> setOptionValue(optionNameAttrMap.get(option), optionValues.get(option)));
        }

        private static Map<String, String> mapOptionValues(String[] options) {
            Map<String, String> optionValues = new HashMap<>();
            for (int idx = 0; idx + 1 < options.length; idx += 2) {
                optionValues.put(options[idx], options[idx + 1]);
            }
            return optionValues;
        }

        private void setOptionValue(String attrName, String optionValue) {
            try {
                Field field = getClass().getDeclaredField(attrName);
                Method valueOf = field.getType().getMethod("valueOf", String.class);
                field.set(this, valueOf.invoke(null, optionValue));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                     NoSuchFieldException | NullPointerException ex) {
                Logger.debug("cli: unknown argument " + attrName + " " + ex.getMessage(), ex);
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
