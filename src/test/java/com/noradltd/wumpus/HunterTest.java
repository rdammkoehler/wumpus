package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HunterTest {

    @Test
    public void aHunterWillKillAWumpusOnSite() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);

        hunter.moveTo(room);

        assertThat(wumpus.isDead(), is(true));
        assertThat(hunter.kills(), equalTo(1));
    }

    @Test
    public void huntersBodiesRemainInTheRoomWhenTheyDie() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        hunter.die();

        assertThat(room.occupants().size(), equalTo(1));
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

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void inventroyTellsUsHowMuchWeKill() {
        Helpers.programRandomizer(true, true, true);
        Room room = new Room();
        Hunter killer = new Hunter();
        Wumpus wumpus = new Wumpus();
        killer.moveTo(room);

        wumpus.moveTo(room);

        assertThat(killer.inventory(), is(equalTo("Inventory:\n\tArrows: 0\n\tWumpus Scalps: 1\n")));
    }

    @Test
    public void huntersKnowIfTheyCanNotShoot() {
        assertThat(new Hunter().canShoot(), is(false));
    }

    @Test
    public void huntersKnowIfTheyCanShoot() {
        assertThat(new Hunter(new ArrowQuiver(1)).canShoot(), is(true));
    }

    @Test
    public void huntersKnowHowManyKillsTheyHave() {
        assertThat(new Hunter().kills(), is(equalTo(0)));
    }

    @Test
    public void huntersCanKillAlotOfWumpi() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        hunter.moveTo(room);
        Wumpus wumpus = new Wumpus();

        wumpus.moveTo(room);

        assertThat(hunter.kills(), is(equalTo(1)));
    }

    @Test
    public void byDefaultAnHunterOnlyHasNullArrows() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        hunter.shoot(0);

        assertThat(exit.occupants(), contains(Arrow.NULL_ARROW));
    }

    @Test
    public void huntersCanShootDownTunnels() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        hunter.moveTo(room);

        hunter.shoot(0);

        long arrowCount = exit.occupants().stream().filter(Arrow.class::isInstance).count();
        assertThat(arrowCount, is(equalTo(1L)));
    }

    @Test
    public void huntersCanKillWumpiByShootingDownTunnels() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        hunter.moveTo(room);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(exit);

        hunter.shoot(0);

        assertThat(wumpus.isDead(), is(true));
        assertThat(hunter.kills(), is(equalTo(1)));
    }

    @Test
    public void huntersCanWasteArrows() {
        Room room = new Room();
        Room exit0 = new Room();
        Room exit1 = new Room();
        room.add(exit0);
        room.add(exit1);
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        hunter.moveTo(room);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(exit1);

        hunter.shoot(0);  // would be more expressive to look up the exit id

        assertThat(wumpus.isDead(), is(false));
        assertThat(hunter.kills(), is(equalTo(0)));
    }

    @Test
    public void shootingArrowsToInvalidExistDoesNotWasteArrows() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        hunter.moveTo(room);

        hunter.shoot(1);

        assertThat(hunter.canShoot(), is(true));
    }

    @Test
    public void huntersDontMoveToInvalidLocations() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        hunter.moveTo(1);

        assertThat(hunter.getRoom(), is(equalTo(room)));
    }

    @Test
    public void huntersMoveToAvailableRooms() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        hunter.moveTo(0);

        assertThat(hunter.getRoom(), is(equalTo(exit)));
    }

    @Test
    public void huntersAreDescribedAsThePresenceOfDeath() {
        assertThat(new Hunter().describe(), is(equalTo("You sense the presence of death")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void wumpiCanEscapeAHunterByFleeingBeforeTheHunterStrikesWithHisKnife() {
        Room deathChamber = new Room();
        Room escapePath = new Room();
        deathChamber.add(escapePath);
        Hunter hunter = new Hunter();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(deathChamber);
        Helpers.programRandomizer(true, false);

        hunter.moveTo(deathChamber);

        assertThat(wumpus.getRoom(), is(equalTo(escapePath)));
        assertThat(hunter.kills(), is(0));
    }


    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void huntersCanPickupUnbrokenArrows() {
        Helpers.programRandomizer(false);
        final int initialArrowCount = 1;
        Room room = new Room();
        Arrow arrow = new Arrow();
        arrow.moveTo(room);  // Programmed Randomizer ensures an unbroken arrow
        Hunter hunter = new Hunter(new ArrowQuiver(initialArrowCount));
        hunter.moveTo(room);

        hunter.takeArrow();

        assertThat(hunter.inventory(), is(equalTo("Inventory:\n\tArrows: " + (initialArrowCount + 1) + "\n\tWumpus Scalps: 0\n")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void huntersCannotPickupBrokenArrows() {
        Helpers.programRandomizer(true);
        final int initialArrowCount = 1;
        Room room = new Room();
        Arrow arrow = new Arrow();
        arrow.moveTo(room);  // Programmed Randomizer ensures a broken arrow
        Hunter hunter = new Hunter(new ArrowQuiver(initialArrowCount));
        hunter.moveTo(room);

        hunter.takeArrow();

        assertThat(hunter.inventory(), is(equalTo("Inventory:\n\tArrows: " + initialArrowCount + "\n\tWumpus Scalps: 0\n")));
    }
}
