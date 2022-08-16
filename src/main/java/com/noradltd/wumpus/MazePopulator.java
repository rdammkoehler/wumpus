package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class MazePopulator {
    Random random = new Random();
    private Room maze;

    MazePopulator populate(Room mazeEntrance) {
        maze = mazeEntrance;
        return this;
    }

    interface OccupantBuilder<T extends Occupant> {
        T build();
    }

    private MazePopulator withOccupant(int occupantCount, OccupantBuilder<?> occupantBuilder) {
        if (occupantCount < 0) {
            throw new IllegalArgumentException("Zero or more ??? per maze, please");
        }
        List<Room> rooms = collectRooms();
        if (occupantCount > rooms.size()) {
            throw new IllegalArgumentException("Only one ??? per room, please");
        }
        for (int count = 0; count < occupantCount; count++) {
            Occupant occupant = occupantBuilder.build();
            // TODO this is really really really and ugly way to check
            while (true) {
                int randomRoomIdx = random.nextInt(rooms.size());
                Room targetRoom = rooms.get(randomRoomIdx);
                if (targetRoom.getOccupants().stream().noneMatch((o) -> occupant.getClass().isInstance(o))) {
                    targetRoom.addOccupant(occupant);
                    break;
                }
            }
        }
        return this;
    }

    MazePopulator withWumpi(int wumpiCount) {
        return withOccupant(wumpiCount, Wumpus::new);
    }

    MazePopulator withBats(int batCount) {
        return withOccupant(batCount, ColonyOfBats::new);
    }

    MazePopulator withBottomlessPits(int pitCount) {
        return withOccupant(pitCount, BottomlessPit::new);
    }

    private List<Room> collectRooms() {
        return collectRoom(maze, new HashSet<>()).stream().toList();
    }

    private Set<Room> collectRoom(Room room, Set<Room> rooms) {
        if (rooms.add(room)) {
//                room.getAdjacentRooms().stream().toList().stream().map(adjacentRoom->collectRoom(adjacentRoom, rooms));
            for (Room adjacentRoom : room.getAdjacentRooms()) {
                collectRoom(adjacentRoom, rooms);
            }
        }
        return rooms;
    }
}
