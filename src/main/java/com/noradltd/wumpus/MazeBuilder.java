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
        }};
        public static final Options DEFAULT = new Options();
        @SuppressWarnings("FieldCanBeLocal")
        private Integer roomCount = 20;
        private Long randomSeed = null;
        private MazeBuilder.Stringifier displayFormat = MazeBuilder.Stringifier.HUMAN;

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
    }
}

class MazeBuilder {
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
                    sb.append("Room ").append(room.number()).append(": ");
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
        return rooms.size() > options.getRoomCount() - 1;
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

    static Maze build(String[] options) {
        return new MazeBuilder(new Maze.Options(options)).buildMaze();
    }
}

class MazeLoader {
    private final Maze.Options options;
    private List<Room> allRooms;

    MazeLoader(Maze.Options options) {
        this.options = options;
    }

    private Maze populateMaze(Maze maze) {
        allRooms = getAllRooms(maze);
        addWumpi(maze);
        addPits(maze);
        return maze;
    }

    private List<Room> getAllRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>());
    }

    private List<Room> collectRoom(Room room, Set<Room> rooms) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            for (Room exit : room.exits()) {
                if (!rooms.contains(exit)) {
                    collectRoom(exit, rooms);
                }
            }
        }
        return rooms.stream().collect(Collectors.toUnmodifiableList());
    }

    private void addOccupants(Maze maze, List<? extends Room.Occupant> occupants) {
        for (Room.Occupant occupant : occupants) {
            int occupantIdx = Random.getRandomizer().nextInt(allRooms.size());
            while (maze.entrance().equals(allRooms.get(occupantIdx))) {  // TODO don't double up pits
                occupantIdx = Random.getRandomizer().nextInt(allRooms.size());
            }
            occupant.moveTo(allRooms.get(occupantIdx));
        }
    }

    private void addPits(Maze maze) {
        addOccupants(maze, getPits());
    }

    private List<BottomlessPit> getPits() {
        List<BottomlessPit> pits = new ArrayList<>();
        final int pitsRequired = Math.max(1, options.getRoomCount() / 5);
        for (int idx = 0; idx < pitsRequired; idx++) {
            pits.add(new BottomlessPit());
        }
        return pits;
    }

    private void addWumpi(Maze maze) {
        addOccupants(maze, getWumpi());
    }

    private List<Wumpus> getWumpi() {
        List<Wumpus> wumpi = new ArrayList<>();
        final int wumpiRequired = Math.max(1, options.getRoomCount() / 7);
        for (int idx = 0; idx < wumpiRequired; idx++) {
            wumpi.add(new Wumpus());
        }
        return wumpi;
    }

    static Maze populate(Maze maze, String[] options) {
        return new MazeLoader(new Maze.Options(options)).populateMaze(maze);
    }
}