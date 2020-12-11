package com.noradltd.wumpus;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.noradltd.wumpus.Helpers.reInterpolateEscapedCharacters;
import static com.noradltd.wumpus.ScenarioContext.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RoomSteps {

    private String describeRoom() {
        return new Game.RoomDescriber(getRoom()).description();
    }

    @Given("^an empty room$")
    public void anEmptyRoom() {
        setRoom(new Room());
    }

    @When("^the hunter enters the room$")
    public void theHunterEntersTheRoom() {
        setHunter(new Hunter());
        getHunter().moveTo(getRoom());
    }

    @Then("^the room describes itself$")
    public void theRoomDescribesItself() {
        assertThat(describeRoom(), equalTo("Has exits \nContains a Hunter"));
    }

    @Given("^a room with a Wumpus$")
    public void aRoomWithAWumpus() {
        setRoom(new Room());
        setWumpus(new Wumpus());
        getWumpus().moveTo(getRoom());
    }

    @Then("^the room describes itself with \"([^\"]*)\"$")
    public void theRoomDescribesItselfWithA(String description) {
        assertThat(describeRoom(), equalTo(reInterpolateEscapedCharacters(description)));
    }

    @Given("^a room with \"([^\"]*)\" exits$")
    public void aRoomWithExits(String exitCountString) {
        int exitCount = Integer.parseInt(exitCountString);
        Room.RoomNumberer defaultRoomNumberer = Room.roomNumberer;
        Room.roomNumberer = new Room.RoomNumberer() {
            private int idx = 0;
            private final int[] values;

            {
                values = new int[exitCount + 1];
                for (int value = 0; value < exitCount + 1; value++) {
                    values[value] = value;
                }
            }

            @Override
            public Integer nextRoomNumber() {
                return values[idx++];
            }
        };
        Room room = new Room();
        setRoom(room);
        for (int count = 0; count < exitCount; count++) {
            room.add(new Room());
        }
        Room.roomNumberer = defaultRoomNumberer;
    }

    @Then("^the room describes itself as having exits \"([^\"]*)\"$")
    public void theRoomDescribesItselfAsHavingExits(String exitList) {
        assertThat(describeRoom(), equalTo("Has exits " + exitList + "\nContains a Hunter"));
    }
}
