package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RoomTest {
    @Test
    public void roomsHaveExits() {
        assertThat(new Room().exits(), is(notNullValue()));
    }

    @Test
    public void roomsAcceptAdditionalExits() {
        Room room = new Room();
        Room exit = new Room();

        room.add(exit);

        assertThat(room.exits(), hasItem(exit));
    }

    @Test
    public void roomExitCanExitToRoom() {
        Room room = new Room();
        Room exit = new Room();

        room.add(exit);

        assertThat(exit.exits(), hasItem(room));
    }

    @Test
    public void roomCanHaveManyExits() {
        Room room = new Room();
        List<Room> exits = new ArrayList<>();
        for (int idx = 0; idx < 10; idx++) {
            Room exit = new Room();
            exits.add(exit);
            room.add(exit);
        }

        exits.forEach(exit -> {
            assertThat(room.exits().contains(exit), is(true));
            assertThat(exit.exits().contains(room), is(true));
        });
    }

    @Test
    public void roomsHaveRoomNumbers() {
        assertThat(new Room().number(), is(greaterThan(0)));
    }

    @Test
    public void roomNumbersAreSequential() {
        Room one = new Room(), two = new Room();

        assertThat(two.number() - one.number(), is(1));
    }

    @Test
    public void roomOccupantsInteractWithNewComersWumpusEatsHunter() {
        Helpers.programRandomizer(true, true);
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);
        Hunter hunter = new Hunter();

        hunter.moveTo(room);

        Helpers.resetRandomizer();

        assertThat(hunter.isDead(), is(0 == room.exits().size()));
        assertThat(wumpus.isFed(), is(true));

    }


    @Test
    public void roomOccupantsInteractWithNewComersWumpusFleesHunter() {
        Helpers.programRandomizer(false);
        Room room = new Room();
        room.add(new Room());
        room.add(new Room());
        room.add(new Room());
        Wumpus wumpus = new Wumpus();
        Hunter hunter = new Hunter();
        wumpus.moveTo(room);

        hunter.moveTo(room);

        Helpers.resetRandomizer();

        assertThat(hunter.isDead(), is(false));
        assertThat(wumpus.isFed(), is(false));

    }

    @Test
    public void emptyRoomsAreDescribed() {
        assertThat(new Room().toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\n"));
    }

    @Test
    public void roomsWithSingleExitsAreDescribed() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 1 exits.\\n"));
    }

    @Test
    public void roomsWithMultipleExitsAreDescribed() {
        Room room = new Room();
        Room exit0 = new Room();
        Room exit1 = new Room();
        room.add(exit0);
        room.add(exit1);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 2 exits.\\n"));
    }

    @Test
    public void huntersDontShowInTheDescription() {
        Room room = new Room();
        Hunter hunter = new Hunter();
        hunter.moveTo(room);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\n"));
    }

    @Test
    public void huntersCanBeDetectedNearby() {
        Room room = new Room();
        Room adjacentRoom = new Room();
        room.add(adjacentRoom);
        Hunter hunter = new Hunter();
        hunter.moveTo(adjacentRoom);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 1 exits.\\nYou sense the presence of death\\n"));
    }

    @Test
    public void roomsWithBottomlessPitsAreDescribed() {
        Room room = new Room();
        BottomlessPit pit = new BottomlessPit();
        pit.moveTo(room);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\nContains a BottomlessPit\\n"));
    }

    @Test
    public void roomsWithColoniesOfBatsAreDescribed() {
        Room room = new Room();
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(room);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\nContains a ColonyOfBats\\n"));
    }

    @Test
    public void roomsWithWumpiAreDescribed() {
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\nContains a Wumpus\\n"));
    }

    @Test
    public void roomsWithDeadWumpiDoNotDescribeTheWumpi() {
        Room room = new Room();
        Wumpus wumpus = new Wumpus();
        wumpus.moveTo(room);
        wumpus.die();

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\n"));
    }

    // TODO might want to use the toString for each occupant rather than the class name

    @Test
    public void roomsCanHaveAllTheHazards() {
        Room room = new Room();
        new Wumpus().moveTo(room);
        new BottomlessPit().moveTo(room);
        new ColonyOfBats().moveTo(room);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 0 exits.\\nContains a BottomlessPit and a ColonyOfBats and a Wumpus\\n"));
    }

    @Test
    public void adjacentWumpusSmellBad() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        new Wumpus().moveTo(exit);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 1 exits.\\nYou smell something foul\\n"));
    }

    @Test
    public void adjacentPitsAreCold() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        new BottomlessPit().moveTo(exit);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 1 exits.\\nYou feel a cold draft\\n"));
    }

    @Test
    public void adjacentBatsMakeNoise() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        new ColonyOfBats().moveTo(exit);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 1 exits.\\nYou hear the rustling of leathery wings\\n"));
    }

    @Test
    public void adjacentHazzardsAreDescribedTogether() {
        Room room = new Room();
        Room exit = new Room();
        room.add(exit);
        new BottomlessPit().moveTo(exit);
        new ColonyOfBats().moveTo(exit);
        new Wumpus().moveTo(exit);

        assertThat(room.toString(), matchesPattern("You are in room #\\d+\\nThis room has 1 exits.\\nYou feel a cold draft\\nYou hear the rustling of leathery wings\\nYou smell something foul\\n"));
    }
}