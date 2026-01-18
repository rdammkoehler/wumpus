package com.noradltd.wumpus;

import java.io.Serial;

public class ColonyOfBats extends Room.Occupant {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void initInteractions() {
        super.initInteractions();
        interactions.put(Hunter.class,
                interloper -> {
            /* these new rules about Bats and relocating and empty rooms are 'hard' you need to focus */
                    Room originalRoom = interloper.getRoom();
                    Room randomRoom = RandomRoomFinder.findRandomRoom(getRoom());
                    if (randomRoom.number().equals(originalRoom.number())) {
                        Logger.info("A swarm of bats swirls around you, screeching and wailing"); // TODO add test here
                    } else {
                        Logger.info("A swarm of bats lift you from the ground in a blinding flurry of leathery wings. They drop you in room " + randomRoom.number());
                        interloper.moveTo(randomRoom);
                    }
                    Room newBatRoom = RandomRoomFinder.findRandomRoom(getRoom());
                    if (!newBatRoom.number().equals(getRoom().number())) {
                        Logger.debug("Moving the colony of bats to room " + newBatRoom.number());
                        moveTo(newBatRoom);
                    }
                }

        );
    }

    @Override
    public String describe() {
        return "You hear the rustling of leathery wings";
    }

    @Override
    public String toString() {
        return "a horde of blackened leather, slick with the blood of their victims undulating across the ceiling";
    }
}
