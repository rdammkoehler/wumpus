package com.noradltd.wumpus;

import java.util.Stack;
import java.util.stream.IntStream;

public class ColonyOfBats extends Room.Occupant {
    {
        interactions.put(Hunter.class,
                interloper -> {
            /* these new rules about Bats and relocating and empty rooms are 'hard' you need to focus */
                    Room originalRoom = interloper.getRoom();
                    Room randomRoom = findRandomRoom();
                    if (randomRoom.number().equals(originalRoom.number())) {
                        Logger.info("A swarm of bats swirls around you, screeching and wailing"); // TODO add test here
                    } else {
                        Logger.info("A swarm of bats lift you from the ground in a blinding flurry of leathery wings. They drop you in room " + randomRoom.number());
                        interloper.moveTo(randomRoom);
                    }
                    Room newBatRoom = findRandomRoom();
                    if (!newBatRoom.number().equals(getRoom().number())) {
                        Logger.debug("Moving the colony of bats to room " + newBatRoom.number());
                        moveTo(newBatRoom);
                    }
                }

        );
    }

    private Room findRandomRoom() {
        Room currentRoom = getRoom();
        Stack<Room> stack = new Stack<>();
        stack.push(currentRoom);
        final int limit = 3;
        for (int count = 0; count < limit && getRoom().equals(currentRoom); count++) {
            // TODO max moves parameterized?
            IntStream.range(0, Random.getRandomizer().nextInt(10) + 1)
                    .forEach(idx ->
                            stack.push(stack.peek()
                                    .exits()
                                    .get(Random.getRandomizer()
                                            .nextInt(stack.peek()
                                                    .exits()
                                                    .size()
                                            )
                                    )
                            )
                    );
            Room candidateRoom = stack.pop();
            Logger.debug("Trying to relocate into room " + candidateRoom.number());
            if (candidateRoom.occupants().isEmpty()) {
                currentRoom = candidateRoom;
            }
        }
        return currentRoom;
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
