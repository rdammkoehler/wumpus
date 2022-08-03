package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoomDisplayTest {
    @Test
    public void aRoomHasAName() {
        assertThat(new Room("Unnamed Room").getName(), equalTo("Unnamed Room"));
    }

    @Test
    public void aRoomHasExits() {
        Room roomOne = new Room("Unnamed Room");
        Room roomTwo = new Room("Unnamed Room");
        roomOne.attachRoom(roomTwo);

        assertThat(roomOne.getAdjacentRooms().size(), equalTo(1));
    }

    @Test
    public void aRoomHasOccupants() {
        Room roomOne = new Room("Unnamed Room");
        Occupant occupant = new Occupant() {
        };
        roomOne.addOccupant(occupant);

        assertThat(roomOne.getOccupants().stream().toList().get(0), equalTo(occupant));
    }

    @Test
    public void aRoomsDescriptionListsNameExitsAndOccupants() {
        String roomName = "First Room";
        int exitCount = 1;
        int occupantCount = 1;
        Room roomOne = new Room(roomName);
        Room roomTwo = new Room("Unnamed Room");
        Occupant occupant = new Occupant() {
        };
        roomOne.addOccupant(occupant);
        roomOne.attachRoom(roomTwo);
        String expectedDescription = "" + roomName + "\nHas " + exitCount + " exit\n\t1\t" + roomTwo.getName() + "\n" + "And " + occupantCount + " occupant\n\t" + occupant.getDescription() + "\n";

        assertThat(roomOne.getDescription(), equalTo(expectedDescription));
    }

    @Test
    public void aRoomsDescriptionContainsTheRoomName() {
        String roomName = "A Room";
        Room roomOne = new Room(roomName);

        assertThat(roomOne.getDescription(), containsString(roomName));
    }

    @Test
    public void aRoomsDescriptionContainsAnExitCount() {
        String roomName = "A Room";
        Room roomOne = new Room(roomName);

        String secondLine = getDescriptionLine(roomOne, 2);
        assertThat(secondLine, matchesPattern("Has \\d+ exits"));
    }

    private static Stream<Arguments> provideExitCountsAndGrammaticallyCorrectExpectations() {
        return Stream.of(
                Arguments.of(0, "exits"),
                Arguments.of(1, "exit"),
                Arguments.of(2, "exits")
        );
    }

    @ParameterizedTest
    @MethodSource("provideExitCountsAndGrammaticallyCorrectExpectations")
    public void aRoomDescriptionExitCountIsGrammaticallyCorrect(int exitCount, String grammaticallyCorrectNoun) {
        Room baseRoom = new Room("Base Room");
        for (int exitIndex = 0; exitIndex < exitCount; exitIndex++) {
            baseRoom.attachRoom(new Room("Room " + exitIndex));
        }

        String secondLine = getDescriptionLine(baseRoom, 2);
        assertThat(secondLine, matchesPattern(".*" + grammaticallyCorrectNoun + "$"));
    }

    @Test
    public void aRoomsDescriptionContainsANamedExit() {
        String roomName = "A Room";
        Room roomOne = new Room("Unnamed Room");
        Room roomTwo = new Room(roomName);
        roomOne.attachRoom(roomTwo);

        String thirdLine = getDescriptionLine(roomOne, 3);
        assertThat(thirdLine, matchesPattern("\t\\d+\t" + roomName));
    }

    @Test
    public void aRoomDescriptionsNamedIndexHasANumericIdentity() {
        String roomName = "A Room";
        Room roomOne = new Room("Unnamed Room");
        Room roomTwo = new Room(roomName);
        roomOne.attachRoom(roomTwo);

        String thirdLine = getDescriptionLine(roomOne, 3);
        assertThat(thirdLine, matchesPattern("\t1+\t" + roomName));
    }

    @Test
    public void aRoomDescriptionsExitsAreAllIndexed() {
        String firstRoomName = "A Room";
        String secondRoomName = "B Room";
        Room roomOne = new Room("Unnamed Room");
        Room roomTwo = new Room(firstRoomName);
        roomOne.attachRoom(roomTwo);
        Room roomThree = new Room(secondRoomName);
        roomOne.attachRoom(roomThree);

        String thirdLine = getDescriptionLine(roomOne, 3);
        assertThat(thirdLine, matchesPattern("\t1\t" + firstRoomName));
        String fourthLine = getDescriptionLine(roomOne, 4);
        assertThat(fourthLine, matchesPattern("\t2\t" + secondRoomName));
    }

    @Test
    public void aRoomsDescriptionContainsAnOccupantCount() {
        String roomName = "A Room";
        Room roomOne = new Room(roomName);

        String thirdLine = getDescriptionLine(roomOne, 3);
        assertThat(thirdLine, matchesPattern("And \\d+ occupants"));
    }

    private static Stream<Arguments> provideOccupantCountsAndGrammaticallyCorrectExpectations() {
        return Stream.of(
                Arguments.of(0, "occupants"),
                Arguments.of(1, "occupant"),
                Arguments.of(2, "occupants")
        );
    }

    @ParameterizedTest
    @MethodSource("provideOccupantCountsAndGrammaticallyCorrectExpectations")
    public void aRoomsDescriptionOccupantCountIsGrammaticallyCorrect(int occupantCount, String grammaticallyCorrectNoun) {
        Room baseRoom = new Room("Base Room");
        for (int occupantIndex = 0; occupantIndex < occupantCount; occupantIndex++) {
            String occupantDescription = "occupant " + (occupantIndex + 1);
            baseRoom.addOccupant(new Occupant() {
                @Override
                public String getDescription() {
                    return occupantDescription;
                }

                @Override
                public int compareTo(Object o) {
                    return getDescription().compareTo(((Occupant)o).getDescription());
                }

            });
        }

        String thirdLine = getDescriptionLine(baseRoom, 3);
        assertThat(thirdLine, matchesPattern(".*" + grammaticallyCorrectNoun + "$"));
    }

    private String getDescriptionLine(Room room, int lineNumber) {
        return room.getDescription().split("\n")[lineNumber-1];
    }
}
