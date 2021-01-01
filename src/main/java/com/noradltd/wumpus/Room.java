package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

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
                Logger.info(newRoom.toString());  // TODO sneaky and confusing, report only if we were previously in a room suppresses startup noise BUT this code now doesn't make sense
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

    // TODO how do we get this to be NOT package protected? DI?
    static RoomNumberer roomNumberer = DEFAULT_ROOM_NUMBERER;
    private final int instanceNumber = roomNumberer.nextRoomNumber();
    private final Set<Room> exits = new HashSet<>();
    private List<Occupant> occupants = new ArrayList<>();

    List<Room> exits() {
        return new ArrayList<>(exits);
    }

    Room add(Occupant occupant) {
        executeOccupantInteractions(occupant);
        return this;
    }

    private void executeOccupantInteractions(Occupant interloper) {
        if (occupants.size() > 0) {
            new ArrayList<>(occupants).stream()
                    .filter(not(Occupant::isDead))
                    .forEach(cohabitant -> interact(cohabitant, interloper));
        } else {
            Logger.debug("this room is empty");
        }
        if (!interloper.isDead() && interloper.getRoom().equals(this)) { // bad casting, please fix
            occupants.add(interloper);
        }
    }

    private void interact(Occupant cohabitant, Occupant interloper) {
        if (!interloper.isDead()) {
            Logger.debug(interloper.getClass().getSimpleName() + "(" + ((interloper.isDead()) ? "DEAD" : "ALIVE") + ")"
                    + " is interacting with " +
                    cohabitant.getClass().getSimpleName() + "(" + ((cohabitant.isDead()) ? "DEAD" : "ALIVE") + ")");
            Occupant[] participants = Random.getRandomizer().shuffle(cohabitant, interloper);
            Arrays.stream(participants)
                    .forEach(participant ->
                            Arrays.stream(participants)
                                    .filter(x -> !x.equals(participant))
                                    .forEach(participant::respondTo)
                    );
        }
    }


    void remove(Occupant occupant) {
        Logger.debug("removing " + occupant.getClass().getSimpleName() + " from " + number());
        occupants.remove(occupant);
    }

    Set<Occupant> occupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
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

    private class RoomDescriber {
        private final Room room;
        private StringBuilder sb = new StringBuilder();

        private RoomDescriber(Room room) {
            this.room = room;
        }

        private String description() {
            sb.append("You are in room #").append(room.number()).append("\n");
            describeExits();
            describeOccupants();
            describeNeighbors();
            return sb.toString();
        }

        private void describeNeighbors() {
            sb.append("\n");
            room.exits().stream()
                    .flatMap(exit -> exit.occupants().stream())
                    .filter(not(Arrow.class::isInstance))
                    .map(Occupant::describe)
                    .distinct()
                    .sorted()
                    .forEach(description -> sb.append(description).append("\n"));
        }

        private void describeOccupants() {
            String contents = room.occupants().stream()
                    .filter(this::notADeadHunter)
                    .sorted()
                    .map(Occupant::toString)
                    .collect(Collectors.joining(" and "));
            if (contents.length() > 0) {
                sb.append("\nContains ").append(contents);
            }
        }

        private boolean notADeadHunter(Occupant occupant) {
            return !(occupant instanceof Hunter && !occupant.isDead());
        }

        private void describe(Occupant occupant) {
            sb.append(occupant.toString());
        }

        private void describeExits() {
            sb.append("This room has ").append(room.exits().size()).append(" exits.");
        }
    }
}
