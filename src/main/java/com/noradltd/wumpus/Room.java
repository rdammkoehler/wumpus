package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Room {
    interface Occupant extends Comparable<Occupier> {
        void moveTo(Room room);

        void respondTo(Occupant actioned);

        Boolean isDead();
    }

    interface RoomNumberer {
        Integer nextRoomNumber();
    }

    public static final RoomNumberer DEFAULT_ROOM_NUMBERER = new RoomNumberer() {
        private int instanceCounter = 1;

        @Override
        public Integer nextRoomNumber() {
            return instanceCounter++;
        }
    };

    static RoomNumberer roomNumberer = DEFAULT_ROOM_NUMBERER;
    private final int instanceNumber = roomNumberer.nextRoomNumber();
    private final Set<Room> exits = new HashSet<>();
    private List<Occupant> occupants = new ArrayList<>();

    public List<Room> exits() {
        return new ArrayList<>(exits);
    }

    Room add(Occupant occupant) {
        occupants.add(occupant);
        if (occupants.size() > 1) {
            executeOccupantInteractions();
        }
        return this;
    }

    private void executeOccupantInteractions() {
        List<Occupant> occupantsTemp = new ArrayList<>(occupants);
        for (Occupant actor : occupantsTemp) {
            if (!actor.isDead()) {
                for (Occupant cohabitant : occupantsTemp) {
                    if (actor != cohabitant) {
                        actor.respondTo(cohabitant);
                    }
                }
            }
        }
        occupants = occupantsTemp;
    }

    @SuppressWarnings("UnusedReturnValue")
    Room remove(Occupant occupant) {
        occupants.remove(occupant);
        return this;
    }

    public Set<Occupant> occupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
    }

    private static void connectRooms(Room one, Room two) {
        one.exits.add(two);
        two.exits.add(one);
    }

    public Room add(Room exit) {
        connectRooms(this, exit);
        return this;
    }

    public Integer number() {
        return hashCode();
    }

    @Override
    public int hashCode() {
        return instanceNumber;
    }
}
