package com.noradltd.wumpus;

import java.util.Stack;
import java.util.stream.IntStream;

public class ColonyOfBats extends Room.Occupant {
    @Override
    public void respondTo(Room.Occupant interloper) {
        if (interloper instanceof Hunter hunter) {
            Room randomRoom = findRandomRoom();
            Logger.info("A swarm of bats lift you from the ground in a blinding flurry of leathery wings. They drop you in room " + randomRoom.number());
            hunter.moveTo(randomRoom);
        }
    }

    private Room findRandomRoom() {
        Room currentRoom = getRoom();
        final Stack<Room> stack = new Stack<>();
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
