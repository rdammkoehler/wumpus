package com.noradltd.wumpus;

import java.util.*;

class GameStateLoader {

    static class LoadedGame {
        final Maze maze;
        final Hunter hunter;

        LoadedGame(Maze maze, Hunter hunter) {
            this.maze = maze;
            this.hunter = hunter;
        }
    }

    static LoadedGame load(GameState state) {
        Map<Integer, Room> roomMap = reconstructRooms(state.rooms);
        Maze maze = createMaze(roomMap, state.hunter.roomNumber);
        Hunter hunter = reconstructHunter(state.hunter, roomMap);
        reconstructOccupants(state.occupants, roomMap);
        return new LoadedGame(maze, hunter);
    }

    private static Map<Integer, Room> reconstructRooms(List<GameState.RoomState> roomStates) {
        Map<Integer, Room> roomMap = new HashMap<>();

        Room.RoomNumberer originalNumberer = Room.roomNumberer;
        try {
            for (GameState.RoomState rs : roomStates) {
                final int targetNumber = rs.number;
                Room.roomNumberer = () -> targetNumber;
                roomMap.put(rs.number, new Room());
            }
        } finally {
            Room.roomNumberer = originalNumberer;
        }

        for (GameState.RoomState rs : roomStates) {
            Room room = roomMap.get(rs.number);
            for (Integer exitNumber : rs.exits) {
                Room exitRoom = roomMap.get(exitNumber);
                if (exitRoom != null && !room.exits().contains(exitRoom)) {
                    room.add(exitRoom);
                }
            }
        }

        return roomMap;
    }

    private static Maze createMaze(Map<Integer, Room> roomMap, int hunterRoomNumber) {
        Room entrance = roomMap.get(hunterRoomNumber);
        return () -> entrance;
    }

    private static Hunter reconstructHunter(GameState.HunterState hs, Map<Integer, Room> roomMap) {
        ArrowQuiver quiver = new ArrowQuiver(hs.arrowCount);
        Hunter hunter = new ReconstructedHunter(quiver, hs.kills, hs.dead);
        hunter.moveTo(roomMap.get(hs.roomNumber));
        return hunter;
    }

    private static void reconstructOccupants(List<GameState.OccupantState> occupants, Map<Integer, Room> roomMap) {
        for (GameState.OccupantState os : occupants) {
            Room room = roomMap.get(os.roomNumber);
            if (room == null) continue;

            Room.Occupant occupant = createOccupant(os);
            if (occupant != null) {
                occupant.moveTo(room);
                if (os.dead) {
                    occupant.die();
                }
            }
        }
    }

    private static Room.Occupant createOccupant(GameState.OccupantState os) {
        return switch (os.type) {
            case "Wumpus" -> new Wumpus();
            case "Arrow" -> createArrow(os);
            case "BottomlessPit" -> new BottomlessPit();
            case "ColonyOfBats" -> new ColonyOfBats();
            default -> {
                Logger.debug("Unknown occupant type: " + os.type);
                yield null;
            }
        };
    }

    private static Arrow createArrow(GameState.OccupantState os) {
        Arrow arrow = new ReconstructedArrow(
                Boolean.TRUE.equals(os.broken),
                Boolean.TRUE.equals(os.killedWumpus)
        );
        return arrow;
    }

    private static class ReconstructedHunter extends Hunter {
        ReconstructedHunter(Quiver quiver, int kills, boolean dead) {
            super(quiver);
            for (int i = 0; i < kills; i++) {
                this.kill(new DummyWumpus());
            }
            if (dead) {
                this.die();
            }
        }
    }

    private static class DummyWumpus extends Wumpus {
        private Room dummyRoom = new Room();
        @Override
        Room getRoom() {
            return dummyRoom;
        }
    }

    private static class ReconstructedArrow extends Arrow {
        ReconstructedArrow(boolean broken, boolean killedWumpus) {
            if (broken) {
                this.die();
            }
        }
    }
}
