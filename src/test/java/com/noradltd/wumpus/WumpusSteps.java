package com.noradltd.wumpus;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WumpusSteps {
    Wumpus wumpus;
    Room room, exit;
    Hunter hunter;

    @Given("^a Wumpus in a room$")
    public void a_Wumpus_in_a_room() {
        room = new Room();
        exit = new Room();
        room.add(exit);
        wumpus = new Wumpus();
        wumpus.moveTo(room);
    }

    @When("^a Hunter enters the room$")
    public void a_Hunter_enters_the_room() {
        hunter = new Hunter();
        hunter.moveTo(room);
    }

    @Then("^the Wumpus eats the Hunter$")
    public void the_Wumpus_eats_the_Hunter() {
//        wumpus.eat(hunter);
    }

    @Then("^the Hunter is dead$")
    public void the_Hunter_is_dead() {
        assertThat(hunter.isDead(), is(true));
    }

    @Then("^the Wumpus is fed$")
    public void the_Wumpus_is_fed() {
//        assertThat(wumpus.isFed(), is(true));
    }

    @When("^the Wumpus flees$")
    public void the_Wumpus_flees() {
//        wumpus.flee();
    }

    @Then("^the Hunter lives$")
    public void the_Hunter_lives() {
        assertThat(hunter.isDead(), is(false));
    }

    @Then("^the Wumpus is hungry$")
    public void the_Wumpus_is_hungry() {
//        assertThat(wumpus.isFed(), is(false));
    }
}
