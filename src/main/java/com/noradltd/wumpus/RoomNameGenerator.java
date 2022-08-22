package com.noradltd.wumpus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class RoomNameGenerator {
    private String[] possibleNames = {"Room Name"};
    private int currentIdx = 0;
    private boolean sequentially = true;
    private boolean haveShuffled = false;

    public RoomNameGenerator using(String[] possibilities) {
        possibleNames = new String[possibilities.length];
        System.arraycopy(possibilities, 0, possibleNames, 0, possibilities.length);
        return this;
    }

    public RoomNameGenerator sequentially() {
        this.sequentially = true;
        return this;
    }

    public RoomNameGenerator randomly() {
        this.sequentially = false;
        return this;
    }

    public String nextName() {
        if (!sequentially && !haveShuffled) {
            List<String> listOfPossibleNames = new ArrayList<>(Arrays.stream(possibleNames).toList());
            Collections.shuffle(listOfPossibleNames);
            possibleNames = listOfPossibleNames.toArray(possibleNames);
            haveShuffled = true;
        }
        String name = possibleNames[currentIdx];
        incrementCurrentIndex();
        return name;
    }

    private void incrementCurrentIndex() {
        currentIdx += 1;
        if (currentIdx > (possibleNames.length - 1)) {
            currentIdx = 0;
        }
    }
}
