package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.noradltd.wumpus.Helpers.getAllRooms;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MazePopulatorTest {

    class Wumpus implements Occupant {

    }
    class ColonyOfBats implements Occupant {}
    class BottomlessPit implements Occupant {}

    class MazePopulator {
        Random random = new Random();
        private Room maze;

        MazePopulator populate(Room mazeEntrance) {
            maze = mazeEntrance;
            return this;
        }

        MazePopulator withWumpi(int wumpiCount) {
            if (wumpiCount < 0) {
                throw new IllegalArgumentException("Zero or more wumpi per maze, please");
            }
            List<Room> rooms = collectRooms();
            if (wumpiCount > rooms.size()) {
                throw new IllegalArgumentException("Only one wumpi per room, please");
            }
            for (int count = 0; count < wumpiCount; count++) {
                int randomRoomIdx = random.nextInt(rooms.size());
                rooms.get(randomRoomIdx).addOccupant(new Wumpus());
            }
            return this;
        }

        MazePopulator withBats(int batCount) {
            if (batCount < 0) {
                throw new IllegalArgumentException("Zero or more colony of bats per maze, please");
            }
            List<Room> rooms = collectRooms();
            if (batCount > rooms.size()) {
                throw new IllegalArgumentException("Only one colony of bats per room, please");
            }
            for (int count = 0; count < batCount; count++) {
                int randomRoomIdx = random.nextInt(rooms.size());
                rooms.get(randomRoomIdx).addOccupant(new ColonyOfBats());
            }
            return this;
        }

        MazePopulator withBottomlessPits(int pitCount) {
            if (pitCount < 0) {
                throw new IllegalArgumentException("Zero or more bottomless pit per maze, please");
            }
            List<Room> rooms = collectRooms();
            if (pitCount > rooms.size()) {
                throw new IllegalArgumentException("Only one bottomless pit per room, please");
            }
            for (int count = 0; count < pitCount; count++) {
                int randomRoomIdx = random.nextInt(rooms.size());
                rooms.get(randomRoomIdx).addOccupant(new BottomlessPit());
            }
            return this;
        }

        private List<Room> collectRooms() {
            return collectRoom(maze, new HashSet<>()).stream().collect(Collectors.toUnmodifiableList());
        }

        private Set<Room> collectRoom(Room room, Set<Room> rooms) {
            if (rooms.add(room)) {
//                room.getAdjacentRooms().stream().toList().stream().map(adjacentRoom->collectRoom(adjacentRoom, rooms));
                for (Room adjacentRoom : room.getAdjacentRooms()) {
                    collectRoom(adjacentRoom, rooms);
                }
            }
            return rooms;
        }
    }

    boolean hasWumpus(Room room) {
        return room.getOccupants().stream().filter((occupant) -> occupant instanceof Wumpus).count() > 0;
    }

    long countWumpi(Room mazeEntrance) {
        List<Room> rooms = getAllRooms(mazeEntrance);
        return rooms.stream().filter((room) -> hasWumpus(room)).count();
    }

    boolean hasBats(Room room) {
        return room.getOccupants().stream().filter((occupant) -> occupant instanceof ColonyOfBats).count() > 0;
    }

    long countBats(Room mazeEntrance) {
        List<Room> rooms = getAllRooms(mazeEntrance);
        return rooms.stream().filter((room) -> hasBats(room)).count();
    }

    boolean hasBottomlessPits(Room room) {
        return room.getOccupants().stream().filter((occupant) -> occupant instanceof BottomlessPit).count() > 0;
    }

    long countBottomlessPits(Room mazeEntrance) {
        List<Room> rooms = getAllRooms(mazeEntrance);
        return rooms.stream().filter((room) -> hasBottomlessPits(room)).count();
    }

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
    public void aMazePopulatorPlacesBatsInAMaze(){
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
    public void aMazePopulatorPlacesBottomlessPitsInAMaze(){
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


}
