package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
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
        assertThat(new ColonyOfBats().toString(), is(equalTo("A horde of blackened leather, slick with the blood of their victims undulates across the ceiling")));
    }

    @Test
    public void aColonyOfBatsMovesAHunterToARandomRoomFarFarAway() {
        final int room_count = 13;
        final int starting_room_idx = 1;
        Room[] rooms = new Room[room_count];
        Helpers.restartRoomNumberer();
        for(int idx = 0; idx < rooms.length; idx++) {
            rooms[idx] = new Room();
            if (idx>0) {
                rooms[idx-1].add(rooms[idx]);
            }
        }
        Helpers.programRandomizer(10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        ColonyOfBats bats = new ColonyOfBats();
        bats.moveTo(rooms[starting_room_idx]);
        Hunter hunter = new Hunter();

        hunter.moveTo(rooms[starting_room_idx]);

        assertThat(hunter.getRoom(), is(equalTo(rooms[room_count-1])));
        Helpers.resetRandomizer();
    }

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
}
