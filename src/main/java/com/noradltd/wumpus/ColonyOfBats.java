package com.noradltd.wumpus;

public class ColonyOfBats extends Room.Occupant {
    @Override
    public void respondTo(Room.Occupant interloper) {
        if (interloper instanceof Hunter) {
            Hunter hunter = (Hunter) interloper;
            final Room randomRoom = findRandomRoom();
            Logger.info("A swarm of bats lift you from the ground in a blinding flurry of leathery wings. They drop you in room " + randomRoom.number());
            hunter.moveTo(randomRoom);
        }
    }

    private Room findRandomRoom() {
        Room currentRoom = getRoom();
        if (currentRoom.exits().size() > 1) {
            int moves = Random.getRandomizer().nextInt(10) + 1;
            for (int moveCount = 0; moveCount < moves; moveCount++) {
                int exitNumber = Random.getRandomizer().nextInt(currentRoom.exits().size());
                currentRoom = currentRoom.exits().get(exitNumber);
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

    public String toString() {
        return "a horde of blackened leather, slick with the blood of their victims undulating across the ceiling";
    }
}
