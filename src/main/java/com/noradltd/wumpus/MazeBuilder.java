package com.noradltd.wumpus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface Maze {
    Room entrance();

    @SuppressWarnings("FieldMayBeFinal")
    class Options {
        private static final Map<String, String> optionNameAttrMap = new HashMap<>() {{
            put("rooms", "roomCount");
            put("seed", "randomSeed");
            put("format", "displayFormat");
            put("wumpi", "wumpiCount");
            put("pits", "pitCount");
            put("bats", "batCount");
        }};
        public static final Options DEFAULT = new Options();
        public static final int DEFAULT_BAT_COUNT = 0;
        public static final int DEFAULT_PIT_COUNT = 0;
        public static final int DEFAULT_WUMPUS_COUNT = 1;
        public static final int DEFAULT_ROOM_COUNT = 20;
        @SuppressWarnings("FieldCanBeLocal")
        private Integer roomCount = DEFAULT_ROOM_COUNT;
        private Long randomSeed = null;
        private MazeBuilder.Stringifier displayFormat = MazeBuilder.Stringifier.HUMAN;
        private Integer wumpiCount = DEFAULT_WUMPUS_COUNT;
        private Integer pitCount = DEFAULT_PIT_COUNT;
        private Integer batCount = DEFAULT_BAT_COUNT;

        private Options() {
        }

        protected Options(String[] options) {
            if (isHelpRequested(options)) {
                printHelp();
            } else {
                processOptions(options);
            }
        }

        private void processOptions(String[] options) {
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
            System.out.println("\t--rooms #\t\tLimit the number or rooms\n" +
                    "\t--seed  #\t\tSet the Randomizer seed\n" +
                    "\t--format $\t\tSet the output format (human, dot, neato)");
        }

        private boolean isHelpRequested(String[] options) {
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

        public MazeBuilder.Stringifier getDisplayFormat() {
            return displayFormat;
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
    }
}

class MazeBuilder {
    private static final Integer MIN_ROOM_COUNT = 2;

    static class MazeStruct implements Maze {
        private final Room entrance;
        private final Stringifier stringifier;

        private MazeStruct(Set<Room> rooms, Stringifier stringifier) {
            this.stringifier = stringifier;
            // todo make sure the room is empty!
            entrance = getRandomRoom(rooms);
        }

        public Room entrance() {
            return entrance;
        }

        @Override
        public String toString() {
            return stringifier.stringify(this);
        }
    }

    @SuppressWarnings("unused")
    enum Stringifier {
        HUMAN {
            @Override
            String stringify(Maze maze) {
                Set<Room> rooms = roomsOf(maze.entrance());
                StringBuilder sb = new StringBuilder();
                for (Room room : rooms) {
                    sb.append(new Game.RoomDescriber(room).description()).append("\n*****\n");
                }
                return sb.toString();
            }
        },
        DOT {
            @Override
            String stringify(Maze maze) {
                Set<Room> rooms = roomsOf(maze.entrance());
                StringBuilder sb = new StringBuilder();
                sb.append("digraph G {\n");
                Set<Integer> bookKeeper = new HashSet<>();
                for (Room room : rooms) {
                    for (Room exit : room.exits()) {
                        if (!bookKeeper.contains(room.number())) {
                            sb.append("\t").append(room.number()).append(" -> ").append(exit.hashCode()).append(";\n");
                            bookKeeper.add(exit.hashCode());
                        }
                    }
                }
                sb.append("}\n");
                return sb.toString();
            }
        },
        NEATO {
            @Override
            String stringify(Maze maze) {
                Set<Room> rooms = roomsOf(maze.entrance());
                StringBuilder sb = new StringBuilder();
                sb.append("graph G {\n");
                Set<Integer> bookKeeper = new HashSet<>();
                for (Room room : rooms) {
                    for (Room exit : room.exits()) {
                        if (!bookKeeper.contains(room.number())) {
                            sb.append("\t").append(room.number()).append(" -- ").append(exit.hashCode()).append(";\n");
                            bookKeeper.add(exit.hashCode());
                        }
                    }
                }
                sb.append("}\n");
                return sb.toString();
            }
        };

        private static Set<Room> roomsOf(Room room) {
            class MazeRunner {
                Set<Room> findAll(Room room, Set<Room> rooms) {
                    rooms.add(room);
                    for (Room exit : room.exits()) {
                        if (!rooms.contains(exit)) {
                            rooms.addAll(findAll(exit, rooms));
                        }
                    }
                    return rooms;
                }
            }
            return new MazeRunner().findAll(room, new HashSet<>());
        }

        abstract String stringify(Maze maze);
    }

    private final Set<Room> rooms = new HashSet<>();
    private final Maze.Options options;

    private MazeBuilder(Maze.Options options) {
        this.options = options;
        if (options.hasRandomSeed()) {
            Random.getRandomizer().setSeed(options.getRandomSeed());
        }
    }

    private Set<Room> createRooms() {
        boolean forceLinking = false;
        while (needsMoreRooms()) {
            addRoom(forceLinking);
            forceLinking = needsMoreRooms();
        }
        return rooms;
    }

    private void addRoom(boolean forceLinking) {
        Room room = new Room();
        rooms.add(room);
        addExits(room, forceLinking || hasEnoughRooms());
    }

    private void addExits(Room room, boolean forceLinking) {
        if (needsMoreExits(room)) {
            randomLengthIntegerStream(3)
                    .forEach(integer -> addExit(room, forceLinking));
            room.exits().stream()
                    .filter(exit -> Random.getRandomizer().nextBoolean())
                    .forEach(exit -> addExits(exit, hasEnoughRooms()));
        }
    }

    private Stream<Integer> randomLengthIntegerStream(Integer upperBound) {
        int number = Random.getRandomizer().nextInt(upperBound - 1) + 1;
        return Arrays.stream(new Integer[number]);
    }

    private void addExit(Room room, boolean forceLinking) {
        if (!forceLinking && needsMoreRooms()) {
            addExit(room);
        } else {
            linkExit(room);
        }
    }

    private void addExit(Room room) {
        rooms.add(new Room().add(room));
    }

    private void linkExit(Room room) {
        Room exit;
        //noinspection StatementWithEmptyBody
        for (exit = room; exit == room; exit = getRandomRoom(rooms)) ;
        room.add(exit);
    }

    public static Room getRandomRoom(Collection<Room> rooms) {
        return rooms.toArray(new Room[]{})[Random.getRandomizer().nextInt(rooms.size())];
    }

    private boolean needsMoreRooms() {
        return !hasEnoughRooms();
    }

    private boolean hasEnoughRooms() {
        return rooms.size() > getRoomCount() - 1;
    }

    private Integer getRoomCount() {
        return Math.max(MIN_ROOM_COUNT, options.getRoomCount());
    }

    private boolean needsMoreExits(Room room) {
        return room.exits().size() < 2;
    }

    private Maze buildMaze() {
        return new MazeStruct(createRooms(), options.getDisplayFormat());
    }

    static Maze build() {
        return new MazeBuilder(Maze.Options.DEFAULT).buildMaze();
    }

    static Maze build(String... options) {
        return new MazeBuilder(new Maze.Options(options)).buildMaze();
    }
}

class MazeLoader {
    public static final int MINIMUM_PIT_COUNT = 1;
    public static final int MINIMUM_WUMPUS_COUNT = 1;
    public static final int MINIMUM_BAT_COUNT = 1;
    private final Maze.Options options;
    private Collection<Room> allRooms;

    MazeLoader(Maze.Options options) {
        this.options = options;
    }

    private int getPitCount() {
        return Math.max(Math.max(MINIMUM_PIT_COUNT, options.getRoomCount() / 5), options.getPitCount());
    }

    private int getWumpiCount() {
        return Math.max(Math.max(MINIMUM_WUMPUS_COUNT, options.getRoomCount() / 7), options.getWumpiCount());
    }

    private int getBatCount() {
        return Math.max(Math.max(MINIMUM_BAT_COUNT, options.getRoomCount() / 5), options.getBatCount());
    }

    private Maze populateMaze(Maze maze) {
        allRooms = collectAllRooms(maze);
        addWumpi(maze);
        addPits(maze);
        addBats(maze);
        return maze;
    }

    private Collection<Room> collectAllRooms(Maze maze) {
        class RoomCollector {
            private final Collection<Room> allRooms;

            RoomCollector(Maze maze) {
                allRooms = collectRoom(maze.entrance(), new HashSet<>());
            }

            private Collection<Room> collectRoom(Room room, Set<Room> rooms) {
                if (!rooms.contains(room)) {
                    rooms.add(room);
                    room.exits().stream().filter(exit -> !rooms.contains(exit)).forEach(exit -> collectRoom(exit, rooms));
                }
                return rooms.stream().collect(Collectors.toUnmodifiableList());
            }

            public Collection<Room> getAllRooms() {
                return allRooms;
            }
        }
        return new RoomCollector(maze).getAllRooms();
    }


    private void addPits(Maze maze) {
        addOccupants(maze, createOccupantsByType(getPitCount(), BottomlessPit.class));
    }

    private void addWumpi(Maze maze) {
        addOccupants(maze, createOccupantsByType(getWumpiCount(), Wumpus.class));
    }

    private void addBats(Maze maze) {
        addOccupants(maze, createOccupantsByType(getBatCount(), ColonyOfBats.class));
    }

    private void addOccupants(Maze maze, Collection<? extends Room.Occupant> occupants) {
        List<Room> rooms = Arrays.asList(allRooms.toArray(new Room[]{}));
        for (Room.Occupant occupant : occupants) {
            int occupantIdx = Random.getRandomizer().nextInt(rooms.size());
            while (maze.entrance().equals(rooms.get(occupantIdx))
                    ||
                    rooms.get(occupantIdx).occupants().stream()
                            .filter(occ -> occupant.getClass().isInstance(occ))
                            .count() > 0) {
                occupantIdx = Random.getRandomizer().nextInt(allRooms.size());
            }
            occupant.moveTo(rooms.get(occupantIdx));
        }
    }

    private Collection<Room.Occupant> createOccupantsByType(int requiredCount, Class<? extends Room.Occupant> occupantClass) {
        Collection<Room.Occupant> occupants = new ArrayList<>();
        for (int idx = 0; idx < requiredCount; idx++) {
            try {
                occupants.add(occupantClass.getDeclaredConstructor(null).newInstance(null));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        return occupants;
    }

    static Maze populate(Maze maze, String[] options) {
        return new MazeLoader(new Maze.Options(options)).populateMaze(maze);
    }
}