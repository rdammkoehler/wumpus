package com.noradltd.wumpus;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class GameSteps {
    ByteArrayOutputStream stdout;
    Game game;

    @After
    public void afterScenario() {
        resetStdout();
    }

    @Given("^the program is executed$")
    public void the_program_is_executed() {
        stdout = captureStdout();
    }

    @When("^the game has initialized$")
    public void the_game_has_initialized() {
        restartRoomNumberer();
        game = new Game(new String[]{});
    }

    @Then("^the first room is described$")
    public void the_first_room_is_described() {
        // TODO fixme
        //assertThat(stdout.toString(), matchesPattern("^Room #\\d+\\nHas \\d+ exits.\\n"));
    }

    @Then("^the hunter is in the first room$")
    public void the_hunter_is_in_the_first_room() {
        // TODO fixme
//        assertThat(game.maze().entrance().occupants().contains(game.hunter()), is(true));
    }

    @Then("the hunter can move through the first exit")
    public void the_hunter_can_move_through_the_first_exit() {
        // TODO fixme
//        game.hunter().moveTo(game.maze().entrance().exits().get(0));
    }


    @Then("the first room is empty")
    public void the_first_room_is_empty() {
        // TODO fixme
//        assertThat(game.maze().entrance().occupants(), is(empty()));
    }
}
