package com.noradltd.wumpus;

import java.util.Comparator;
import java.util.List;

public class RandomRoomFinder {

    public static Room findRandomRoom(Room currentRoom) {
        Logger.debug("looking for a random room from " + currentRoom.number());
        if (currentRoom.exits().isEmpty()) {
            return currentRoom;
        }

        // TODO: Law of Demeter Principle violation.
        // This functionality should be the responsibility of the Room or Maze class.
        List<Room> allRooms = MazeTraverser.collectAllRoomsAsList(currentRoom);

        List<Room> candidates = allRooms.stream()
                .filter(room -> !room.equals(currentRoom))
                .filter(room -> room.occupants().isEmpty())
                .sorted(Comparator.comparingInt(Room::number))
                .toList();

        if (candidates.isEmpty()) {
            Logger.debug("Random Room: " + currentRoom.number() + " (no valid candidates found)");
            return currentRoom;
        }

        Room selectedRoom = candidates.get(Random.getRandomizer().nextInt(candidates.size()));
        Logger.debug("Random Room: " + selectedRoom.number());
        return selectedRoom;
    }
}