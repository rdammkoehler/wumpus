package com.noradltd.wumpus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ResetRandomizerExtension.class)
public class GameStateLoaderTest {

    @BeforeEach
    void setup() {
        Helpers.restartRoomNumberer();
    }

    @AfterEach
    void cleanup() {
        Helpers.restartRoomNumberer();
    }

    @Test
    public void loadReconstructsRoomTopology() {
        // Create original state
        Room room1 = new Room();
        Room room2 = new Room();
        Room room3 = new Room();
        room1.add(room2);
        room2.add(room3);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        assertThat(allRooms, hasSize(3));
    }

    @Test
    public void loadReconstructsRoomConnections() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Room entrance = loaded.maze.entrance();
        assertThat(entrance.exits(), hasSize(1));
    }

    @Test
    public void loadReconstructsHunterInCorrectRoom() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        assertThat(loaded.hunter.getRoom().number(), is(2));
    }

    @Test
    public void loadReconstructsHunterArrowCount() {
        Room room = new Room();
        Hunter hunter = new Hunter(new ArrowQuiver(3));
        hunter.moveTo(room);
        GameState state = GameState.fromGame(null, hunter, room);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        assertThat(loaded.hunter.inventory(), containsString("Arrows: 3"));
    }

    @Test
    public void loadReconstructsWumpus() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        long wumpusCount = allRooms.stream()
                .flatMap(r -> r.occupants().stream())
                .filter(Wumpus.class::isInstance)
                .count();
        assertThat(wumpusCount, is(1L));
    }

    @Test
    public void loadReconstructsDeadWumpus() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);
        wumpus.die();
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        Room.Occupant loadedWumpus = allRooms.stream()
                .flatMap(r -> r.occupants().stream())
                .filter(Wumpus.class::isInstance)
                .findFirst()
                .orElse(null);
        assertNotNull(loadedWumpus);
        assertTrue(loadedWumpus.isDead());
    }

    @Test
    public void loadReconstructsBottomlessPit() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        BottomlessPit pit = new BottomlessPit();
        pit.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        long pitCount = allRooms.stream()
                .flatMap(r -> r.occupants().stream())
                .filter(BottomlessPit.class::isInstance)
                .count();
        assertThat(pitCount, is(1L));
    }

    @Test
    public void loadReconstructsColonyOfBats() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        long batsCount = allRooms.stream()
                .flatMap(r -> r.occupants().stream())
                .filter(ColonyOfBats.class::isInstance)
                .count();
        assertThat(batsCount, is(1L));
    }

    @Test
    public void loadReconstructsArrow() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Arrow arrow = new Arrow();
        Helpers.programRandomizer(false); // Arrow doesn't shatter
        arrow.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        long arrowCount = allRooms.stream()
                .flatMap(r -> r.occupants().stream())
                .filter(Arrow.class::isInstance)
                .count();
        assertThat(arrowCount, is(1L));
    }

    @Test
    public void loadReconstructsMultipleOccupantsInSameRoom() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);
        BottomlessPit pit = new BottomlessPit();
        pit.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        Room room2Loaded = allRooms.stream()
                .filter(r -> r.number() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull(room2Loaded);
        assertThat(room2Loaded.occupants(), hasSize(2));
    }

    @Test
    public void loadedGameMazeEntranceIsHunterRoom() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room2);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        assertThat(loaded.maze.entrance(), is(loaded.hunter.getRoom()));
    }

    @Test
    public void loadPreservesRoomNumbers() {
        Room room1 = new Room();
        Room room2 = new Room();
        Room room3 = new Room();
        room1.add(room2);
        room2.add(room3);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        GameState state = GameState.fromGame(null, hunter, room1);

        Helpers.restartRoomNumberer();
        GameStateLoader.LoadedGame loaded = GameStateLoader.load(state);

        Set<Room> allRooms = MazeTraverser.collectAllRooms(loaded.maze.entrance());
        List<Integer> roomNumbers = allRooms.stream().map(Room::number).toList();
        assertThat(roomNumbers, containsInAnyOrder(1, 2, 3));
    }
}
