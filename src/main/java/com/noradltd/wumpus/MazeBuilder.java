package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


class MazeBuilder {
    static class Maze {
        private final Random random = new Random(0);
        private Room firstRoom;
        private Set<Room> rooms = new HashSet<Room>();

        Maze() {
            addRooms();
            // TODO make sure the first room is Empty so we can put the Hunter in there
            firstRoom = (Room) rooms.toArray()[random.nextInt(rooms.size())];
        }

        private void addRooms() {
            boolean forceLinking = false;
            while (rooms.size() < 20) {
                Room room = new Room();
                rooms.add(room);
                addExits(room, forceLinking || rooms.size() > 19);
                forceLinking = true;
            }
        }

        private void addExits(Room room, boolean forceLinking) {
            if (rooms.size() <= 20) {
                if (room.exits().size() < 2) {
                    Integer numberOfExits = random.nextInt(2) + 1;
                    for (int count = 0; count < numberOfExits; count++) {
                        while (room.exits().size() < 3) {
                            if (!forceLinking && rooms.size() < 20) {
                                Room exit = new Room();
                                room.add(exit);
                                rooms.add(exit);
                                forceLinking = false;
                            } else {
                                Room exit = room;
                                while (room == exit) {
                                    exit = (Room) rooms.toArray()[random.nextInt(rooms.size())];
                                    if (room != exit) {
                                        room.add(exit);
                                    }
                                }
                            }
                        }
                    }
                    for (Room exit : room.exits()) {
                        if (random.nextBoolean()) {
                            addExits(exit, forceLinking);
                        }
                    }
                }
            }
        }

        public Room firstRoom() {
            return firstRoom;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Room room : rooms) {
                sb.append("Room ").append(room.hashCode()).append(": ");
                sb.append(new Game.RoomDescriber(room).description()).append("\n*****\n");
            }
            return sb.toString();
        }
    }

    static Maze build() {
        return new Maze();
    }
}
