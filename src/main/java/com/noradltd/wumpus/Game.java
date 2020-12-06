package com.noradltd.wumpus;

import java.util.stream.Collectors;

class Game {
    static class RoomDescriber extends Room {
        private Room room;

        RoomDescriber(Room room) {
            this.room = room;
        }

        private String describe(Occupant occupant) {
            StringBuilder sb = new StringBuilder();
            String occupantName = occupant.getClass().getSimpleName();
            // plural?
            sb.append("a ");
            sb.append(occupantName);
            return sb.toString();
        }

        public String description() {
            StringBuilder sb = new StringBuilder();
            sb.append("Has exits ");
            boolean comma = false;
            for (Room exit : room.exits()) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(exit.number());
                comma = true;
            }
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
            return sb.toString();
        }
    }

    private Hunter hunter = new Hunter();
    private Maze maze;

    Game() {
        maze = MazeBuilder.build(new String[] {});
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
}
