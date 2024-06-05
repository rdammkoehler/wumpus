package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Collectors;

class Room {
    static abstract class Occupant implements Comparable<Occupant> {
        private Room room;
        private Boolean dead = Boolean.FALSE;

        Room getRoom() {
            return room;
        }

        void moveTo(Room newRoom) {
            if (room != null) {
                Logger.debug("Moving " + getClass().getSimpleName() + " from " + room.number() + " to " + newRoom.number());
                room.remove(this);
            } else {
                Logger.debug("Moving " + getClass().getSimpleName() + " to " + newRoom.number());
            }
            room = newRoom;
            newRoom.add(this);
        }

        abstract void respondTo(Occupant actioned);

        Boolean isDead() {
            return dead;
        }

        void die() {
            Logger.debug(getClass().getSimpleName() + " has died!");
            dead = Boolean.TRUE;
        }

        abstract String describe();

        @Override
        public int compareTo(Occupant other) {
            return getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
        }

    }

    interface RoomNumberer {
        Integer nextRoomNumber();
    }

    private static final RoomNumberer DEFAULT_ROOM_NUMBERER = new RoomNumberer() {
        private int instanceCounter = 1;

        @Override
        public Integer nextRoomNumber() {
            return instanceCounter++;
        }
    };

    // TODO how do we get this to be NOT package protected?
    static RoomNumberer roomNumberer = DEFAULT_ROOM_NUMBERER;
    private final int instanceNumber = roomNumberer.nextRoomNumber();
    private final Set<Room> exits = new HashSet<>();
    private final List<Occupant> occupants = new ArrayList<>();

    Room() {
        Logger.debug("New Room " + this.instanceNumber);
    }

    List<Room> exits() {
        return new ArrayList<>(exits);
    }

    void add(Occupant occupant) {
        executeOccupantInteractions(occupant);
    }

    private void executeOccupantInteractions(Occupant interloper) {
        class Interactor {
            final Occupant instigator;

            Interactor(Occupant instigator) {
                this.instigator = instigator;
            }

            void interact(Occupant victim) {
                victim.respondTo(instigator);
                if (victim.getRoom().number().equals(instigator.getRoom().number())) {
                    instigator.respondTo(victim);
                }
            }
        }
        if (!occupants.isEmpty()) {
            if (!interloper.isDead()) {
                Logger.debug(interloper.getClass().getSimpleName() + " is interacting with " + occupants.stream()
                        .map(occupant -> occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")")
                        .collect(Collectors.joining(", ")));
                ArrayList<Occupant> copyOfOccupants = new ArrayList<>(occupants);
                for (Occupant cohabitant : copyOfOccupants) {
                    if (Random.getRandomizer().nextBoolean()) {
                        new Interactor(interloper).interact(cohabitant);
                    } else {
                        new Interactor(cohabitant).interact(interloper);
                    }
                }
            }
        } else {
            Logger.debug("this room is empty");
        }
        if (!interloper.isDead() && interloper.getRoom().equals(this)) {
            occupants.add(interloper);
        }
    }

    void remove(Occupant occupant) {
        Logger.debug("removing " + occupant.getClass().getSimpleName() + " from " + number());
        occupants.remove(occupant);
    }

    Set<Occupant> occupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
    }

    boolean containsSameTypeOfOccupant(Occupant occupant) {
        return occupants().stream().anyMatch(occ -> occupant.getClass().isInstance(occ));
    }

    private static void connectRooms(Room one, Room two) {
        one.exits.add(two);
        two.exits.add(one);
    }

    Room add(Room exit) {
        connectRooms(this, exit);
        return this;
    }

    Integer number() {
        return hashCode();
    }

    @Override
    public int hashCode() {
        return instanceNumber;
    }

    @Override
    public String toString() {
        return new RoomDescriber(this).description();
    }

    /*
     I think it is weird to make this a record class b/c its really verby
     So is this sort of antipattern thing in Java now?
     Or is this actually a super sexy way to make a decorator?
    */
    private record RoomDescriber(Room room) {

        private String description() {
            StringBuilder sb = new StringBuilder();
            sb.append("You are in room #").append(room.number()).append("\n");
            describeExits(sb);
            describeOccupants(sb);
            describeNeighbors(sb);
            return sb.toString();
        }

        private void describeNeighbors(StringBuilder sb) {
            sb.append("\n");
            room.exits().stream()
                    .flatMap(exit -> exit.occupants().stream())
                    .filter(occupant -> !(occupant instanceof Arrow))
                    .map(Occupant::describe)
                    .distinct()
                    .sorted()
                    .forEach(description -> sb.append(description).append("\n"));
        }

        private void describeOccupants(StringBuilder sb) {
            Collection<Occupant> describableOccupants = room.occupants().stream()
                    .filter(occupant -> !(occupant instanceof Hunter && !occupant.isDead()))
                    .sorted()
                    .toList();
            if (!describableOccupants.isEmpty()) {
                sb.append("\nContains ")
                        .append(describableOccupants.stream()
                                .map(this::describe)
                                .collect(Collectors.joining(" and ")));
            }
        }

        private String describe(Occupant occupant) {
            return occupant.toString();
        }

        private void describeExits(StringBuilder sb) {
            sb.append("This room has ").append(room.exits().size()).append(" exits.");
        }
    }
}
