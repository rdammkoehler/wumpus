package com.noradltd.wumpus;

import java.util.Stack;
import java.util.stream.IntStream;

public class ColonyOfBats extends Room.Occupant {
    {
        interactions.put(Hunter.class,
                interloper -> {
                    Room randomRoom = findRandomRoom();
                    Logger.info("A swarm of bats lift you from the ground in a blinding flurry of leathery wings. They drop you in room " + randomRoom.number());
                    interloper.moveTo(randomRoom);
                    // TODO should the Colony return to it's original location or find a new one?
                }

        );
    }

    private Room findRandomRoom() {
        Room currentRoom = getRoom();
        Stack<Room> stack = new Stack<>();
        stack.push(currentRoom);
        if (currentRoom.exits().size() > 1) {
            while (getRoom().equals(currentRoom)) {
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
                // TODO: Original rules state the room you get dropped in is empty
                currentRoom = stack.pop();
            }
        } else {
            currentRoom = currentRoom.exits().get(0);
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
