package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MazePopulatorTest {

    @Test
    public void aMazePopulatorPlacesWumpiInAMaze() {
        int wumpiCount = 1;
        Room mazeEntrance = new MazeBuilder().withRoomCount(5).build();
        new MazePopulator().populate(mazeEntrance).withWumpi(wumpiCount);

        assertThat(countWumpi(mazeEntrance), equalTo((long) wumpiCount));
    }

    @Test
    public void aMazePopulatorPlacesMultipleWumpiInAMaze() {
        int wumpiCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(5).build();
        new MazePopulator().populate(mazeEntrance).withWumpi(wumpiCount);

        assertThat(countWumpi(mazeEntrance), equalTo((long) wumpiCount));
    }

    @Test
    public void aMazePopulatorCannotPlaceMoreWumpiThanThereAreRooms() {
        int wumpiCount = 5;
        int roomCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(roomCount).build();

        assertThrows(IllegalArgumentException.class, () -> new MazePopulator().populate(mazeEntrance).withWumpi(wumpiCount));
    }

    @Test
    public void aMazePopulatorCannotPlaceNegativeNumbersOfWumpi() {
        int wumpiCount = -1;
        int roomCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(roomCount).build();

        assertThrows(IllegalArgumentException.class, () -> new MazePopulator().populate(mazeEntrance).withWumpi(wumpiCount));
    }

    @Test
    public void aMazePopulatorPlacesBatsInAMaze() {
        int batCount = 1;
        Room mazeEntrance = new MazeBuilder().withRoomCount(5).build();
        new MazePopulator().populate(mazeEntrance).withBats(batCount);

        assertThat(countBats(mazeEntrance), equalTo((long) batCount));
    }

    @Test
    public void aMazePopulatorPlacesMultipleBatsInAMaze() {
        int batCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(5).build();
        new MazePopulator().populate(mazeEntrance).withBats(batCount);

        assertThat(countBats(mazeEntrance), equalTo((long) batCount));
    }

    @Test
    public void aMazePopulatorCannotPlaceMoreBatsThanThereAreRooms() {
        int batCount = -1;
        int roomCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(roomCount).build();

        assertThrows(IllegalArgumentException.class, () -> new MazePopulator().populate(mazeEntrance).withBats(batCount));
    }

    @Test
    public void aMazePopulatorCannotPlaceNegativeNumbersOfBats() {
        int batCount = -1;
        int roomCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(roomCount).build();

        assertThrows(IllegalArgumentException.class, () -> new MazePopulator().populate(mazeEntrance).withBats(batCount));
    }

    @Test
    public void aMazePopulatorPlacesBottomlessPitsInAMaze() {
        int bottomlessPitCount = 1;
        Room mazeEntrance = new MazeBuilder().withRoomCount(5).build();
        new MazePopulator().populate(mazeEntrance).withBottomlessPits(bottomlessPitCount);

        assertThat(countBottomlessPits(mazeEntrance), equalTo((long) bottomlessPitCount));
    }

    @Test
    public void aMazePopulatorPlacesMultipleBottomlessPitsInAMaze() {
        int bottomlessPitCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(5).build();
        new MazePopulator().populate(mazeEntrance).withBottomlessPits(bottomlessPitCount);

        assertThat(countBottomlessPits(mazeEntrance), equalTo((long) bottomlessPitCount));
    }

    @Test
    public void aMazePopulatorCannotPlaceMoreBottomlessPitsThanThereAreRooms() {
        int bottomlessPitCount = 5;
        int roomCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(roomCount).build();

        assertThrows(IllegalArgumentException.class, () -> new MazePopulator().populate(mazeEntrance).withBottomlessPits(bottomlessPitCount));
    }

    @Test
    public void aMazePopulatorCannotPlaceNegativeNumbersOfBottomlessPits() {
        int bottomlessPitCount = -1;
        int roomCount = 2;
        Room mazeEntrance = new MazeBuilder().withRoomCount(roomCount).build();

        assertThrows(IllegalArgumentException.class, () -> new MazePopulator().populate(mazeEntrance).withBottomlessPits(bottomlessPitCount));
    }

    @Test
    public void aMazePopulatorCantPutMoreThanOneTypeOfOccupantInARoom() {
        int wumpiCount = 5;
        Room mazeEntrance = new MazeBuilder().withRoomCount(wumpiCount).build();
        new MazePopulator().populate(mazeEntrance).withWumpi(wumpiCount);

        assertThat(countWumpi(mazeEntrance), equalTo((long) wumpiCount));
    }

}
