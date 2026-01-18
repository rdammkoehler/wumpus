# Change Log

All notable changes to the Hunt the Wumpus project are documented in this file.

## [1.0.2] - 2026-01-18

### Changed

#### Main.java Refactoring - Separation of Concerns

**Problem:** `Main.java` mixed multiple concerns - CLI I/O, game loop, command parsing, and help text generation (identified in TODO comment at lines 14-15).

**Solution:** Extracted three new classes to separate responsibilities.

**New Files Created:**

##### ConsoleUI.java
- **Purpose:** Handles all user I/O operations
- **Methods:**
  - `showWelcome()` - Display welcome message
  - `showPrompt()` - Display command prompt "i|l|m|s|t?"
  - `showHelp()` - Display help text
  - `showGoodbye()` - Display goodbye message
  - `showScore(String)` - Display game score
  - `showMessage(String)` - General message display
  - `readCommand()` - Read user input
  - `promptPlayAgain()` - Ask user to play again

##### CommandParser.java
- **Purpose:** Parses and routes user commands
- **Components:**
  - `Command` interface with `execute(String arg)` method
  - `ParsedCommand` record holding command and argument
  - Command map supporting: quit, move, shoot, inventory, help, look, take, viz
- **Methods:**
  - `parse(String input)` - Returns `ParsedCommand` from user input
  - `execute(ParsedCommand)` - Executes the parsed command

##### GameController.java
- **Purpose:** Controls the game loop
- **Methods:**
  - `play()` - Main game loop (welcome, create game, command loop, show score)

**Changes to Main.java:**
- Reduced from 124 lines to 21 lines
- Now only creates `ConsoleUI`, loops `GameController.play()` while user wants to play
- Removed TODO comment (issue resolved)

**Before:**
```java
public class Main {
    private static final Pattern USER_COMMAND = ...;
    private Game game;
    private final BufferedReader input;
    private interface Command { ... }
    private final Map<String, Command> COMMANDS = ...;
    // ... 120+ lines of mixed concerns
}
```

**After:**
```java
public class Main {
    public static void main(String[] args) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            ConsoleUI ui = new ConsoleUI(input);
            do {
                new GameController(ui, args).play();
            } while (ui.promptPlayAgain());
        } catch (IOException ioException) {
            Logger.error("System Failure", ioException);
        } finally {
            Logger.info("Goodbye");
        }
    }
}
```

---

## [1.0.1] - 2026-01-18

### Added

#### CLAUDE.md - Claude Code Guidance File
- **File:** `CLAUDE.md`
- **Purpose:** Provides guidance to Claude Code (claude.ai/code) for working in this repository
- **Contents:**
  - Build and test commands (Maven compile, test, single test execution)
  - Architecture overview covering core game loop, maze structure, room/occupant system
  - Randomization and testing patterns (ProgrammableRandom, ResetRandomizerExtension)
  - Visualization notes including ARM Mac compatibility caveat

---

## [1.0.0] - 2026-01-17

### Overview

This release includes structural improvements focused on code quality, maintainability, test reliability, and dependency updates. The changes address code duplication, semantic clarity issues, non-deterministic test failures, and outdated dependencies.

---

### Added

#### MazeTraverser Utility Class
- **File:** `src/main/java/com/noradltd/wumpus/MazeTraverser.java`
- **Purpose:** Consolidates duplicate room traversal logic that was previously copied across 4 different locations in the codebase
- **Methods:**
  - `collectAllRooms(Room startingRoom)` - Returns a `Set<Room>` containing all rooms reachable from the starting room
  - `collectAllRoomsAsList(Room startingRoom)` - Returns a `List<Room>` for ordered access to all reachable rooms
- **Benefits:**
  - Single source of truth for maze traversal algorithm
  - Easier to maintain and test
  - Reduced code duplication by ~66 lines across the codebase

#### Git Configuration
- **File:** `.gitignore`
- **Excludes:**
  - Build output (`target/`)
  - IDE files (`.idea/`, `*.iml`)
  - Generated maze visualizations (`*_maze.png`, `maze.png`, `test.png`)
  - Log files (`*.log`)
  - Claude Code configuration (`.claude/`)

