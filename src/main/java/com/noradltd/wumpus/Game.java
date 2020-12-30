package com.noradltd.wumpus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

class Game {
    private final Hunter hunter;
    private boolean playing = true;

    Game(String[] options) {
        Game.Options gameOptions = new Game.Options(options);
        Maze maze = MazeLoader.populate(MazeBuilder.build(gameOptions), gameOptions);
        hunter = new Hunter(new ArrowQuiver(gameOptions.getInitialArrowCount()));
        hunter.moveTo(maze.entrance());
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
                    // TODO remove diagnostic
//                    rooms.stream().map(room -> new Room.RoomDescriber(room).description()).forEach(System.err::println);
                }
                return rooms.stream().collect(Collectors.toUnmodifiableList());
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
            put("arrows", "initialArrowCount");
            put("bats", "batCount");
            put("format", "displayFormat");
            put("pits", "pitCount");
            put("rooms", "roomCount");
            put("seed", "randomSeed");
            put("wumpi", "wumpiCount");
        }};
        public static final Options DEFAULT = new Options();
        public static final int DEFAULT_BAT_COUNT = 0;
        public static final int DEFAULT_PIT_COUNT = 0;
        public static final int DEFAULT_WUMPUS_COUNT = 1;
        public static final int DEFAULT_ROOM_COUNT = 20;
        public static final int DEFAULT_INITIAL_ARROW_COUNT = 5;
        @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
        private Integer roomCount = DEFAULT_ROOM_COUNT;
        @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
        private Long randomSeed = null;
        @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
        private Integer wumpiCount = DEFAULT_WUMPUS_COUNT;
        @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
        private Integer pitCount = DEFAULT_PIT_COUNT;
        @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
        private Integer batCount = DEFAULT_BAT_COUNT;
        @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
        private Integer initialArrowCount = DEFAULT_INITIAL_ARROW_COUNT;

        protected Options(String... options) {
            if (isHelpRequested(options)) {
                printHelp();
            } else {
                processOptions(options);
            }
        }

        private void processOptions(String... options) {
            for (int optionIdx = 0; optionIdx < options.length - 1; optionIdx += 2) {
                String optionName = options[optionIdx].substring(2);
                String attrName = optionNameAttrMap.getOrDefault(optionName, null);
                if (attrName != null) {
                    String optionValue = options[optionIdx + 1].toUpperCase();
                    setOptionValue(attrName, optionValue);
                }
            }
        }

        private void setOptionValue(String attrName, String optionValue) {
            try {
                Field field = this.getClass().getDeclaredField(attrName);
                Method valueOf = field.getType().getMethod("valueOf", String.class);
                field.set(this, valueOf.invoke(null, optionValue));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        }

        private void printHelp() {
            Logger.info(
                    "\t--arrows #\t\tLimit the number of arrows\n" +
                    "\t--bats #\t\tLimit the number of colonies of bats\n" +
                    "\t--pits #\t\tLimit the number of bottomless pits\n" +
                    "\t--rooms #\t\tLimit the number of rooms\n" +
                    "\t--seed  #\t\tSet the Randomizer seed\n" +
                    "\t--wumpi  #\t\tLimit the number of wumpi\n"
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
    }
}

