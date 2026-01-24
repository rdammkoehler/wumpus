package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ColonyOfBatsTest {
    @Test
    public void aColonyOfBatsMovesAHunterToAnotherRoom() {
        Room startingRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(finishingRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        Hunter hunter = new Hunter();

        hunter.moveTo(startingRoom);

        assertThat(hunter.getRoom(), is(not(equalTo(startingRoom))));
    }

    @Test
    public void aColonyOfBatsIgnoresAWumpus() {
        Room room = new Room();
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(room);
        Wumpus wumpus = new Wumpus();

        wumpus.moveTo(room);

        assertThat(wumpus.getRoom(), is(equalTo(room)));
    }

    @Test
    public void describeColonyOfBatsTellsUsWhatWeKnow() {
        assertThat(new ColonyOfBats().describe(), is(equalTo("You hear the rustling of leathery wings")));
    }

    @Test
    public void toStringIllustratesTheHorror() {
        assertThat(new ColonyOfBats().toString(), is(equalTo("a horde of blackened leather, slick with the blood of their victims undulating across the ceiling")));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aColonyOfBatsMovesAHunterToARandomRoomFarFarAway() {
        final int roomCount = 13;
        final int startingRoomIdx = 1;
        Room[] rooms = new Room[roomCount];
        Helpers.restartRoomNumberer();
        for (int idx = 0; idx < rooms.length; idx++) {
            rooms[idx] = new Room();
            if (idx > 0) {
                rooms[idx - 1].add(rooms[idx]);
            }
        }
        // New algorithm: candidates sorted by room number are [#1,#3,#4,...,#13] (12 rooms, excluding #2)
        // To select room #13 (rooms[12]), we need index 11
        Helpers.programRandomizer(11);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(rooms[startingRoomIdx]);
        Hunter hunter = new Hunter();

        hunter.moveTo(rooms[startingRoomIdx]);

        assertThat(hunter.getRoom(), is(equalTo(rooms[roomCount - 1])));
    }

    // TODO flakey test
    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aColonyOfBatsUsesTheOnlyExitIfTHereIsOnlyOneExit() {
        Room startingRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(finishingRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        Hunter hunter = new Hunter();
        // Program randomizer: false for interaction order (bats respond to hunter),
        // then ints for RandomRoomFinder (0 iterations + exit selection)
        // reality is we generate lots of bool and lots of int, do we need a fancier programmable randomizer?
        // the fancy one exists
        Helpers.programRandomizer(false, false);

        hunter.moveTo(startingRoom);

        assertThat(hunter.getRoom(), is(equalTo(finishingRoom)));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aColonyOfBatsDropsTheHunterInAnUnOccupiedRoom() {
        Room startingRoom = new Room();
        Room occupiedRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(occupiedRoom);
        occupiedRoom.add(finishingRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        BottomlessPit pit = new BottomlessPit();
        pit.moveTo(occupiedRoom);
        Hunter hunter = new Hunter();
        // New algorithm: only finishingRoom is empty and not the starting room
        // So candidates = [finishingRoom], index 0 selects it
        Helpers.programRandomizer(
                new boolean[]{false, false},
                new int[]{0}
        );

        hunter.moveTo(startingRoom);
        Helpers.visualize(startingRoom);
        assertThat(hunter.getRoom(), is(equalTo(finishingRoom)));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aColonyOfBatsMovesToAnotherUnoccupiedRoom() {
        Room startingRoom = new Room();
        Room occupiedRoom = new Room();
        Room emptyRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(occupiedRoom);
        occupiedRoom.add(emptyRoom);
        emptyRoom.add(finishingRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        BottomlessPit pit = new BottomlessPit();
        pit.moveTo(occupiedRoom);
        Hunter hunter = new Hunter();
        // New algorithm: Two findRandomRoom calls occur:
        // 1. For hunter: candidates = [emptyRoom, finishingRoom], index 0 selects emptyRoom
        // 2. For bats: candidates = [finishingRoom] (emptyRoom now has hunter), index 0 selects it
        Helpers.programRandomizer(
                new boolean[]{false, false},
                new int[]{0, 0}
        );

        hunter.moveTo(startingRoom);

        Helpers.visualize(startingRoom);

        assertThat(bats.getRoom(), is(not(equalTo(hunter.getRoom()))));
        assertThat(bats.getRoom(), is(not(equalTo(pit.getRoom()))));
        assertThat(bats.getRoom(), is(not(equalTo(startingRoom))));
        assertThat(bats.getRoom(), is(equalTo(finishingRoom)));
    }


    // interesting problem, because there are only two rooms and the rules say the target must be empty for
    // both the hunter and the bats move and the new room cannot be the starting room the bats keep landing in the room
    // with the hunter and the whole thing starts again until we run out of stack space
    // how to fix?
    // this seems to be because we move the hunter to occupied room (no choice)
    // then we try to move the bats but they can only go to the occupied room, which trigges the bats. So the
    // other option is 'don't move the bats'
    @Test
    public void aColonyOfBatsMovesHunterToAnOccupiedRoomIfItHasNoOtherOptions() {
        Room startingRoom = new Room();
        Room occupiedRoom = new Room();
        ColonyOfBats bats = new ColonyOfBats();
        startingRoom.add(occupiedRoom);
        bats.moveTo(startingRoom);
        BottomlessPit pit = new BottomlessPit();
        pit.moveTo(occupiedRoom);
        Hunter hunter = new Hunter();

        hunter.moveTo(startingRoom);

        assertThat(hunter.getRoom(), is(equalTo(startingRoom)));
        assertThat(hunter.isAlive(), is(true));
    }
}
