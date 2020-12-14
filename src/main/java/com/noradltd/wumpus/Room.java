package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Room {
    static abstract class Occupant implements Comparable<Occupant> {
        private Room room;
        private Boolean dead = Boolean.FALSE;

        public Room getRoom() {
            return room;
        }

        public void moveTo(Room newRoom) {
            if (room != null) {
                Logger.debug("Moving " + this.getClass().getSimpleName() + " from " + room.number() + " to " + newRoom.number());
                room.remove(this);
            } else {
                Logger.debug("Moving " + this.getClass().getSimpleName() + " to " + newRoom.number());
            }
            room = newRoom;
            newRoom.add(this);
        }

        abstract void respondTo(Occupant actioned);

        public Boolean isDead() {
            return dead;
        }

        protected void die() {
            Logger.debug(this.getClass().getSimpleName() + " has died!");
            dead = Boolean.TRUE;
        }

        abstract String describe();

        @Override
        public int compareTo(Occupant other) {
            return this.getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
        }

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
        executeOccupantInteractions(occupant);
        return this;
    }

    private void executeOccupantInteractions(Occupant interloper) {
        if (occupants.size() > 0) {
            Logger.debug(interloper.getClass().getSimpleName() + " is interacting with " + occupants.stream()
                    .map(occupant -> occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")")
                    .collect(Collectors.joining(", ")));
            for (Occupant cohabitant : new ArrayList<>(occupants)) {
                if (!interloper.isDead()) {
                    if (Random.getRandomizer().nextBoolean()) {
                        cohabitant.respondTo(interloper);
                        interloper.respondTo(cohabitant);
                    } else {
                        interloper.respondTo(cohabitant);
                        cohabitant.respondTo(interloper);
                    }
                }
            }
        } else {
            Logger.debug("this room is empty");
        }
        if (!interloper.isDead() && interloper.getRoom().equals(this)) { // bad casting, please fix
            occupants.add(interloper);
        }
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
