package com.noradltd.wumpus;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.noradltd.wumpus.ScenarioContext.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HunterSteps {
    @After
    public void afterScenario() {
        ScenarioContext.reset();
    }

    static final String DESTINATION = "destination";

    Room getDestination() {
        return (Room) getOther(DESTINATION);
    }

    Room setDestination(Room destination) {
        return (Room) setOther(DESTINATION, destination);
    }

    static final String TARGET = "target";

    Integer getTarget() {
        return (Integer) getOther(TARGET);
    }

    Integer setTarget(Integer target) {
        return (Integer) setOther(TARGET, target);
    }

    static final String TARGET_ROOM = "target-room";

    Room getTargetRoom() {
        return (Room) getOther(TARGET_ROOM);
    }

    Room setTargetRoom(Room targetRoom) {
        return (Room) setOther(TARGET_ROOM, targetRoom);
    }

    static final String ARROW = "arrow";

    Arrow getArrow() {
        return (Arrow) getOther(ARROW);
    }

    Arrow setArrow(Arrow arrow) {
        return (Arrow) setOther(ARROW, arrow);
    }

    static final String QUIVER = "quiver";

    Hunter.Quiver getQuiver() {
        return (Hunter.Quiver) getOther(QUIVER);
    }

    Hunter.Quiver setQuiver(Hunter.Quiver quiver) {
        return (Hunter.Quiver) setOther(QUIVER, quiver);
    }

    class TestQuiver implements Hunter.Quiver {

        int arrowCount = 1;

        @Override
        public boolean isEmpty() {
            return arrowCount < 1;
        }

        @Override
        public Arrow next() {
            setArrow(new Arrow());
            arrowCount--;
            return getArrow();
        }
    }

    @Given("a fresh maze")
    public void aFreshMaze() {
        setMaze(MazeBuilder.build());
    }

    @Then("the hunter starts in the entrance")
    public void theHunterStartsInTheEntrance() {
        setQuiver(new TestQuiver());
        setHunter(new Hunter(getQuiver()));
        getHunter().moveTo(getMaze().entrance());
    }

    @When("the hunter moves from the entrance")
    public void theHunterMovesFromTheEntrance() {
        final int randomExitIndex = Random.getRandomizer().nextInt(getHunter().getRoom().exits().size());
        final Room randomExit = getHunter().getRoom().exits().get(randomExitIndex);
        setDestination(randomExit);
        getHunter().moveTo(getDestination());
    }

    @Then("the entrance is empty")
    public void theEntranceIsEmpty() {
        assertThat(getMaze().entrance().occupants(), is(empty()));
    }

    @When("the hunter shoots an arrow")
    public void theHunterShootsAnArrow() {
        final int randomExitIndex = Random.getRandomizer().nextInt(getHunter().getRoom().exits().size());
        final Room randomExit = getHunter().getRoom().exits().get(randomExitIndex);
        setTargetRoom(randomExit);
        getHunter().shoot(randomExitIndex);
    }

    @When("the hunter shoots an arrow at the Wumpus")
    public void theHunterShootsAnArrowAtTheWumpus() {
        Room wumpusRoom = getWumpus().getRoom();
        setTargetRoom(wumpusRoom);
        Integer wumpusExitIdx = getHunter().getRoom().exits().indexOf(wumpusRoom);
        setTarget(wumpusExitIdx);
        getHunter().shoot(getTarget());
    }

    @Then("the arrow follows a tunnel")
    public void theArrowFollowsATunnel() {
        assertThat(getArrow().getRoom(), is(equalTo(getTargetRoom())));
    }

    @When("the hunter shoots all of there arrows")
    public void theHunterShootsAllOfThereArrows() {
        while (getHunter().canShoot()) getHunter().shoot(0);
    }

    @Then("the hunter can't shoot any more")
    public void theHunterCantShootAnyMore() {
        assertThat(getHunter().canShoot(), is(false));
    }

    @When("the hunter is in an adjacent room")
    public void theHunterIsInAnAdjacentRoom() {
        setQuiver(new TestQuiver());
        setHunter(new Hunter(getQuiver()));
        getHunter().moveTo(new Room());
        getWumpus().getRoom().add(getHunter().getRoom());
    }

    @Then("the Wumpus dies")
    public void theWumpusDies() {
        assertThat(getWumpus().isDead(), is(true));
    }
}
