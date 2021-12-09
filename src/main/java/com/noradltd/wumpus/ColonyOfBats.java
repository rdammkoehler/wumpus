package com.noradltd.wumpus;

public class ColonyOfBats extends Room.Occupant {
    @Override
    protected void respondTo(Hunter hunter) {
        Room randomRoom = findRandomRoom();
        Logger.info("A swarm of bats lift you from the ground in a blinding flurry of leathery wings. They drop you in room " + randomRoom.number());
        Logger.debug("Moving hunter to " + randomRoom.number());
        hunter.moveTo(randomRoom);
        Room nextBatRoom = findNewRoomForBats(randomRoom);
        Logger.debug("Relocating bats to " + nextBatRoom.number());
        moveTo(nextBatRoom);
    }

    private Room findNewRoomForBats(Room startingRoom) {
        Room nextBatRoom = startingRoom;
        while(nextBatRoom.equals(startingRoom))
            nextBatRoom = findConnectedRoomARandomDistanceAway(startingRoom);
        return nextBatRoom;
    }

    private Room findRandomRoom() {
        Room currentRoom = getRoom();
        Room nextRoom = currentRoom;
        if (currentRoom.exits().size() > 1) {
            while (getRoom().equals(nextRoom)) {
                nextRoom = findConnectedRoomARandomDistanceAway(nextRoom);
            }
        } else {
            nextRoom = currentRoom.exits().get(0);
        }
        return nextRoom;
    }

    private Room findConnectedRoomARandomDistanceAway(Room currentRoom) {
        Room targetRoom = currentRoom;
        int moves = Random.getRandomizer().nextInt(10);
        try {
            for (int moveCount = 0; moveCount < moves; moveCount++) {
                targetRoom = targetRoom.getRandomExit();
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        return targetRoom;
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
