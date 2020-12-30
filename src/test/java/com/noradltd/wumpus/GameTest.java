package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

public class GameTest {

    @Test
    public void getScoreReportsTheScore() {
        Game game = new Game(new String[]{});

        assertThat(game.getScore(), matchesPattern("Score: Hunter \\d+ Wumpus \\d+"));
    }

//    @Test // TODO bad test
//    public void getScoreReportsOnePointForWumpusIfTheHunterIsDead() {
//        Game game = new Game(new String[]{"--seed", "0"});
//        game.move(0);
//
//        assertThat(game.getScore(), matchesPattern("Score: Hunter \\d+ Wumpus 1"));
//    }
}
