package com.noradltd.wumpus;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

class InteractionResolver implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Room room;
    private final OccupantManager occupantManager;

    InteractionResolver(Room room, OccupantManager occupantManager) {
        this.room = room;
        this.occupantManager = occupantManager;
    }

    void resolveInteractions(Room.Occupant interloper) {
        if (occupantManager.isEmpty()) {
            Logger.debug("this room is empty");
        } else {
            if (!interloper.isDead()) {
                logInteractionStart(interloper);
                executeInteractionsWithOccupants(interloper);
            }
        }
        addInterloperIfStillAliveAndPresent(interloper);
    }

    private void logInteractionStart(Room.Occupant interloper) {
        Logger.debug(interloper.getClass().getSimpleName() + " is interacting with " +
                occupantManager.getOccupantsSnapshot().stream()
                        .map(occupant -> occupant.getClass().getSimpleName() + "(" + ((occupant.isDead()) ? "DEAD" : "ALIVE") + ")")
                        .collect(Collectors.joining(", ")));
    }

    private void executeInteractionsWithOccupants(Room.Occupant interloper) {
        List<Room.Occupant> currentOccupants = occupantManager.getOccupantsSnapshot();
        for (Room.Occupant cohabitant : currentOccupants) {
            if (Random.getRandomizer().nextBoolean()) {
                interact(interloper, cohabitant);
            } else {
                interact(cohabitant, interloper);
            }
        }
    }

    private void interact(Room.Occupant instigator, Room.Occupant victim) {
        Logger.debug(" victim " + victim.getClass().getSimpleName() + " responding to " + instigator.getClass().getSimpleName());
        victim.respondTo(instigator);
        if (isInSameRoom(instigator, victim)) {
            Logger.debug(" instigator " + instigator.getClass().getSimpleName() + " responding to " + victim.getClass().getSimpleName());
            instigator.respondTo(victim);
        }
    }

    private static boolean isInSameRoom(Room.Occupant instigator, Room.Occupant victim) {
        // TODO: Law of Demeter Principle violation.
        // This logic should be moved to a method on the Occupant class,
        // such as `isInSameRoomAs(Occupant other)`.
        return victim.getRoom().number().equals(instigator.getRoom().number());
    }

    private void addInterloperIfStillAliveAndPresent(Room.Occupant interloper) {
        if (!interloper.isDead() && interloper.getRoom().equals(room)) {
            occupantManager.addOccupant(interloper);
        }
    }
}
