package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Collectors;

class Game {
    static class RoomDescriber extends Room {
        private final Room room;

        RoomDescriber(Room room) {
            this.room = room;
        }


        public String description() {
            StringBuilder sb = new StringBuilder();
            sb.append("You are in room #").append(room.number()).append("\n");
            describeExits(sb);
            describeOccupants(sb);
            describeNeighbors(sb);
            return sb.toString();
        }

        private void describeNeighbors(StringBuilder sb) {
            sb.append("\n");
            room.exits().stream()
                    .flatMap(exit -> exit.occupants().stream())
                    .filter(occupant -> !Arrow.class.isInstance(occupant))
                    .map(Room.Occupant::describe)
                    .distinct()
                    .forEach(description -> sb.append(description).append("\n"));
        }

        private void describeOccupants(StringBuilder sb) {
            final Collection<Occupant> describableOccupants = room.occupants().stream()
                    .filter(occupant -> !Hunter.class.isInstance(occupant))
                    .filter(occupant -> !occupant.isDead())
                    .sorted()
                    .collect(Collectors.toList());
            if (describableOccupants.size() > 1) {
                sb.append("\nContains ");
                boolean and = false;
                for (Occupant occupant : describableOccupants) {
                    if (and) {
                        sb.append(" and ");
                    }
                    sb.append(describe(occupant));
                    and = true;
                }
            }
        }

        private String describe(Occupant occupant) {
            StringBuilder sb = new StringBuilder();
            String occupantName = occupant.getClass().getSimpleName();
            sb.append("a "); // TODO plural
            sb.append(occupantName);
            return sb.toString();
        }

        private void describeExits(StringBuilder sb) {
            sb.append("This room has ").append(room.exits().size()).append(" exits.");
        }
    }

    private final Hunter hunter;
    private final Maze maze;
    private boolean playing = true;

    Game(String[] options) {
        maze = MazeLoader.populate(MazeBuilder.build(options), options);
        hunter = new Hunter(new ArrowQuiver(5));  // default to 5 arrows for the moment
        hunter().moveTo(maze.entrance());
    }

    public Maze maze() {
        return maze;
    }

    public Hunter hunter() {
        return hunter;
    }

    public String describe() {
        return new Game.RoomDescriber(hunter.getRoom()).description();
    }

    public boolean isPlaying() {
        return playing && !hunter.isDead();
    }

    public void quit() {
        playing = false;
    }

    void diagnostics() {
        System.out.println("Current Room " + hunter.getRoom().number());
        System.out.println("Occupants: " + diagnoticFor(hunter.getRoom()));
        StringBuilder sb = new StringBuilder();
        sb.append("Exits:");
        int idx = 1;
        for (Room exit : hunter.getRoom().exits()) {
            sb.append("idx: ").append(idx++).append(" #").append(exit.number()).append("\n");
        }
        sb.append("\n");
        for (Room exit : hunter.getRoom().exits()) {
            sb.append("Will be described as:\n");
            sb.append(new RoomDescriber(exit).description());
            sb.append("\t");
            sb.append(diagnoticFor(exit));
            sb.append("\t\n*****\n");
        }
        sb.append("----------------------------------------");
        sb.append("Room List\n");
        allRooms(maze).forEach(room -> sb.append("[#")
                .append(room.number())
                .append(" {")
                .append(room.occupants().stream().map(occupant -> occupant.getClass().getSimpleName()).collect(Collectors.joining(", ")))
                .append("}] ")
        );
        System.out.println(sb.toString());

    }

    private static List<Room> allRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
    }

    private static Set<Room> collectRoom(Room room, Set<Room> rooms) {
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


    private String diagnoticFor(Room room) {
        return room.occupants().stream()
                .map(occupant -> occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")")
                .collect(Collectors.joining(", "));
    }

    private static final ThreadLocal<Map<String, Object>> threadLocalBag = ThreadLocal.withInitial(() -> new HashMap<>() {{
        put("randomizer", new Random());
    }});

    static Map<String, Object> getThreadLocalBag() {
        return threadLocalBag.get();
    }
}
