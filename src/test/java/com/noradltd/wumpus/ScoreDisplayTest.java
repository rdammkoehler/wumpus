package com.noradltd.wumpus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.captureStdout;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ScoreDisplayTest {

    private Hunter hunter;
    private Game game;
    private ByteArrayOutputStream out;

    String playLog() {
        return out.toString();
    }

    @BeforeEach
    public void beforeEach() {
        out = captureStdout();
        Room firstRoom = new Room();
        Room secondRoom = new Room();
        firstRoom.attach(secondRoom);
        hunter = new Hunter(firstRoom);
        game = new Game(hunter);
    }

    @Test
    public void ifAHunterSurvivesTheGameScoreHuntersKilledIsZero() {
        game.quit();

        assertThat(playLog(), containsString("Hunters Killed:\t0"));
    }

    @Test
    public void ifAHunterDiesTheGameScoreHuntersKilledIsOne() {
        hunter.die();
        game.quit();

        assertThat(playLog(), containsString("Hunters Killed:\t1"));
    }

    @Test
    public void ifYouQuitImmediatelyThereAreNoMoves() {
        game.quit();

        assertThat(playLog(), containsString("Moves Made:\t0"));
    }

    @Test
    public void ifYouMoveTheHunterThenMovesIncrease() {
        game.moveHunterThroughExit(0);
        game.quit();

        assertThat(playLog(), containsString("Moves Made:\t1"));
    }

    @Test
    public void ifMoveMoreThanOnceThatIsCountedToo() {
        game.moveHunterThroughExit(0);
        game.moveHunterThroughExit(0);
        game.quit();

        assertThat(playLog(), containsString("Moves Made:\t2"));
    }

    @Test
    public void huntersStartWithFiveArrows() {
        game.quit();

        assertThat(playLog(), containsString("Arrows Remaining:\t5"));
    }

    @Test
    public void huntersExpendArrowsThroughoutTheGame() {
        hunter.removeInventory(Hunter.InventoryItem.Arrow);
        game.quit();

        assertThat(playLog(), containsString("Arrows Remaining:\t4"));
    }

    @Test
    public void huntersCanUseUpAllTheirArrows() {
        hunter.removeInventory(Hunter.InventoryItem.Arrow);
        hunter.removeInventory(Hunter.InventoryItem.Arrow);
        hunter.removeInventory(Hunter.InventoryItem.Arrow);
        hunter.removeInventory(Hunter.InventoryItem.Arrow);
        hunter.removeInventory(Hunter.InventoryItem.Arrow);
        game.quit();

        assertThat(playLog(), containsString("Arrows Remaining:\t0"));
    }

    @Test
    public void huntersWithoutKillsScoreZeroWumpiScalps() {
        game.quit();

        assertThat(playLog(), containsString("Wumpi Scalps:\t0"));
    }


    @Test
    public void huntersKillsEffectScoreByIncreasingWumpiScalps() {
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        game.quit();

        assertThat(playLog(), containsString("Wumpi Scalps:\t1"));
    }

    @Test
    public void proficientHuntersHaveManyKillsEffectingWumpiScalpCount() {
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        game.quit();

        assertThat(playLog(), containsString("Wumpi Scalps:\t5"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Moves Made:", "Wumpi Scalps:", "Arrows Remaining:", "Hunters Killed:", "Game Over"})
    void scoreContainsString(String string) {
        game.quit();

        assertThat(playLog(), containsString(string));
    }
}
