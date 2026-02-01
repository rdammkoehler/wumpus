package com.noradltd.wumpus;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO [Interface Segregation] OccupantManager exposes both read (getOccupants, getOccupantsSnapshot)
//   and write (addOccupant, removeOccupant) operations. Consider separating into
//   OccupantReader and OccupantWriter interfaces if clients only need subset of operations.
class OccupantManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Room.Occupant> occupants = new ArrayList<>();

    List<Room.Occupant> getOccupantsSnapshot() {
        return new ArrayList<>(occupants);
    }

    Set<Room.Occupant> getOccupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
    }

    boolean isEmpty() {
        return occupants.isEmpty();
    }

    void addOccupant(Room.Occupant occupant) {
        occupants.add(occupant);
    }

    void removeOccupant(Room.Occupant occupant) {
        occupants.remove(occupant);
    }

    boolean containsSameTypeOfOccupant(Room.Occupant occupant) {
        return occupants.stream().anyMatch(occ -> occupant.getClass().isInstance(occ));
    }
}
