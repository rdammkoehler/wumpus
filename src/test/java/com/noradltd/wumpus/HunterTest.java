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

    @Test
    public void toStringRevealsOurProwess() {
        assertThat(new Hunter().toString(), is(equalTo("A genuine specimen of Wumpus murdering prowess")));
    }

    @Test
    public void toStringRevealsOurStateInDeath() {
        Hunter deadHunter = new Hunter();

        deadHunter.die();

        assertThat(deadHunter.toString(), is(equalTo("The corpse of an unfortunate soul lies here")));
    }

    @Test
    public void inventoryRevealsOurAmmoCount() {
        assertThat(new Hunter().inventory(), is(equalTo("Inventory:\n\tArrows: 0\n\tWumpus Scalps: 0\n")));
    }

    @Test
    public void inventroyTellsUsHowMuchWeKill() {
        Room room = new Room();
        Hunter killer = new Hunter();
        Wumpus wumpus = new Wumpus();
        killer.moveTo(room);

        wumpus.moveTo(room);

        assertThat(killer.inventory(), is(equalTo("Inventory:\n\tArrows: 0\n\tWumpus Scalps: 1\n")));
    }
}
