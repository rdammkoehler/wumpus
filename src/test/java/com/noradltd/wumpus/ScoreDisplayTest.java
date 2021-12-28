package com.noradltd.wumpus;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.captureStdout;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ScoreDisplayTest {
    class Hunter {
        enum InventoryItem {WumpusScalp}

        int scalpCount = 0;

        void addInventory(InventoryItem item) {
            if (item == InventoryItem.WumpusScalp) {
                scalpCount++;
            }
        }
    }

    class Game {
        private Hunter hunter;

        Game(Hunter hunter) {
            this.hunter = hunter;
        }

        void quit() {
            System.out.println("Moves Made:");
            System.out.println("Wumpi Scalps:\t" + hunter.scalpCount);
            System.out.println("Arrows Remaining:");
            System.out.println("Hunters Killed:");
            System.out.println("Game Over");
        }
    }

    @Test
    public void huntersWithoutKillsScoreZeroWumpiScalps() {
        ByteArrayOutputStream out = captureStdout();
        Hunter hunter = new Hunter();
        Game game = new Game(hunter);

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Wumpi Scalps:\t0"));
    }


    @Test
    public void huntersKillsEffectScoreByIncreasingWumpiScalps() {
        ByteArrayOutputStream out = captureStdout();
        Hunter hunter = new Hunter();
        Game game = new Game(hunter);

        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Wumpi Scalps:\t1"));
    }

    @Test
    public void proficientHuntersHaveManyKillsEffectingWumpiScalpCount() {
        ByteArrayOutputStream out = captureStdout();
        Hunter hunter = new Hunter();
        Game game = new Game(hunter);

        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        hunter.addInventory(Hunter.InventoryItem.WumpusScalp);
        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString("Wumpi Scalps:\t5"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Moves Made:", "Wumpi Scalps:", "Arrows Remaining:", "Hunters Killed:", "Game Over"})
    void scoreContainsString(String string) {
        ByteArrayOutputStream out = captureStdout();
        Hunter hunter = new Hunter();
        Game game = new Game(hunter);

        game.quit();

        String playLog = out.toString();
        assertThat(playLog, containsString(string));
    }
}
