package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.Set;

public class AsciiMapper {

    static String map(Room room) {
        String map = new AsciiMapper(room).toString();
        Logger.info(map);
        return map;
    }

    private Room startingRoom;
    private Set<Room> processedRooms = new HashSet<>();

    AsciiMapper(Room startingRoom) {
        this.startingRoom = startingRoom;
    }

    @Override
    public String toString() {
        return mapRoom(startingRoom);
    }

    String mapRoom(Room room) {
        StringBuilder repr = new StringBuilder();

        repr.append("[");
        repr.append(room.number());
        repr.append("]");
        repr.append("(");
        for (Room.Occupant occupant : room.occupants()) {
            repr.append(occupant.getClass().getSimpleName().substring(0,1));
        }
        repr.append(")");
        processedRooms.add(room);

        int idx = 0;
        for (Room exit : room.exits()) {
            if (!processedRooms.contains(exit)) {
                repr.append("-{");
                repr.append(idx++);
                repr.append("}->");
                repr.append(mapRoom(exit));
            }
        }
        return repr.toString();
    }
}
