package com.noradltd.wumpus;

import java.util.*;
import java.util.stream.Collectors;

class Room {
    static abstract class Occupant implements Comparable<Occupant> {
        interface Interaction {
            void execute(Room.Occupant interloper);
        }

        private Room room;
        private Boolean dead = Boolean.FALSE;
        protected HashMap<Class<? extends Room.Occupant>, Interaction> interactions = new HashMap<>() {
            @Override
            public Interaction get(Object key) {
                return super.getOrDefault(key, arg -> new Interaction() {
                    @Override
                    public void execute(Room.Occupant interloper) {
                    }
                });
            }
        };

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

    // TODO how do we get this to be NOT package protected?
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
