# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

This is a Maven-based Java 17 project.

```bash
# Build the project
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=HunterTest

# Run a single test method
mvn test -Dtest=HunterTest#aHunterCanMoveToAnAdjacentRoom

# Clean and rebuild
mvn clean compile

# Package (creates executable JAR)
mvn package
```

## Architecture Overview

This is a Java implementation of "Hunt the Wumpus," a classic text-based adventure game.

### Core Game Loop

`Main.java` handles CLI interaction and the game loop. `Game` orchestrates gameplay, managing the `Hunter`, `Maze`, and game state. Command-line options (--rooms, --arrows, --wumpi, etc.) configure game parameters via `Game.Options`.

### Maze Structure

- `Maze` interface exposes `entrance()` - the starting room
- `MazeBuilder` creates the room topology (interconnected graph of rooms)
- `MazeLoader` populates the maze with hazards (Wumpus, BottomlessPit, ColonyOfBats)
- `MazeTraverser` utility provides room collection/traversal methods used across the codebase

### Room and Occupant System

`Room` is the central class managing:
- Room topology via bidirectional exit connections
- Occupant collection (Hunter, Wumpus, Arrow, etc.)
- Interaction dispatch between occupants

`Room.Occupant` is the abstract base class for all entities in the maze:
- `Hunter` - the player character with a quiver of arrows
- `Wumpus` - the enemy that can eat the hunter or flee
- `Arrow` - projectile that can kill a Wumpus
- `BottomlessPit` - instant death hazard
- `ColonyOfBats` - transports the hunter to a random room

Each occupant defines interactions via a `HashMap<Class<? extends Occupant>, Interaction>` map.

### Randomization and Testing

`Random` is accessed via `Random.getRandomizer()` which uses ThreadLocal state stored in `Game.getThreadLocalBag()`. Tests control randomization using:
- `Helpers.programRandomizer(boolean...)` or `programRandomizer(int...)` for deterministic behavior
- `@ExtendWith(ResetRandomizerExtension.class)` JUnit extension to reset state between tests

### Visualization

`Visualizer` generates Graphviz maze diagrams using the `guru.nidi:graphviz-java` library. Note: Some visualization tests may error on ARM Mac due to missing J2V8 native binaries.

## Coding Guidelines

- **Always write unit tests for new code.** Every new class or significant functionality must have corresponding unit tests before committing.
