package com.noradltd.wumpus;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// todo,need factory wrapper on the impl; these test are for the interface level
public class RoomNamerTest {

    private static final String[] NAMES = {"Test Room 1", "Test Room 2"};

    @Test
    public void aRoomNamerGeneratesARoomName() {
        String roomName = new ListBasedRoomNameGenerator().nextName();

        assertThat(roomName, is(notNullValue()));
    }

    @Test
    public void aRoomNamerAcceptsAListOfPossibleNames() {
        String roomName = new ListBasedRoomNameGenerator().using(NAMES).nextName();

        assertThat(roomName, in(Arrays.asList(NAMES)));
    }

    @Test
    public void aRoomNamerReturnsAllTheRoomNames() {
        RoomNameGenerator nameGenerator = new ListBasedRoomNameGenerator().using(NAMES);

        String[] roomNames = {
                nameGenerator.nextName(),
                nameGenerator.nextName()
        };

        assertThat(Arrays.asList(roomNames), containsInAnyOrder(NAMES[0], NAMES[1]));
    }

    @Test
    public void aRoomNamerReturnsRoomNamesInOrderWhenAsked() {
        RoomNameGenerator nameGenerator = new ListBasedRoomNameGenerator().sequentially().using(NAMES);

        String[] roomNames = {
                nameGenerator.nextName(),
                nameGenerator.nextName()
        };

        assertThat(roomNames, equalTo(NAMES));
    }

    @Test
    public void aRoomNamerReturnsRoomNamesRandomlyWhenAsked() {
        String[] possibleNames = createPossibleRoomNamesArray();
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
        String[] possibleNames = createPossibleRoomNamesArray();
        String[] originalPossibleNames = new String[possibleNames.length];
        System.arraycopy(possibleNames, 0, originalPossibleNames, 0, possibleNames.length);

        new ListBasedRoomNameGenerator().randomly().using(possibleNames);

        assertThat(originalPossibleNames, equalTo(possibleNames));
    }

    private String[] createPossibleRoomNamesArray() {
        String[] possibleNames = new String[26];
        char roomNameSuffix = 'a';
        for (int idx = 0; idx < possibleNames.length; idx++) {
            possibleNames[idx] = "Test Room " + roomNameSuffix++;
        }
        return possibleNames;
    }
}
