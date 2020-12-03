package com.noradltd.wumpus;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


class MazeBuilder {
    static class Maze {
        static class Options {

            private static final HashMap<String, String> ARGUMENT_TO_FIELD_NAME_MAPPING = new HashMap<>() {{
                put("rooms", "roomCount");
                put("reporting", "reportingType");
                put("wumpi", "wumpusCount");
                put("bats", "batCount");
                put("pits", "pitCount");
            }};

            enum ReportType {
                HUMAN {
                    @Override
                    public String stringify(Maze maze) {
                        StringBuilder sb = new StringBuilder();
                        for (Room room : maze.rooms) {
                            sb.append("Room ").append(room.hashCode()).append(": ");
                            sb.append(new Game.RoomDescriber(room).description()).append("\n*****\n");
                        }
                        return sb.toString();
                    }
                },
                DOT {
                    @Override
                    public String stringify(Maze maze) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("digraph G {\n");
                        for (Room room : maze.rooms) {
                            sb.append("\t").append(room.hashCode());
                            for (Room exit : room.exits()) {
                                sb.append(" -> ").append(exit.hashCode());
                            }
                            sb.append(";\n");
                        }
                        sb.append("}");
                        return sb.toString();
                    }
                },
                NEATO {
                    @Override
                    public String stringify(Maze maze) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("graph G {\n");
                        Set<Room> bookkeeper = new HashSet<Room>();
                        for (Room room : maze.rooms) {
                            for (Room exit : room.exits()) {
                                if (!(bookkeeper.contains(exit))) {
                                    sb.append("\t").append(room.hashCode()).append(" -- ").append(exit.hashCode()).append(";\n");
                                    bookkeeper.add(exit);
                                }
                            }
                            bookkeeper.add(room);
                        }
                        sb.append("}");
                        return sb.toString();
                    }
                };

                public abstract String stringify(Maze maze);
            }

            enum Arguments {
                rooms(Integer.class), reporting(Maze.Options.ReportType.class), wumpi(Integer.class), bats(Integer.class), pits(Integer.class);
                private Class valueParser;

                Arguments(Class valueParser) {
                    this.valueParser = valueParser;
                }

                Object parse(String value) {
                    try {
                        return valueParser.getMethod("valueOf", String.class).invoke(null, value);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            private static final Options DEFAULTS = new Options();
            private Integer roomCount = 20;
            private ReportType reportingType = ReportType.HUMAN;
            private Integer wumpusCount = 3;
            private Integer batCount = 3;
            private Integer pitCount = 3;

            private static String fieldForArgument(String argument) {
                return ARGUMENT_TO_FIELD_NAME_MAPPING.getOrDefault(argument, null);
            }

            private Options() {
            }

            Options(String[] argv) {
                for (int argIdx = 0; argIdx < argv.length; argIdx += 2) {
                    String argName = argv[argIdx].substring(2);
                    int argValueIdx = argIdx + 1;
                    if (argValueIdx < argv.length) {
                        String argValue = argv[argValueIdx];
                        String fieldName = fieldForArgument(argName);
                        if (fieldName != null) {
                            try {
                                Object value = Arguments.valueOf(argName).parse(argValue);
                                this.getClass().getDeclaredField(fieldName).set(this, value);
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }

            public Integer getRoomCount() {
                return roomCount;
            }

            public ReportType getReportingType() {
                return reportingType;
            }

            public Integer getWumpusCount() {
                return wumpusCount;
            }

            public Integer getBatCount() {
                return batCount;
            }

            public Integer getPitCount() {
                return pitCount;
            }
        }

        private final Options options;
        private final Random random = new Random(0);
        private final Room firstRoom;
        private final Set<Room> rooms = new HashSet<Room>();

        Maze() {
            this(Options.DEFAULTS);
        }

        Maze(Options options) {
            this.options = options;
            addRooms();
            makeHazzardous();
            firstRoom = selectEmptyRoom();
            firstRoom.add(new Hunter());
        }

        private Room selectEmptyRoom() {
            Room room;
            do {
                room = randomRoom();
            } while (room.occupants().size() != 0);
            return room;
        }

        private Room randomRoom() {
            return rooms.toArray(new Room[] {})[random.nextInt(rooms.size())];
        }

        private void makeHazzardous() {
            for(int wumpiCount = 0; wumpiCount < options.getWumpusCount(); wumpiCount++) {
                selectEmptyRoom().add(new Wumpus());
            }
            for(int batsCount = 0; batsCount < options.getBatCount(); batsCount++) {
                selectEmptyRoom().add(new Bats());
            }
            for(int pitCount = 0; pitCount < options.getPitCount(); pitCount++) {
                selectEmptyRoom().add(new Pit());
            }
        }

        private void addRooms() {
            boolean forceLinking = false;
            while (needMoreRooms()) {
                Room room = new Room();
                rooms.add(room);
                addExits(room, forceLinking || !needMoreRooms());
                forceLinking = true;
            }
        }

        private boolean needMoreRooms() {
            return rooms.size() < options.getRoomCount();
        }

        private void addExits(Room room, boolean forceLinking) {
            if (mazeIsIncomplete() && needsExits(room)) {
                forceLinking = addExitsToRoom(room, forceLinking);
                addExitsToExitsOf(room, forceLinking);
            }
        }

        private void addExitsToExitsOf(Room room, boolean forceLinking) {
            for (Room exit : room.exits()) {
                if (wantsMoreExists()) {
                    addExits(exit, forceLinking);
                }
            }
        }

        private boolean needsExits(Room room) {
            return room.exits().size() < 2;
        }

        private boolean wantsMoreExists() {
            return random.nextBoolean();
        }

        private boolean mazeIsIncomplete() {
            return rooms.size() <= options.getRoomCount();
        }

        private boolean addExitsToRoom(Room room, boolean forceLinking) {
            int numberOfExitsToAdd = random.nextInt(2) + 1;
            while (room.exits().size() < numberOfExitsToAdd) {
                if (!forceLinking && needMoreRooms()) {
                    addAnExitRoom(room);
                    forceLinking = false;
                } else {
                    attachAnExit(room);
                }
            }
            return forceLinking;
        }

        private void addAnExitRoom(Room room) {
            Room exit = new Room();
            room.add(exit);
            rooms.add(exit);
        }

        private void attachAnExit(Room room) {
            Room exit = room;
            while (room == exit) {
                exit = (Room) rooms.toArray()[random.nextInt(rooms.size())];
                if (room != exit) {
                    room.add(exit);
                }
            }
        }

        public Room firstRoom() {
            return firstRoom;
        }

        @Override
        public String toString() {
            return options.getReportingType().stringify(this);
        }
    }

    static Maze build() {
        return build(new String[]{});
    }

    static Maze build(String[] options) {
        return new Maze(new Maze.Options(options));
    }
}
