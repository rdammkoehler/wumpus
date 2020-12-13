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

    @Test
    public void arrowsComeFromQuivers() {
        Hunter.Quiver quiver = new ArrowQuiver(5);

        Arrow arrow = quiver.next();

        assertThat(arrow.killedAWumpus(), is(false));
    }

    @Test
    public void arrowsKnowTheyHaveKilled() {
        Arrow arrow = new Arrow();

        arrow.respondTo(new Wumpus());

        assertThat(arrow.killedAWumpus(), is(true));
    }

    @Test
    public void arrowsCannotBeReused() {

    }

    @Test
    public void arrowQuiversRunOutOfArrows() {
        Hunter.Quiver quiver = new ArrowQuiver(1);

        quiver.next();

        assertThat(quiver.isEmpty(), is(true));
    }
}
