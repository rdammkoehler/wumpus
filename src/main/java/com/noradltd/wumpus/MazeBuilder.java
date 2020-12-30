package com.noradltd.wumpus;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


interface Maze {
    Room entrance();
}

class MazeBuilder {
    private static final Integer MIN_ROOM_COUNT = 2;

    static class MazeStruct implements Maze {
        private final Room entrance;

        private MazeStruct(Set<Room> rooms) {
            entrance = getRandomRoom(rooms);
        }

        @Override
        public Room entrance() {
            return entrance;
        }
    }

    private final Set<Room> rooms = new HashSet<>();
    private final Game.Options options;

    private MazeBuilder(Game.Options options) {
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
        Room exit = room;
        while (exit == room) {
            exit = getRandomRoom(rooms);
        }
        room.add(exit);
    }

    private static Room getRandomRoom(Collection<Room> rooms) {
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
        return new MazeStruct(createRooms());
    }

    static Maze build() {
        return new MazeBuilder(Game.Options.DEFAULT).buildMaze();
    }

    static Maze build(Game.Options options) {
        return new MazeBuilder(options).buildMaze();
    }
}

class MazeLoader {
    private static final int MINIMUM_PIT_COUNT = 1;
    private static final int MINIMUM_WUMPUS_COUNT = 1;
    private static final int MINIMUM_BAT_COUNT = 1;
    private final Game.Options options;
    private final List<Room> rooms;
    private final Maze maze;

    MazeLoader(Game.Options options, Maze maze) {
        this.options = options;
        this.maze = maze;
        rooms = collectAllRooms();
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

    private Maze populateMaze() {
        addWumpi();
        addPits();
        addBats();
        return maze;
    }

    private List<Room> collectAllRooms() {
        class RoomCollector {
            private final List<Room> allRooms;

            RoomCollector(Maze maze) {
                allRooms = collectRoom(maze.entrance(), new HashSet<>());
            }

            private List<Room> collectRoom(Room room, Set<Room> rooms) {
                if (!rooms.contains(room)) {
                    rooms.add(room);
                    room.exits().stream().filter(exit -> !rooms.contains(exit)).forEach(exit -> collectRoom(exit, rooms));
                }
                return rooms.stream().collect(Collectors.toUnmodifiableList());
            }

            public List<Room> getAllRooms() {
                return allRooms;
            }
        }
        return new RoomCollector(maze).getAllRooms();
    }


    private void addPits() {
        addOccupants(createOccupantsByType(getPitCount(), BottomlessPit.class));
    }

    private void addWumpi() {
        addOccupants(createOccupantsByType(getWumpiCount(), Wumpus.class));
    }

    private void addBats() {
        addOccupants(createOccupantsByType(getBatCount(), ColonyOfBats.class));
    }

    private void addOccupants(Collection<? extends Room.Occupant> occupants) {
        for (Room.Occupant occupant : occupants) {
            int occupantIdx = Random.getRandomizer().nextInt(rooms.size());
            while (maze.entrance().equals(rooms.get(occupantIdx))
                    ||
                    rooms.get(occupantIdx).occupants().stream().anyMatch(occ -> occupant.getClass().isInstance(occ))) {
                occupantIdx = Random.getRandomizer().nextInt(rooms.size());
            }
            occupant.moveTo(rooms.get(occupantIdx));
        }
    }

    private Collection<Room.Occupant> createOccupantsByType(int requiredCount, Class<? extends Room.Occupant> occupantClass) {
        Collection<Room.Occupant> occupants = new ArrayList<>();
        for (int idx = 0; idx < requiredCount; idx++) {
            try {
                occupants.add(occupantClass.getDeclaredConstructor((Class<?>[]) null).newInstance((Object[]) null));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        return occupants;
    }

    static Maze populate(Maze maze, Game.Options options) {
        return new MazeLoader(options, maze).populateMaze();
    }
}
