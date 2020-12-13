package com.noradltd.wumpus;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class HunterTest {
    @Test
    public void aHunterWillKillAWumpusOnSite() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        Wumpus wumpus = new Wumpus();
        hunter.moveTo(room);

        wumpus.moveTo(room);

        assertThat(wumpus.isDead(), is(true));
        assertThat(hunter.kills(), equalTo(1));
    }

    // TODO maybe not? Maybe the corpse lays there...silly since the game is over, but what if you have 'lives'?
    @Ignore
    public void huntersLeaveTheRoomWhenTheyDie() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        hunter.die();

        assertThat(room.occupants().size(), equalTo(0));
    }
}
