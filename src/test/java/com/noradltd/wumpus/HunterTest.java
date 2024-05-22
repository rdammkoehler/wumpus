package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HunterTest {

    // TODO this is relying on the order of the randomizer
    //  and it doesn't force the values
    //  and it is effectively similar to some other tests that ultimately check this interaction
    //  here we need to force the randomizer
    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aHunterWillKillAWumpusOnSite() {
        Helpers.programRandomizer(false, true);
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
        Helpers.programRandomizer(true, true, true);
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
        Helpers.resetRandomizer();
        Room room = new Room();
        Room exit0 = new Room();
        Room exit1 = new Room();
        room.add(exit0);
        room.add(exit1);
        // #      exit0
        // #     /
        // # room
        // #     \
        // #      exit1
        Hunter hunter = new Hunter(new ArrowQuiver(1));
        hunter.moveTo(room);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(exit1);
        // TODO this is ugly
        int target = room.exits().get(0).occupants().isEmpty() ? 0 : 1;

        hunter.shoot(target);

        assertThat(wumpus.isDead(), is(false));  // TODO suddenly this is broken! passes by itself, but fails with full suite of tests
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

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void wumpiKillHuntersWhenHunterMisses() {
        Helpers.programRandomizer(true, false, true);
        Room deathChamber = new Room();
        Room escapePath = new Room();
        deathChamber.add(escapePath);
        Hunter hunter = new Hunter();
        hunter.moveTo(deathChamber);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(deathChamber);

        assertThat(hunter.isDead(), is(true));
        assertThat(wumpus.isDead(), is(false));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void wumpiFleeHuntersWhenHunterMisses() {
        Helpers.programRandomizer(true, false, false);
        Room deathChamber = new Room();
        Room escapePath = new Room();
        deathChamber.add(escapePath);
        Hunter hunter = new Hunter();
        hunter.moveTo(deathChamber);
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(deathChamber);

        assertThat(hunter.isDead(), is(false));
        assertThat(wumpus.isDead(), is(false));
    }

    @Test
    public void deadHuntersSmellBad() {
        Hunter hunter = new Hunter();
        hunter.die();

        assertThat(hunter.describe(), is(equalTo("You smell the mouldering of a corpse")));
    }

    @Test
    public void youCannotKillADeadWumpus() {
        Hunter hunter = new Hunter();
        Wumpus wumpus = new Wumpus();
        wumpus.die();

        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            hunter.kill(wumpus);
        } finally {
            Helpers.resetStdout();
        }
        assertThat(stdout.toString(), is(emptyString()));
    }

    @Test
    public void hunterCannotKillAWumpusFromAnotherRoom() {
        Room huntersRoom = new Room();
        Hunter hunter = new Hunter();
        hunter.moveTo(huntersRoom);
        Room wumpusRoom = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(wumpusRoom);

        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            hunter.kill(wumpus);
        } finally {
            Helpers.resetStdout();
        }
        assertThat(stdout.toString(), is(emptyString()));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void hunterCanPickupArrows() {
        Helpers.programRandomizer(false);
        Hunter.Quiver quiver = new ArrowQuiver(0);
        Room room = new Room();
        Hunter hunter = new Hunter(quiver);
        Arrow arrow = new Arrow();
        arrow.moveTo(room);
        hunter.moveTo(room);

        hunter.takeArrow();

        assertThat(quiver.arrowsRemaining(), is(equalTo("1")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void hunterCantPickupBrokenArrows() {
        Helpers.programRandomizer(true);
        Hunter.Quiver quiver = new ArrowQuiver(0);
        Room room = new Room();
        Hunter hunter = new Hunter(quiver);
        Arrow arrow = new Arrow();
        arrow.moveTo(room);
        hunter.moveTo(room);

        hunter.takeArrow();

        assertThat(quiver.arrowsRemaining(), is(equalTo("0")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void huntersWithoutQuiversCantTakeArrows() {
        Helpers.programRandomizer(false);
        Hunter hunter = new Hunter();
        Room room = new Room();
        Arrow arrow = new Arrow();
        arrow.moveTo(room);
        hunter.moveTo(room);
        Pattern arrowsAreZero = Pattern.compile(".*Arrows: 0.*", Pattern.DOTALL);

        hunter.takeArrow();

        assertThat(hunter.inventory(), matchesPattern(arrowsAreZero));
    }

    @Test
    public void huntersWithoutQuiversCantShootArrows() {
        Room room0 = new Room();
        Room room1 = new Room();
        room0.add(room1);
        Hunter hunter = new Hunter();
        hunter.moveTo(room0);
        Pattern noMoreArrows = Pattern.compile(".*You have no more arrows.*", Pattern.DOTALL);

        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            hunter.shoot(0);
        } finally {
            Helpers.resetStdout();
        }
        assertThat(stdout.toString(), matchesPattern(noMoreArrows));
    }

    @Test
    public void byDefaultAnHunterOnlyHasNullArrows() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        Hunter hunter = new Hunter();
        hunter.moveTo(room);
        Pattern noMoreArrows = Pattern.compile(".*You have no more arrows.*", Pattern.DOTALL);

        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            hunter.shoot(0);
        } finally {
            Helpers.resetStdout();
        }
        assertThat(stdout.toString(), matchesPattern(noMoreArrows));
    }
}
