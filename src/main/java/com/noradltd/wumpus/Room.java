package com.noradltd.wumpus;

import java.lang.reflect.InvocationTargetException;
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
        if (!occupant.isDead() && equals(occupant.getRoom())) {
            occupants.add(occupant);
        }
        return this;
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
                        .filter(not(participant::equals))
                        .forEach(participant::respondTo)
                );
    }

    private String debugDescriptionOfOccupant(Occupant occupant) {
        return occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")";
    }

    void remove(Occupant occupant) {
        Logger.debug("removing " + occupant.getClass().getSimpleName() + " from " + number());
        occupants.remove(occupant);
    }

    Set<Occupant> occupants() {
        return occupants.stream().collect(Collectors.toUnmodifiableSet());
    }

    // TODO does this _need_ to be static?
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

        private void describeExits() {
            sb.append("This room has ").append(room.exits().size()).append(" exits.");
        }
    }
}