---

### Changed

#### Dependency Updates (pom.xml)

| Dependency | Old Version | New Version | Reason |
|------------|-------------|-------------|--------|
| maven-compiler-plugin | 3.0 | 3.12.1 | Outdated, missing features |
| maven-surefire-plugin | 2.16 | 3.2.5 | Maven 2-era version, needed update |
| junit-jupiter-api | 5.7.0-RC1 | 5.10.2 | Release candidate → stable release |
| junit-jupiter-params | 5.7.0 | 5.10.2 | Version alignment with junit-jupiter-api |

#### Arrow.java - Semantic State Separation

**Problem:** The `Arrow` class was using `isDead()` (inherited from `Occupant`) to represent a "broken" arrow state. This was semantically confusing because in the game context, "dead" refers to living things (hunters, wumpi) dying, not objects breaking.

**Solution:** Added a separate `broken` state field with proper semantics.

**Changes:**
- Added private `boolean broken = false` field
- Modified `isBroken()` to return the new `broken` field instead of delegating to `isDead()`
- Overrode `die()` method to set `broken = true` before calling `super.die()` (maintains backward compatibility)
- Updated `toString()` to check `isBroken()` instead of `isDead()`
- Removed redundant `broken = true` assignment in `shatter()` method (since `die()` already sets it)
- Removed TODO comment about semantic clarity (issue resolved)

**Before:**
```java
public boolean isBroken() {
    return isDead();  // Confusing: arrows don't "die"
}
```

**After:**
```java
private boolean broken = false;

public boolean isBroken() {
    return broken;  // Clear: arrows "break"
}

@Override
void die() {
    broken = true;
    super.die();
}
```

#### Game.java - Simplified Score Calculation

**Problem:** The `getScore()` method contained a 30-line inner class `MazeOccupantCounter` with its own room traversal implementation, duplicating logic found elsewhere.

**Solution:** Replaced with `MazeTraverser` usage and extracted a helper method.

**Changes:**
- Removed inner class `MazeOccupantCounter` (30 lines)
- Now uses `MazeTraverser.collectAllRoomsAsList(hunter.getRoom())`
- Extracted `countDeadOccupants(List<Room>, Class<? extends Room.Occupant>)` helper method
- Removed TODO comment about duplicate traversal logic (issue resolved)

**Before:**
```java
public String getScore() {
    class MazeOccupantCounter {
        private Set<Room> rooms = null;
        // ... 25+ lines of traversal and counting logic
    }
    MazeOccupantCounter counter = new MazeOccupantCounter();
    // ...
}
```

**After:**
```java
public String getScore() {
    List<Room> rooms = MazeTraverser.collectAllRoomsAsList(hunter.getRoom());
    Long huntersKilled = countDeadOccupants(rooms, Hunter.class);
    Long wumpiKilled = countDeadOccupants(rooms, Wumpus.class);
    return "Score: Hunter " + wumpiKilled + " Wumpus " + huntersKilled;
}

private Long countDeadOccupants(List<Room> rooms, Class<? extends Room.Occupant> occupantType) {
    return rooms.stream()
            .map(Room::occupants)
            .flatMap(Collection::stream)
            .filter(occupantType::isInstance)
            .filter(Room.Occupant::isDead)
            .count();
}
```

#### Visualizer.java - Consolidated Room Traversal

**Problem:** Contained its own 11-line `collectRoom()` method duplicating traversal logic.

**Solution:** Delegate to `MazeTraverser`.

**Changes:**
- Removed `collectRoom(Room, Set<Room>)` method (11 lines)
- Modified `getAllRooms(Maze)` to use `MazeTraverser.collectAllRoomsAsList(maze.entrance())`

#### MazeBuilder.java (MazeLoader class) - Consolidated Room Traversal

**Problem:** The `MazeLoader` class contained a 15-line inner class `RoomCollector` with duplicate traversal logic.

**Solution:** Delegate to `MazeTraverser`.

