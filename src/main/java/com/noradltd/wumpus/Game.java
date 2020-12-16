package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Collectors;

class Game {
    private final Hunter hunter;
    private boolean playing = true;

    Game(String[] options) {
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);
        hunter = new Hunter(new ArrowQuiver(5));  // default to 5 arrows for the moment
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

    // TODO eliminate at some point
    void gotoRoom(Integer arg) {
        hunter.moveTo(findRoom(arg));
    }

    private Room findRoom(Integer roomNumber) {
        return allRooms().stream().filter(room -> room.number() == roomNumber).distinct().collect(Collectors.toList()).get(0);
    }

    private List<Room> allRooms() {
        return collectRoom(hunter.getRoom(), new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
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

    private static final ThreadLocal<Map<String, Object>> threadLocalBag = ThreadLocal.withInitial(() -> new HashMap<>() {{
        put("randomizer", new Random());
    }});

    static Map<String, Object> getThreadLocalBag() {
        return threadLocalBag.get();
    }

    String diagnostics() {
        class GameDiagnostics {
            String occupants(Room mazeRoom) {
                return mazeRoom.occupants().stream()
                        .map(occupant -> occupant.getClass().getSimpleName())
                        .collect(Collectors.joining(", "));
            }

            String diagnostics() {
                Room room = hunter.getRoom();
                StringBuilder sb = new StringBuilder();
                sb.append("Current Room ").append(room.number()).append("\n");
                sb.append("Occupants: ").append(diagnosticFor(room)).append("\n");
                sb.append("Exits:");
                int idx = 1;
                for (Room exit : room.exits()) {
                    sb.append("idx: ").append(idx++).append(" #").append(exit.number()).append("\n");
                }
                sb.append("\n");
                for (Room exit : room.exits()) {
                    sb.append("Will be described as:\n");
                    sb.append(new Room.RoomDescriber(exit).description());
                    sb.append("\t");
                    sb.append(diagnosticFor(exit));
                    sb.append("\t\n*****\n");
                }
                sb.append("----------------------------------------");
                sb.append("Room List\n");
                allRooms().forEach(mazeRoom -> sb.append("[#").append(mazeRoom.number()).append(" {").append(occupants(mazeRoom)).append("}] "));
                return sb.toString();
            }

            private String diagnosticFor(Room room) {
                return room.occupants().stream()
                        .map(occupant -> occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")")
                        .collect(Collectors.joining(", "));
            }
        }
        return new GameDiagnostics().diagnostics();
    }


}

