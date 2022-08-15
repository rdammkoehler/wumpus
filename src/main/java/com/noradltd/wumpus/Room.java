package com.noradltd.wumpus;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Room implements Comparable<Room> {
    private final Set<Room> adjacentRooms = new TreeSet<>();
    private final Set<Occupant> occupants = new TreeSet<>();
    private final String name;

    Room(String uniqueName) {
        name = uniqueName;
    }

    void attachRoom(Room otherRoom) {
//        System.out.println("attaching " + this.name + " with " + otherRoom.name);
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
        addExitsDescription(builder);
        addOccupantsDescription(builder);
        return builder.toString();
    }

    private void addOccupantsDescription(StringBuilder builder) {
        builder.append("And ");
        final int occupantCount = occupants.size();
        builder.append(occupantCount);
        if (occupantCount == 1) {
            builder.append(" occupant\n");
        } else {
            builder.append(" occupants\n");
        }
        if (occupantCount > 0) {
            builder.append("\t");
            builder.append(getOccupants().stream().toList().get(0).getDescription());
            builder.append("\n");
        }
    }

    private void addExitsDescription(StringBuilder builder) {
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
    }

    @Override
    public int compareTo(Room room) {
        return this.name.compareTo(room.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Room{");
        sb.append("name='").append(name).append('\'');
        sb.append(" adjacent={ ");
        for(Room exit: getAdjacentRooms()){
            sb.append(exit.getName()).append(", ");
        }
        sb.append('}');
        sb.append('}');
        return sb.toString();
    }
}
