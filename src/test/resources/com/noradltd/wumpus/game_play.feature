Feature: Game Play

  Scenario: Game Start
    Given the program is executed
    When the game has initialized
    Then the first room is described as "Has exits 10\nContains a Hunter"

  Scenario: Hunter is in the First Room
    Given the program is executed
    When the game has initialized
    Then the hunter is in the first room

  Scenario: First Move
    Given the program is executed
    When the game has initialized
    Then the hunter can move through the first exit
    And the first room is empty

