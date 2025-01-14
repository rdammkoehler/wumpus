package com.noradltd.wumpus;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
    public void arrowsKnowWhenTheyFail() {
        Room emptyRoom = new Room();
        Arrow arrow = new Arrow();

        arrow.moveTo(emptyRoom);

        assertThat(arrow.killedAWumpus(), is(false));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void brokenArrowsCannotBeReused() {
        Helpers.programRandomizer(true, true);
        Room batRoom = new Room();
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(batRoom);
        Arrow arrow = new Arrow();

        arrow.moveTo(batRoom);

        assertThat(arrow.isBroken(), is(true));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void unBrokenArrowsCanBeReused() {
        Room batRoom = new Room();
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(batRoom);
        Arrow arrow = new Arrow();
        Helpers.programRandomizer(false, false);

        arrow.moveTo(batRoom);

        assertThat(arrow.isBroken(), is(false));
    }

    @Test
    public void arrowQuiversRunOutOfArrows() {
        Hunter.Quiver quiver = new ArrowQuiver(1);

        quiver.next();

        assertThat(quiver.isEmpty(), is(true));
    }

    @Test
    public void brokenArrowsAreShattered() {
        Arrow arrow = new Arrow();
        arrow.die();

        assertThat(arrow.toString(), is(equalTo("a shattered arrow")));
    }

    @Test
    public void unBrokenArrowsAreViciouslyBarbed() {
        assertThat(new Arrow().toString(), is(equalTo("a nasty looking arrow with a viciously barbed point lies here")));
    }

    @Test
    public void describedArrowsShouldntBe() {
        assertThat(new Arrow().describe(), is(equalTo("--arrows should not be described--")));
    }

    @Disabled("so a null input would just blow up with a null pointer, is that ok?")
    @Test
    public void nullArrowsAreHarmless() {
        // how?
        Arrow.NULL_ARROW.respondTo(null);
        //doesn't throw an exception?
    }

    @Test
    public void emptyQuiversReturnNullArrows() {
        Hunter.Quiver quiver = new ArrowQuiver(0);

        assertThat(quiver.next(), is(Arrow.NULL_ARROW));
    }

    @Test
    public void weCanAskAnArrowQuiverHowManyArrowsRemain() {
        assertThat(new ArrowQuiver(5).arrowsRemaining(), is(equalTo("5")));
    }

    @Test
    public void youCannotPutBrokenArrowsIntoAnArrowQuiver() {
        ArrowQuiver quiver = new ArrowQuiver(0);
        Arrow arrow = new Arrow();
        arrow.die();

        quiver.add(arrow);

        assertThat(quiver.isEmpty(), is(true));
    }
}
