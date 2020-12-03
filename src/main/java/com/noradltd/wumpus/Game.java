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

    private final Room firstRoom;
    private Hunter hunter = new Hunter();

    Game() {
        firstRoom = new Room();
        firstRoom.add(new Room());
        hunter.moveTo(firstRoom);
        System.out.println(this.describe());
    }
    public Hunter hunter() { return hunter; }

    public Room firstRoom() {
        return firstRoom;
    }

    public String describe() {
        return new Game.RoomDescriber(firstRoom).description();
    }
}
