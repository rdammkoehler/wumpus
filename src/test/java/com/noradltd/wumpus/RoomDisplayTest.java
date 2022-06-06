package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoomDisplayTest {
    @Test
    public void aRoomHasAName() {
        assertThat(new Room().getName(), equalTo("Unnamed Room"));
    }

    @Test
    public void aRoomHasExits() {
        Room roomOne = new Room();
        Room roomTwo = new Room();
        roomOne.attachRoom(roomTwo);

        assertThat(roomOne.getAdjacentRooms().size(), equalTo(1));
    }

    @Test
    public void aRoomHasOccupants() {
        Room roomOne = new Room();
        Occupant occupant = new Occupant() {
        };
        roomOne.addOccupant(occupant);

        assertThat(roomOne.getOccupants().get(0), equalTo(occupant));
    }

    @Test
    public void aRoomsDescriptionListsNameExitsAndOccupants() {
        String roomName = "First Room";
        int exitCount = 1;
        int occupantCount = 1;
        Room roomOne = new Room();
        Room roomTwo = new Room();
        Occupant occupant = new Occupant() {
        };
        roomOne.setName(roomName);
        roomOne.addOccupant(occupant);
        roomOne.attachRoom(roomTwo);
        String expectedDescription = "" + roomName + "\nHas " + exitCount + " exit\n\t1\t" + roomTwo.getName() + "\n" + "And " + occupantCount + " occupant\n\t" + occupant.getDescription() + "\n";

        assertThat(roomOne.getDescription(), equalTo(expectedDescription));
    }

    @Test
    public void aRoomsDescriptionContainsTheRoomName() {
        String roomName = "A Room";
        Room roomOne = new Room();
        roomOne.setName(roomName);

        assertThat(roomOne.getDescription(), containsString(roomName));
    }

    @Test
    public void aRoomsDescriptionContainsAnExitCount() {
        String roomName = "A Room";
        Room roomOne = new Room();
        roomOne.setName(roomName);

        String description = roomOne.getDescription();
        String[] lines = description.split("\n");
        String secondLine = lines[1];
        assertThat(secondLine, matchesPattern("Has \\d+ exit"));
    }

    @Test
    public void aRoomsDescriptionContainsANamedExit() {
        String roomName = "A Room";
        Room roomOne = new Room();
        Room roomTwo = new Room();
        roomTwo.setName(roomName);
        roomOne.attachRoom(roomTwo);

        String description = roomOne.getDescription();
        String[] lines = description.split("\n");
        String secondLine = lines[2];
        assertThat(secondLine, matchesPattern("\t\\d+\t" + roomName));
    }

    @Test
    public void aRoomDescriptionsNamedIndexHasANumericIdentity() {
        String roomName = "A Room";
        Room roomOne = new Room();
        Room roomTwo = new Room();
        roomTwo.setName(roomName);
        roomOne.attachRoom(roomTwo);

        String description = roomOne.getDescription();
        String[] lines = description.split("\n");
        String secondLine = lines[2];
        assertThat(secondLine, matchesPattern("\t1+\t" + roomName));
    }

    @Test
    public void aRoomDescriptionsExitsAreAllIndexed() {
        String firstRoomName = "A Room";
        String secondRoomName = "B Room";
        Room roomOne = new Room();
        Room roomTwo = new Room();
        roomTwo.setName(firstRoomName);
        roomOne.attachRoom(roomTwo);
        Room roomThree = new Room();
        roomThree.setName(secondRoomName);
        roomOne.attachRoom(roomThree);

        String description = roomOne.getDescription();
        String[] lines = description.split("\n");
        String secondLine = lines[2];
        assertThat(secondLine, matchesPattern("\t1\t" + firstRoomName));
        String thirdLine = lines[3];
        assertThat(thirdLine, matchesPattern("\t2\t" + secondRoomName));
    }

    @Test
    public void aRoomsDescriptionContainsAnOccupantCount() {
        String roomName = "A Room";
        Room roomOne = new Room();
        roomOne.setName(roomName);

        String description = roomOne.getDescription();
        String[] lines = description.split("\n");
        String secondLine = lines[2];
        assertThat(secondLine, matchesPattern("And \\d+ occupant"));
    }
}
