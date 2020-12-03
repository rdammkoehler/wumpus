package com.noradltd.wumpus;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class MazeSteps {

    Room firstRoom;

    @Given("a new Maze")
    public void aNewMaze() {
        firstRoom = MazeBuilder.build().firstRoom();
    }

    @Then("there are {int} rooms")
    public void thereAreRooms(int roomCount) {
        assertThat(countRooms(firstRoom), equalTo(roomCount));
    }

    static Integer countRooms(Room room) {
        return collectRoom(room, new HashSet<Room>()).size();
    }

    private static Set<Room> collectRoom(Room room, Set<Room> rooms) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            for (Room exit : room.exits()) {
                if (!rooms.contains(exit)) {
                    collectRoom(exit, rooms);
                }
            }
        }
        return rooms;
    }
}
