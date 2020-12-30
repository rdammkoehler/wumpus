package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BottomlessPitTest {
    @Test
    public void huntersDieInBottomlessPits() {
        Hunter hunter = new Hunter();
        BottomlessPit pit = new BottomlessPit();

        pit.respondTo(hunter);

        assertThat(hunter.isDead(), is(true));
    }

    @Test
    public void wumpusDoNotDieInBottomlessPits() {
        Wumpus wumpus = new Wumpus();
        BottomlessPit pit = new BottomlessPit();

        pit.respondTo(wumpus);

        assertThat(wumpus.isDead(), is(false));
    }

    @Test
    public void bottomlessPitsAreDescribedAsCold() {
        assertThat(new BottomlessPit().describe(), is(equalTo("You feel a cold draft")));
    }

    @Test
    public void bottomlessPitsAreStringifiedAsGapingMaws() {
        assertThat(new BottomlessPit().toString(), is(equalTo("a gaping maw, filled with darkness and emitting an icy breath of despair")));
    }
}
