package com.noradltd.wumpus;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class MazeSteps {

    Maze maze;

    @Given("a new Maze")
    public void aNewMaze() {
        maze = MazeBuilder.build();
    }

    @Then("there are {int} rooms")
    public void thereAreRooms(int roomCount) {
        assertThat(Helpers.countRooms(maze), equalTo(roomCount));
    }

    @Given("the smallest maze possible")
    public void theSmallestMazePossible() {
        maze = MazeBuilder.build("--rooms", "1");
    }
}
