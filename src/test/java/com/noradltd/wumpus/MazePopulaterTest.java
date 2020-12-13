package com.noradltd.wumpus;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class MazePopulaterTest {

    private void checkWumpusPopulation(int roomCount) {
        String[] options = {"--rooms", Integer.toString(roomCount)};
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        assertThat(countMazeOccupantsByType(maze, Wumpus.class), is(Math.max(1, roomCount / 7)));
    }

    @Test
    public void addsThreeWumpiIntoAMazeOfTwentyOneRooms() {
        checkWumpusPopulation(21);
    }

    @Test
    public void addsTwoWumpiIntoAMazeOfFourteenRooms() {
        checkWumpusPopulation(14);
    }

    @Test
    public void addOneWumpiIntoAMazeOfTenRooms() {
        checkWumpusPopulation(10);
    }

    @Test
    public void addOneWumpiIntoAMazeOfFiveRooms() {
        checkWumpusPopulation(5);
    }

    // TODO there is probably some edge case around 1 room

    private void checkPitPopulation(int roomCount) {
        String[] options = {"--rooms", Integer.toString(roomCount)};
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        assertThat(countMazeOccupantsByType(maze, BottomlessPit.class), is(Math.max(1, roomCount / 5)));
    }

    @Test
    public void addsFourBottomlessPitsIntoAMazeOfTwentyRooms() {
        checkPitPopulation(20);
    }

    @Test
    public void addsOneBottomlessPitIntoAMazeOfThreeRooms() {
        checkPitPopulation(3);
    }

    private List<Integer> getRoomIdsContainingOccupantsOfType(Class<? extends Room.Occupant> occupantType, Maze maze) {
        return getAllRooms(maze).stream()
                .map(room -> room.occupants())
                .flatMap(occupants -> occupants.stream())
                .filter(occupant -> occupantType.isInstance(occupant))
                .map(occupant -> occupant.getRoom().number())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean noMoreThanOneOccupantOfTheSameTypePerRoom(Class<? extends Room.Occupant> occupantType) {
        int roomCount = 5;
        int occupantCount = roomCount - 1;
        String optionKey = OPTION_KEY_LOOKUP_BY_OCCUPANT_TYPE.get(occupantType);
        String[] options = {
                "--rooms", Integer.toString(roomCount),
                optionKey, Integer.toString(occupantCount)
        };
        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        List<Integer> occupantRoomIds = getRoomIdsContainingOccupantsOfType(occupantType, maze);

        return countMazeOccupantsByType(maze, occupantType) == (roomCount - 1)
                &&
                occupantRoomIds.size() == occupantCount;
    }

    @Test
    public void noMoreThanOneWumpusPerRoom() {
        assertThat(noMoreThanOneOccupantOfTheSameTypePerRoom(Wumpus.class), is(true));
    }

    @Test
    public void noMoreThanOnePitPerRoom() {
        assertThat(noMoreThanOneOccupantOfTheSameTypePerRoom(BottomlessPit.class), is(true));
    }

    @Test
    public void noMoreThanOneColonyOfBatsPerRoom() {
        assertThat(noMoreThanOneOccupantOfTheSameTypePerRoom(ColonyOfBats.class), is(true));
    }

    @Test
    public void noMoreThanOneOfEachHazardPerRoomButAllInOneRoom() {
        String[] options = {"--rooms", "1"};

        Maze maze = MazeLoader.populate(MazeBuilder.build(options), options);

        Integer countOfUniqueRooms = (int) Arrays.asList(Wumpus.class, BottomlessPit.class, ColonyOfBats.class)
                .stream()
                .map(occupantType -> getRoomIdsContainingOccupantsOfType(occupantType, maze))
                .flatMap(subList -> subList.stream())
                .distinct()
                .count();
        assertThat(countOfUniqueRooms, is(equalTo(1)));
    }

}
