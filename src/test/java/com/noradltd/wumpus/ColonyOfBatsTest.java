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
        final int room_count = 13;
        final int a_room_with_more_than_one_exit = 1;
        Room[] rooms = new Room[room_count];
        Helpers.restartRoomNumberer();
        for (int idx = 0; idx < rooms.length; idx++) {
            rooms[idx] = new Room();
            if (idx > 0) {
                rooms[idx - 1].add(rooms[idx]);
            }
        }
        final int batsFirst = 1;
        final int roomsToMove = 10;
        final int exitToRoom3 = 0;
        final int roomsToMoveBatsRelativeToHunter = 1;
        final int exitNumber = 0;
        Helpers.programRandomizer(batsFirst,
                roomsToMove,
                exitToRoom3,
                exitNumber,
                exitNumber,
                exitNumber,
                exitNumber,
                exitNumber,
                exitNumber,
                exitNumber,
                exitNumber,
                exitNumber,
                roomsToMoveBatsRelativeToHunter,
                exitNumber);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(rooms[a_room_with_more_than_one_exit]);
        Hunter hunter = new Hunter();

        Logger.debug(rooms[a_room_with_more_than_one_exit].number().toString());
        hunter.moveTo(rooms[a_room_with_more_than_one_exit]);

        assertThat(hunter.getRoom(), is(equalTo(rooms[11])));
        assertThat(bats.getRoom(), is(equalTo(rooms[12])));
    }

    //    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void aColonyOfBatsUsesTheOnlyExitIfTHereIsOnlyOneExit() {
        Room startingRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(finishingRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        Hunter hunter = new Hunter();

        hunter.moveTo(startingRoom);

        assertThat(hunter.getRoom(), is(equalTo(finishingRoom)));
    }

    @ExtendWith(ResetRandomizerExtension.class)
    @Test
    public void batsMoveToAnotherRoomAfterDroppingHunter() {
        Helpers.programRandomizer(1, 1, 1);
        Room startingRoom = new Room();
        Room finishingRoom = new Room();
        startingRoom.add(finishingRoom);
        Room additionalRoom = new Room();
        finishingRoom.add(additionalRoom);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(startingRoom);
        Hunter hunter = new Hunter();

        hunter.moveTo(startingRoom);

        assertThat(hunter.getRoom(), is(equalTo(finishingRoom)));
        assertThat(bats.getRoom(), is(equalTo(additionalRoom)));
    }
}
