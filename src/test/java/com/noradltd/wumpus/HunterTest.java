package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class HunterTest {
    @Test
    public void aHunterWillKillAWumpusOnSite() {
        Hunter hunter = new Hunter();
        Wumpus wumpus = new Wumpus();

        hunter.respondTo(wumpus);

        assertThat(wumpus.isDead(), is(true));
        assertThat(hunter.kills(), equalTo(1));
    }

    @Test
    public void huntersLeaveTheRoomWhenTheyDie() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        hunter.die();

        assertThat(room.occupants().size(), equalTo(0));
    }
}
