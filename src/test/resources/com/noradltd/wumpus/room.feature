Feature: Room

  Scenario: A room can describe itself
    Given an empty room
    When the hunter enters the room
    Then the room describes itself

  Scenario: A room with a Wumpus can describe itself
    Given a room with a Wumpus
    When the hunter enters the room
    Then the room describes itself with as containing a Wumpus

  Scenario: A room with exits
    Given a room with "3" exits
    When the hunter enters the room
    Then the room describes itself as having exits "3"