package com.noradltd.wumpus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Room {
    interface Occupant extends Comparable<Occupier> {
        void moveTo(Room room);

        void respondTo(Occupant actioned);
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
    private Set<Room> exits = new HashSet<Room>();
    private Set<Occupant> occupants = new HashSet<Occupant>();

    public List<Room> exits() {
        return exits.stream().collect(Collectors.toList());
    }

    Room add(Occupant occupant) {
        occupants.add(occupant);
        if (occupants.size() > 1) {
            Set<Occupant> occupantsTemp = new HashSet<Occupant>(occupants);
            for (Occupant actor : occupantsTemp) {
                for (Occupant actioned : occupantsTemp) {
                    if (actor != actioned) {
                        actor.respondTo(actioned);
                    }
                }
            }
            occupants = occupantsTemp;
        }
        return this;
    }

    Room remove(Occupant occupant) {
        occupants.remove(occupant);
        return this;
    }

    public Set<Occupant> occupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
    }

    private static void connectRooms(Room one, Room two) {
        if (!one.exits.contains(two)) one.exits.add(two);
        if (!two.exits.contains(one)) two.exits.add(one);
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
