package com.noradltd.wumpus;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Room implements Comparable {
    private Set<Room> adjacentRooms = new TreeSet<>();
    private Set<Occupant> occupants = new TreeSet<>();
    private final String name;

    Room(String uniqueName) {
        name = uniqueName;
    }

    void attachRoom(Room otherRoom) {
        adjacentRooms.add(otherRoom);
        otherRoom.adjacentRooms.add(this);
    }

    Set<Room> getAdjacentRooms() {
        return Collections.unmodifiableSet(adjacentRooms);
    }

    void addOccupant(Occupant occupant) {
        occupants.add(occupant);
    }

    Set<Occupant> getOccupants() {
        return Collections.unmodifiableSet(occupants);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append("\nHas ");
        final int exitCount = getAdjacentRooms().size();
        builder.append(exitCount);
        if (exitCount == 1) {
            builder.append(" exit\n");
        } else {
            builder.append(" exits\n");
        }
        if (getAdjacentRooms().size() > 0) {
            for (int index = 0; index < getAdjacentRooms().size(); index++) {
                builder.append("\t");
                int humanIndex = index + 1;
                builder.append(humanIndex);
                builder.append("\t");
                builder.append(getAdjacentRooms().stream().toList().get(index).getName());
                builder.append("\n");
            }
        }
        builder.append("And ");
        builder.append(occupants.size());
        builder.append(" occupant\n");
        if (occupants.size() > 0) {
            builder.append("\t");
            builder.append(getOccupants().stream().toList().get(0).getDescription());
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Room)
            return this.name.compareTo(((Room) o).name);
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
