package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArrowTest {
    @Test
    public void arrowsKillWumpi() {
        Wumpus wumpus = new Wumpus();
        Arrow arrow = new Arrow();

        arrow.respondTo(wumpus);

        assertThat(wumpus.isDead(), is(true));
    }
}
