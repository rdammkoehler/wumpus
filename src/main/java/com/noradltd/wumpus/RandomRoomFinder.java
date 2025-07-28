package com.noradltd.wumpus;

import java.util.Stack;
import java.util.stream.IntStream;

public class RandomRoomFinder {

    public static Room findRandomRoom(Room currentRoom) {
        if (currentRoom.exits().isEmpty()) {
            return currentRoom;
        }
        Room originalRoom = currentRoom;
        Stack<Room> stack = new Stack<Room>();
        stack.push(currentRoom);
        final int limit = 3;
        for (int count = 0; count < limit && originalRoom.equals(currentRoom); count++) {
            // TODO max moves parameterized?
            // herein lies your issue me thinks
            // it maybe when the hunter moves first v. last, Confirmed!
            //      but why?
            //      it seems to matter that the ColonyOfBats is the victim and moves first
            // what the hell does the following code even do?
            //  I think it randomly selects an exit from the room on the top of the stack
            //  I think it gets 'up to 10' exits
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
            // todo why does it seem to _not_ matter that we didn't pop() stack on each iter, are we missing a case?
            if (stack.peek().occupants().isEmpty()) {
                currentRoom = stack.pop();
            }
//            Room candidateRoom = stack.pop();
//            if (candidateRoom.occupants().isEmpty()) {
//                currentRoom = candidateRoom;
//            }
        }
        Logger.debug("Trying to relocate into room " + currentRoom.number());
        return currentRoom;
    }
}