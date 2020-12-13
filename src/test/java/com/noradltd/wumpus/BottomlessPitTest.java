package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
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
}
