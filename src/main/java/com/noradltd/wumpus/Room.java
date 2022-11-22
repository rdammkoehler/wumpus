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

    // TODO how do we get this to be NOT package protected?
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
            Logger.debug(interloper.getClass().getSimpleName() + " is interacting with " + occupants.stream()
                    .map(occupant -> occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")")
                    .collect(Collectors.joining(", ")));
            ArrayList<Occupant> copyOfOccupants = new ArrayList<>(occupants);
            for (Occupant cohabitant : copyOfOccupants) {
                if (!interloper.isDead()) {
                    // TODO the following should work effectively the same as the later version but doesn't
//                    List<Occupant> occupantList = Arrays.asList(new Occupant[]{interloper, cohabitant});
//                    if (Random.getRandomizer().nextBoolean()) {
//                        Collections.reverse(occupantList);
//                    }
//                    occupantList.get(0).respondTo(occupantList.get(1));
//                  TODO this could potentially be cleaner, see above
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

    private class RoomDescriber {
        private final Room room;

        private RoomDescriber(Room room) {
            this.room = room;
        }

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
                    .collect(Collectors.toList());
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
