Feature: Hunter

  Scenario: Starts in the entrance of the maze
    Given a fresh maze
    Then the hunter starts in the entrance

  Scenario: Can Move Room to Room
    Given a fresh maze
    And the hunter starts in the entrance
    When the hunter moves from the entrance
    Then the entrance is empty

  Scenario: Shoots Arrows
    Given a fresh maze
    And the hunter starts in the entrance
    When the hunter shoots an arrow
    Then the arrow follows a tunnel

  Scenario: Has Limited Arrows
    Given a fresh maze
    And the hunter starts in the entrance
    When the hunter shoots all of there arrows
    Then the hunter can't shoot any more

  Scenario: Kills Wumpi
    Given a room with a Wumpus
    When the hunter is in an adjacent room
    And the hunter shoots an arrow at the Wumpus
    Then the Wumpus dies

  Scenario: Scares Wumpi
    Given a room with a Wumpus
    When the hunter enters the room
    Then the Wumpus flees

  Scenario: Gets Eaten By a Wumpus
    Given a room with a Wumpus
    When the hunter enters the room
    Then the Wumpus eats the Hunter