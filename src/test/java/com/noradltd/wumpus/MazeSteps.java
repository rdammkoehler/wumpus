package com.noradltd.wumpus;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class MazeSteps {

    Room firstRoom;

    @Given("a new Maze")
    public void aNewMaze() {
        firstRoom = MazeBuilder.build().entrance();
    }

    @Then("there are {int} rooms")
    public void thereAreRooms(int roomCount) {
        assertThat(Helpers.countRooms(firstRoom), equalTo(roomCount));
    }

}
