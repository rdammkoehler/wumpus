package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// todo,need factory wrapper on the impl; these test are for the interface level
public class RoomNamerTest {

    @Test
    public void aRoomNamerGeneratesARoomName() {
        String roomName = new ListBasedRoomNameGenerator().nextName();

        assertThat(roomName, is(notNullValue()));
    }

    @Test
    public void aRoomNamerAcceptsAListOfPossibleNames() {
        String[] possibleNames = {"Test Room 1", "Test Room 2"};

        String roomName = new ListBasedRoomNameGenerator().using(possibleNames).nextName();

        assertThat(roomName, in(Arrays.asList(possibleNames)));
    }

    @Test
    public void aRoomNamerReturnsAllTheRoomNames() {
        String[] possibleNames = {"Test Room 1", "Test Room 2"};
        RoomNameGenerator nameGenerator = new ListBasedRoomNameGenerator().using(possibleNames);

        String[] roomNames = {
                nameGenerator.nextName(),
                nameGenerator.nextName()
        };

        assertThat(Arrays.asList(roomNames), containsInAnyOrder("Test Room 1", "Test Room 2"));
    }

    @Test
    public void aRoomNamerReturnsRoomNamesInOrderWhenAsked() {
        String[] possibleNames = {"Test Room 1", "Test Room 2"};
        RoomNameGenerator nameGenerator = new ListBasedRoomNameGenerator().sequentially().using(possibleNames);

        String[] roomNames = {
                nameGenerator.nextName(),
                nameGenerator.nextName()
        };

        assertThat(roomNames, equalTo(possibleNames));
    }

    @Test
    public void aRoomNamerReturnsRoomNamesRandomlyWhenAsked() {
        String[] possibleNames = new String[26];
        char varVar = 'a';
        for (int idx = 0; idx < possibleNames.length; idx++) {
            possibleNames[idx] = "Test Room " + varVar++;
        }
        RoomNameGenerator nameGenerator = new ListBasedRoomNameGenerator().randomly().using(possibleNames);

        String[] roomNames = new String[possibleNames.length];
        for (int idx = 0; idx < roomNames.length; idx++) {
            roomNames[idx] = nameGenerator.nextName();
        }

        assertThat(Arrays.asList(roomNames), containsInAnyOrder(possibleNames));
        assertThat(roomNames, not(equalTo(possibleNames)));
    }

    @Test
    public void aRoomNamerDoesNotChangeTheOrderOfTheGivenPossibleNamesWhenRandomized() {
        String[] possibleNames = new String[26];
        String[] originalPossibleNames = new String[possibleNames.length];
        char varVar = 'a';
        for (int idx = 0; idx < possibleNames.length; idx++) {
            possibleNames[idx] = "Test Room " + varVar++;
        }
        System.arraycopy(possibleNames, 0, originalPossibleNames, 0, possibleNames.length);

        new ListBasedRoomNameGenerator().randomly().using(possibleNames);

        assertThat(originalPossibleNames, equalTo(possibleNames));
    }
}
