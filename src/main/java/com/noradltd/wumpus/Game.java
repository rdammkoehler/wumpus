package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Collectors;

class Game {
    private final Hunter hunter;
    private boolean playing = true;

    Game(String[] options) {
        // TODO we construct options 3 times, once for builder, once for loader, and once for hunter; do this only once
        //  Maze.Options should probably be Game.Options
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);
        hunter = new Hunter(new ArrowQuiver(new Maze.Options(options).getInitialArrowCount()));
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

}

