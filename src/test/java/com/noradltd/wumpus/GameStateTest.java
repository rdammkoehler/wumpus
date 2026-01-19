package com.noradltd.wumpus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ResetRandomizerExtension.class)
public class GameStateTest {

    private static final String TEST_SAVE_FILE = "test_game_state.json";

    @BeforeEach
    void setup() {
        new File(TEST_SAVE_FILE).delete();
        Helpers.restartRoomNumberer();
    }

    @AfterEach
    void cleanup() {
        new File(TEST_SAVE_FILE).delete();
        Helpers.restartRoomNumberer();
    }

    @Test
    public void fromGameCapturesRoomTopology() {
        Room room1 = new Room();
        Room room2 = new Room();
        Room room3 = new Room();
        room1.add(room2);
        room2.add(room3);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);

        GameState state = GameState.fromGame(null, hunter, room1);

        assertThat(state.rooms, hasSize(3));
        assertThat(state.rooms.stream().map(r -> r.number).toList(), containsInAnyOrder(1, 2, 3));
    }

    @Test
    public void fromGameCapturesHunterState() {
        Room room = new Room();
        Hunter hunter = new Hunter(new ArrowQuiver(3));
        hunter.moveTo(room);

        GameState state = GameState.fromGame(null, hunter, room);

        assertThat(state.hunter.roomNumber, is(1));
        assertThat(state.hunter.dead, is(false));
        assertThat(state.hunter.arrowCount, is(3));
        assertThat(state.hunter.kills, is(0));
    }

    @Test
    public void fromGameCapturesDeadHunterState() {
        Room room = new Room();
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room);
        hunter.die();

        GameState state = GameState.fromGame(null, hunter, room);

        assertThat(state.hunter.dead, is(true));
    }

    @Test
    public void fromGameCapturesOccupants() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);

        GameState state = GameState.fromGame(null, hunter, room1);

        assertThat(state.occupants, hasSize(1));
        assertThat(state.occupants.get(0).type, is("Wumpus"));
        assertThat(state.occupants.get(0).roomNumber, is(2));
        assertThat(state.occupants.get(0).dead, is(false));
    }

    @Test
    public void fromGameCapturesDeadOccupants() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);
        wumpus.die();

        GameState state = GameState.fromGame(null, hunter, room1);

        assertThat(state.occupants.get(0).dead, is(true));
    }

    @Test
    public void fromGameCapturesArrowState() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Arrow arrow = new Arrow();
        arrow.moveTo(room2);

        GameState state = GameState.fromGame(null, hunter, room1);

        GameState.OccupantState arrowState = state.occupants.stream()
                .filter(o -> o.type.equals("Arrow"))
                .findFirst()
                .orElse(null);
        assertNotNull(arrowState);
        assertThat(arrowState.broken, is(notNullValue()));
    }

    @Test
    public void fromGameCapturesScoreData() {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);
        wumpus.die();

        GameState state = GameState.fromGame(null, hunter, room1);

        assertThat(state.score.wumpusKills, is(1L));
        assertThat(state.score.hunterDeaths, is(0L));
    }

    @Test
    public void saveToFileCreatesJsonFile() throws IOException {
        Room room = new Room();
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room);
        GameState state = GameState.fromGame(null, hunter, room);

        state.saveToFile(TEST_SAVE_FILE);

        File file = new File(TEST_SAVE_FILE);
        assertTrue(file.exists());
        String content = Files.readString(file.toPath());
        assertThat(content, containsString("\"rooms\""));
        assertThat(content, containsString("\"hunter\""));
        assertThat(content, containsString("\"score\""));
    }

    @Test
    public void loadFromFileRestoresState() throws IOException {
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);
        GameState originalState = GameState.fromGame(null, hunter, room1);
        originalState.saveToFile(TEST_SAVE_FILE);

        GameState loadedState = GameState.loadFromFile(TEST_SAVE_FILE);

        assertThat(loadedState.rooms, hasSize(originalState.rooms.size()));
        assertThat(loadedState.hunter.roomNumber, is(originalState.hunter.roomNumber));
        assertThat(loadedState.hunter.arrowCount, is(originalState.hunter.arrowCount));
        assertThat(loadedState.occupants, hasSize(originalState.occupants.size()));
    }

    @Test
    public void roomStateFromRoomCapturesExits() {
        Room room1 = new Room();
        Room room2 = new Room();
        Room room3 = new Room();
        room1.add(room2);
        room1.add(room3);

        GameState.RoomState roomState = GameState.RoomState.fromRoom(room1);

        assertThat(roomState.number, is(1));
        assertThat(roomState.exits, hasSize(2));
        assertThat(roomState.exits, containsInAnyOrder(2, 3));
    }

    @Test
    public void hunterStateFromHunterCapturesKills() {
        // Set up rooms
        Room room1 = new Room();
        Room room2 = new Room();
        room1.add(room2);

        // Place hunter first
        Hunter hunter = new Hunter(new ArrowQuiver(5));
        hunter.moveTo(room1);

        // Shoot an arrow into the wumpus room (simulates kill via arrow)
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room2);

        // Program randomizer: true for hunter to kill, false to prevent other random effects
        Helpers.programRandomizer(true, false, false, false);
        hunter.shoot(0); // Shoot through first exit (to room2 with wumpus)

        GameState.HunterState hunterState = GameState.HunterState.fromHunter(hunter);

        assertThat(hunterState.kills, is(1));
    }

    @Test
    public void occupantStateFromOccupantCapturesType() {
        BottomlessPit pit = new BottomlessPit();
        Room room = new Room();
        pit.moveTo(room);

        GameState.OccupantState state = GameState.OccupantState.fromOccupant(pit);

        assertThat(state.type, is("BottomlessPit"));
        assertThat(state.roomNumber, is(1));
    }

    @Test
    public void occupantStateFromColonyOfBatsCapturesType() {
        ColonyOfBats bats = new ColonyOfBats();
        Room room = new Room();
        bats.moveTo(room);

        GameState.OccupantState state = GameState.OccupantState.fromOccupant(bats);

        assertThat(state.type, is("ColonyOfBats"));
    }
}
