package com.noradltd.wumpus;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.ByteArrayOutputStream;

import static com.noradltd.wumpus.Helpers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class GameSteps {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    Game game;

    @After
    public void afterScenario() {
        resetStdout();
    }

    @Given("^the program is executed$")
    public void the_program_is_executed() throws Throwable {
        stdout = captureStdout();
    }

    @When("^the game has initialized$")
    public void the_game_has_initialized() throws Throwable {
        Room.roomNumberer = new Room.RoomNumberer() {
            private int instanceCounter = 1;

            @Override
            public Integer nextRoomNumber() {
                return instanceCounter++;
            }
        };
        game = new Game();
    }

    @Then("^the first room is described as \"([^\"]*)\"$")
    public void the_first_room_is_described(String description) throws Throwable {
        description = reinterpolatEscapedCharacters(description) + '\n';
        assertThat(stdout.toString(), equalTo(description));
    }

    @Then("^the hunter is in the first room$")
    public void the_hunter_is_in_the_first_room() throws Throwable {
        assertThat(game.firstRoom().occupants().contains(game.hunter()), is(true));
    }

    @Then("the hunter can move through the first exit")
    public void the_hunter_can_move_through_the_first_exit() {
        game.hunter().moveTo(game.firstRoom().exits().get(0));
    }


    @Then("the first room is empty")
    public void the_first_room_is_empty() {
        assertThat(game.firstRoom().occupants(), is(empty()));
    }
}
