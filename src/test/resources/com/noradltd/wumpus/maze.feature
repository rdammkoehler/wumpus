Feature: Maze

  Scenario: Initial Maze
    Given a new Maze
    Then there are 20 rooms

  Scenario: Smallest Maze
    Given the smallest maze possible
    Then there are 2 rooms