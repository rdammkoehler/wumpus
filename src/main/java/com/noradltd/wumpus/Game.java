package com.noradltd.wumpus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class Game {
    static class RoomDescriber extends Room {
        private final Room room;

        RoomDescriber(Room room) {
            this.room = room;
        }

        private String describe(Occupant occupant) {
            StringBuilder sb = new StringBuilder();
            String occupantName = occupant.getClass().getSimpleName();
            sb.append("a ");
            sb.append(occupantName);
            return sb.toString();
        }

        public String description() {
            StringBuilder sb = new StringBuilder();
            describeExits(sb);
            describeOccupants(sb);
            return sb.toString();
        }

        private void describeOccupants(StringBuilder sb) {
            if (room.occupants().size() > 0) {
                sb.append("\nContains ");
                boolean and = false;
                for (Occupant occupant : room.occupants().stream().sorted().collect(Collectors.toList())) {
                    if (and) {
                        sb.append(" and ");
                    }
                    sb.append(describe(occupant));
                    and = true;
                }
            }
        }

        private void describeExits(StringBuilder sb) {
            sb.append("Has exits ");
            boolean comma = false;
            for (Room exit : room.exits()) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(exit.number());
                comma = true;
            }
        }
    }

    private final Hunter hunter = new Hunter();
    private final Maze maze;

    Game(String[] options) {
        maze = MazeBuilder.build(options);
        hunter().moveTo(maze.entrance());
        System.out.println(this.describe());
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


    private static final ThreadLocal<Map<String,?>> threadLocalBag = ThreadLocal.withInitial(() -> new HashMap<>() {{
        put("randomizer", new Random());
    }});

    static Map<String,?> getThreadLocalBag() {
        return threadLocalBag.get();
    }
}
