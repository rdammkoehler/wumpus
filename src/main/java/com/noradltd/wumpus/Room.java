package com.noradltd.wumpus;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

class Room {
    static abstract class Occupant implements Comparable<Occupant> {
        private static int occupantIdSequence = 0;
        protected int occupantId = occupantIdSequence++;
        protected Room room;
        private Boolean dead = Boolean.FALSE;

        Room getRoom() {
            return room;
        }

        void moveTo(Room newRoom) {
            if (room != null) {
                Logger.debug("Moving " + getClass().getSimpleName() + " from " + room.number() + " to " + newRoom.number());
                room.remove(this);
            } else {
                if (newRoom instanceof Hunter.Quiver quiver) {
                    Logger.debug("Adding " + getClass().getSimpleName() + " to a Quiver");
                } else {
                    Logger.debug("Moving " + getClass().getSimpleName() + " to " + newRoom.number());
                }
            }
            room = newRoom;
            newRoom.add(this);
        }

        protected void respondTo(Arrow arrow) {
        }

        protected void respondTo(BottomlessPit pit) {
        }

        protected void respondTo(ColonyOfBats bats) {
        }

        protected void respondTo(Hunter hunter) {
        }

        protected void respondTo(Wumpus wumpus) {
        }

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

        protected void respondTo(Occupant interloper) {
            if (isCohabitant(interloper)) {
                /*
                 * custom dynamic dispatcher!
                 * Java can't do multi-dispatch. But, we can force it to do 'the right thing' with the following code
                 * still looking for a better solution that doesn't propagate a ton of duplication around
                 *
                 */
                try {
                    getClass().getDeclaredMethod("respondTo", interloper.getClass()).invoke(this, interloper);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    // no-op
                }
            }
        }

        private boolean isCohabitant(Occupant otherOccupant) {
            return getRoom().equals(otherOccupant.getRoom());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Occupant)) return false;
            Occupant occupant = (Occupant) o;
            return occupantId == occupant.occupantId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(occupantId);
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
    protected List<Occupant> occupants = new ArrayList<>();

    List<Room> exits() {
        final ArrayList<Room> rooms = new ArrayList<>(exits);
        rooms.sort(Comparator.comparingInt(Room::number));
        return rooms;
    }

    Room exits(Integer idx) {
        return exits().get(idx);
    }

    void add(Occupant occupant) {
        executeOccupantInteractions(occupant);
        if (!occupant.isDead() && equals(occupant.getRoom()) && !occupants.contains(occupant)) {
            occupants.add(occupant); //a room can't contain the same occupant more than once this should be a set or set like
        }
    }

    private void executeOccupantInteractions(Occupant interloper) {
        if (occupants.size() > 0) {
            new ArrayList<>(occupants).forEach(cohabitant -> interact(cohabitant, interloper));
        } else {
            Logger.debug("this room is empty");
        }
    }

    private void interact(Occupant cohabitant, Occupant interloper) {
        Logger.debug(debugDescriptionOfOccupant(interloper) + " is interacting with " + debugDescriptionOfOccupant(cohabitant));
        Occupant[] participants = Random.getRandomizer().shuffle(cohabitant, interloper);
        Logger.debug(debugDescriptionOfOccupant(participants[0]) + " goes first");
        Arrays.stream(participants)
                .filter(not(Occupant::isDead))
                .forEach(participant -> Arrays.stream(participants)
                        .filter(not(Occupant::isDead))
                        .filter(not(participant::equals)) // the interloper shouldn't be here yet...but might be because of side-effects
                        .forEach(participant::respondTo)
                );
    }

    private String debugDescriptionOfOccupant(Occupant occupant) {
        return occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")";
    }

    private void remove(Occupant occupant) {
        Logger.debug("removing " + occupant.getClass().getSimpleName() + " from " + number());
        occupants.remove(occupant);
    }

    Set<Occupant> occupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
    }

    private void connectRooms(Room one, Room two) {
        one.exits.add(two);
        two.exits.add(one);
    }

    void add(Room exit) {
        connectRooms(this, exit);
    }

    Room getRandomExit() {
        return Random.getRandomizer().shuffle(exits()).get(0);
    }

    Integer number() {
        return hashCode();
    }

    @Override
    public int hashCode() {
        return instanceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return instanceNumber == room.instanceNumber;
    }

    @Override
    public String toString() {
        return new RoomDescriber(this).description();
    }

    private class RoomDescriber {
        private final Room room;
        private final StringBuilder sb = new StringBuilder();

        private RoomDescriber(Room room) {
            this.room = room;
        }

        private String description() {
            sb.append("You are in room #").append(room.number()).append("\n");
            describeExits();
            describeOccupants();
            describeNeighbors();
            if (Logger.isDebugging()) {
                revealNeighbors();
            }
            return sb.toString();
        }

        private void revealNeighbors() {
            sb.append("\n[");

            // TODO there is probably some ultra sexy way to do this with streams but I can't think of it now
            int idx = 1;
            for (Room exit : room.exits()) {
                sb.append("{").append(idx).append("} Room: ").append(exit.number()).append(" (").append(exit.occupants().stream().map(occupant -> occupant.getClass().getSimpleName()).collect(Collectors.joining(", "))).append(") ");
                idx++;
            }
            sb.append("]\n");
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

        private void describeExits() {
            sb.append("This room has ").append(room.exits().size()).append(" exits.");
        }
    }
}