**Changes:**
- Removed inner class `RoomCollector` (15 lines)
- Modified `collectAllRooms()` to use `MazeTraverser.collectAllRoomsAsList(maze.entrance())`

#### Helpers.java (Test Utility) - Consolidated Room Traversal

**Problem:** Test helper contained its own 10-line `collectRoom()` method.

**Solution:** Delegate to `MazeTraverser`.

**Changes:**
- Removed `collectRoom(Room, Set<Room>)` method (10 lines)
- Modified `countRooms(Maze)` to use `MazeTraverser.collectAllRooms(maze.entrance()).size()`
- Modified `getAllRooms(Maze)` to use `MazeTraverser.collectAllRoomsAsList(maze.entrance())`

---

### Fixed

#### Non-Deterministic Test Failures

Two tests were failing intermittently due to uncontrolled randomization:

##### ColonyOfBatsTest.aColonyOfBatsUsesTheOnlyExitIfTHereIsOnlyOneExit

**Root Cause:** The test depended on the interaction order between the hunter and bats, which is determined by `Random.getRandomizer().nextBoolean()` in `Room.executeOccupantInteractions()`. The bats' interaction with the hunter only triggers correctly when the boolean returns `false`.

**Fix:**
- Added `@ExtendWith(ResetRandomizerExtension.class)` annotation
- Added `Helpers.programRandomizer(false, false)` to control the random values

##### RandomRoomFinderTest.aRoomWithAnExitTreeTwoDeepReturnsFirstBecauseEmtpy

**Root Cause:** The `findRandomRoom()` method uses `nextInt()` to randomly select rooms, causing non-deterministic results.

**Fix:**
- Added `@ExtendWith(ResetRandomizerExtension.class)` annotation
- Added `Helpers.programRandomizer(0, 0)` to ensure deterministic room selection

##### RandomRoomFinderTest.aRoomWithAnExitTreeTwoDeepReturnsSecondBecauseNotEmtpy

**Root Cause:** Same as above - random room selection in `findRandomRoom()`.

**Fix:**
- Added `@ExtendWith(ResetRandomizerExtension.class)` annotation
- Added `Helpers.programRandomizer(0, 0, 0, 1)` to navigate to the correct room deterministically

---

### Technical Debt - Remaining TODOs

The following structural improvements are documented as TODO comments for future consideration:

#### Room.java (Line 6-10)
**Issue:** Single Responsibility Principle violation - the class handles room topology, occupant management, and interaction dispatch.
**Suggestion:** Split into `Room` (topology), `OccupantManager`, and `InteractionResolver`.

#### Room.java (Line 105-106)
**Issue:** Non-deterministic interaction ordering due to `Random.getRandomizer().nextBoolean()`.
**Suggestion:** Define clear interaction precedence with an `InteractionHandler` interface.

#### Game.java (Line 93-94)
**Issue:** ThreadLocal state management creates hidden dependencies and makes testing harder.
**Suggestion:** Use explicit dependency injection - pass `Random` instance through constructors.

#### Game.java (Line 172-173)
**Issue:** Reflection-based configuration in `Options.setOptionValue()` bypasses type safety and is hard to debug.
**Suggestion:** Use explicit setter methods or a CLI library (Picocli, JCommander).

#### ~~Main.java (Line 14-15)~~ ✅ RESOLVED in v1.0.2
**Issue:** Mixed concerns - CLI I/O, game loop, command parsing, and help text generation.
**Resolution:** Extracted `CommandParser`, `GameController`, and `ConsoleUI` classes.

---

### Test Results

| Metric | Count |
|--------|-------|
| Total Tests | 156 |
| Passed | 143 |
| Failed | 0 |
| Errors | 6 (Graphviz/J2V8 native library - environment specific) |
| Skipped | 7 |

**Note:** The 6 errors are pre-existing issues related to the Graphviz visualization library requiring J2V8 native binaries that are not available for ARM Mac architecture. These are environment-specific and do not indicate code problems.

---

### Migration Notes

No breaking changes. All existing functionality is preserved. The refactoring changes are internal and do not affect the public API.

### Contributors

- Claude Opus 4.5 (AI Assistant)
