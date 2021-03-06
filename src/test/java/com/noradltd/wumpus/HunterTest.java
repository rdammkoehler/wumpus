package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HunterTest {

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aHunterMayKillAWumpusOnSite() {
        Helpers.programRandomizer(new boolean[]{true}, new int[]{0});
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
        assertThat(new Hunter().toString(), is(equalTo("a genuine specimen of Wumpus murdering prowess")));
    }

    @Test
    public void toStringRevealsOurStateInDeath() {
        Hunter deadHunter = new Hunter();

        deadHunter.die();

        assertThat(deadHunter.toString(), is(equalTo("the corpse of an unfortunate soul")));
    }

    @Test
    public void inventoryRevealsOurAmmoCount() {
        assertThat(new Hunter().inventory(), is(equalTo("Inventory:\n\tArrows: 0\n\tWumpus Scalps: 0\n")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void inventroyTellsUsHowMuchWeKill() {
        // TODO this programming of the randomizer is NOT intuitive!
        Helpers.programRandomizer(new boolean[]{true}, new int[]{1});
        Logger.debug(Random.getRandomizer().toString());
        Room room = new Room();
        Hunter killer = new Hunter();
        Wumpus wumpus = new Wumpus();
        killer.moveTo(room);

        wumpus.moveTo(room);

        assertThat(killer.inventory(), is(equalTo("Inventory:\n\tArrows: 0\n\tWumpus Scalps: 1\n")));
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
        Logger.info("START HERE");
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
        Helpers.programRandomizer(new boolean[]{false, false}, new int[]{0});
        Room deathChamber = new Room();
        Room escapePath = new Room();
        deathChamber.add(escapePath);
        Hunter hunter = new Hunter();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(deathChamber);

        hunter.moveTo(deathChamber);

        assertThat(wumpus.getRoom(), is(equalTo(escapePath)));
        assertThat(wumpus.isDead(), is(false));
        assertThat(hunter.kills(), is(0));
    }


    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void huntersCanPickupUnbrokenArrows() {
        Helpers.programRandomizer(false);
        int initialArrowCount = 1;
        Room room = new Room();
        Arrow arrow = new Arrow();
        arrow.moveTo(room);  // Programmed Randomizer ensures an unbroken arrow
        Hunter hunter = new Hunter(new ArrowQuiver(initialArrowCount));
        hunter.moveTo(room);

        hunter.takeArrows();

        assertThat(hunter.inventory(), is(equalTo("Inventory:\n\tArrows: " + (initialArrowCount + 1) + "\n\tWumpus Scalps: 0\n")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void huntersCannotPickupBrokenArrows() {
        Helpers.programRandomizer(true);
        int initialArrowCount = 1;
        Room room = new Room();
        Arrow arrow = new Arrow();
        arrow.moveTo(room);  // Programmed Randomizer ensures a broken arrow
        Hunter hunter = new Hunter(new ArrowQuiver(initialArrowCount));
        hunter.moveTo(room);

        hunter.takeArrows();

        assertThat(hunter.inventory(), is(equalTo("Inventory:\n\tArrows: " + initialArrowCount + "\n\tWumpus Scalps: 0\n")));
    }

    @Test
    public void wumpiRunsFromArrows() {
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        Wumpus wumpus = new Wumpus();
        Room hunterRoom = new Room();
        Room emptyRoom = new Room();
        Room wumpusRoom = new Room();
        hunterRoom.add(emptyRoom);
        emptyRoom.add(wumpusRoom);
        hunter.moveTo(hunterRoom);
        wumpus.moveTo(wumpusRoom);

        hunter.shoot(0);

        assertThat(wumpus.getRoom(), is(equalTo(emptyRoom)));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void wumpiRunsAwayFromArrows() {
        Helpers.programRandomizer(new boolean[]{false}, new int[]{0});  // there is disagreement about 0 or 1
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        Wumpus wumpus = new Wumpus();
        Room hunterRoom = new Room();
        Room emptyRoom = new Room();
        Room wumpusRoom = new Room();
        Room farRoom = new Room();
        hunterRoom.add(emptyRoom);
        emptyRoom.add(wumpusRoom);
        wumpusRoom.add(farRoom);
        hunter.moveTo(hunterRoom);
        wumpus.moveTo(wumpusRoom);

        hunter.shoot(0);

        assertThat("Wumpus is in the farRoom " + AsciiMapper.map(hunterRoom), wumpus.getRoom(), is(equalTo(farRoom)));
    }
}
