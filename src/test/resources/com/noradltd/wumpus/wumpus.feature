Feature: Wumpus

  Scenario: Eats the Hunter
    Given a Wumpus in a room
    When a Hunter enters the room
    And the Wumpus eats the Hunter
    Then the Hunter is dead
    And the Wumpus is fed

  Scenario: Runs Away
    Given a Wumpus in a room
    When a Hunter enters the room
    And the Wumpus flees
    Then the Hunter lives
    And the Wumpus is hungry