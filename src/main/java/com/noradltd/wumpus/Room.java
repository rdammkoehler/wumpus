package com.noradltd.wumpus;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// TODO [SRP] Room class has multiple responsibilities: manages room topology (exits) AND occupant collection.
//   Consider: Room should only handle topology; create a separate RoomContents or OccupantContainer class.
// TODO [Large Class] Room contains nested Occupant abstract class - consider extracting to its own file
//   for better maintainability and to follow one-class-per-file convention.
class Room implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // TODO [SRP] Occupant is a significant abstraction that deserves its own file. Extract to Occupant.java.
    //   This would also make the Room class more focused on its core responsibility.
    static abstract class Occupant implements Comparable<Occupant>, Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        interface Interaction {
            void execute(Room.Occupant interloper);
        }

        private Room room;
        private Boolean dead = Boolean.FALSE;
        protected transient HashMap<Class<? extends Room.Occupant>, Interaction> interactions;

        protected Occupant() {
            initInteractions();
        }

        protected void initInteractions() {
            interactions = new HashMap<>() {
                @Override
                public Interaction get(Object key) {
                    return super.getOrDefault(key, arg -> {});
                }
            };
        }

        @Serial
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            initInteractions();
        }

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

        void respondTo(Occupant interloper) {
            if (isAlive() && interloper.isAlive()) { // TODO broke one test when added, in Hunter re: Can't kill dead Wumpus
                interactions.get(interloper.getClass()).execute(interloper);
            }
        }

        Boolean isAlive() {
            return !dead;
        }

        Boolean isDead() {
            return dead;
        }

        void die() {
            Logger.debug(getClass().getSimpleName() + " has died!");
            System.err.println(getClass().getSimpleName() + " has died!");
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

    // TODO [Encapsulation] Package-protected mutable static field breaks encapsulation.
    //   This is modified directly by GameStateLoader which creates tight coupling.
    //   Remediation: Use dependency injection - pass RoomNumberer to a RoomFactory instead.
    static RoomNumberer roomNumberer = DEFAULT_ROOM_NUMBERER;

    // Topology
    private final int instanceNumber = roomNumberer.nextRoomNumber();
    private final Set<Room> exits = new HashSet<>();

    // Occupant management (delegated)
    private final OccupantManager occupantManager = new OccupantManager();
    private final InteractionResolver interactionResolver = new InteractionResolver(this, occupantManager);

    Room() {
        Logger.debug("New Room " + this.instanceNumber);
    }

    // Topology methods
    List<Room> exits() {
        return new ArrayList<>(exits);
    }

    Room add(Room exit) {
        connectRooms(this, exit);
        return this;
    }

    private static void connectRooms(Room one, Room two) {
        one.exits.add(two);
        two.exits.add(one);
    }

    Integer number() {
        return hashCode();
    }

    @Override
    public int hashCode() {
        return instanceNumber;
    }

    // Occupant management methods (delegated)
    void add(Occupant occupant) {
        interactionResolver.resolveInteractions(occupant);
    }

    void remove(Occupant occupant) {
        Logger.debug("removing " + occupant.getClass().getSimpleName() + " from " + number());
        occupantManager.removeOccupant(occupant);
    }

    Set<Occupant> occupants() {
        return occupantManager.getOccupants();
    }

    boolean containsSameTypeOfOccupant(Occupant occupant) {
        return occupantManager.containsSameTypeOfOccupant(occupant);
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
            // TODO this appears to sort not by Occupant description but by exit order
            // meaning, it seems, the description is always in the order of the exits
            // presumably by exit identity and therefore the Player can know that the
            // description of the room infers which exit contians the wumpus based on
            // the order of the descriptions.
            // What test can be created to prove this?
            // How can we ensure we have a random order so we're not tipping our hand?
            // I guess first, prove the theory about order.
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
