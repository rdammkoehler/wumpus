# Change Log

All notable changes to the Hunt the Wumpus project are documented in this file.

## [1.0.10] - 2026-01-18

### Added

#### Load Game State Feature

**Feature:** Added ability to load saved game state.

**Command Line Option:**
- `--load` - Start game from saved state instead of new game

**In-Game Commands:**
- `f`, `forget`, or `load` - Load saved game state during gameplay

**Implementation:**
- `Game.loadState()` - Static method to deserialize game from `game_state` file
- `Game.hasSavedGame()` - Check if save file exists
- `Game.requestLoad()` - Request load during gameplay
- `Game.getPendingLoad()` - Get pending loaded game for controller swap
- `GameController` checks for `--load` option and pending loads

**Usage:**
```bash
# Start from saved game
./bin/wump --load

# Or in-game:
i|l|m|s|t? f
Game state loaded from game_state
```

---

## [1.0.9] - 2026-01-18

### Added

#### Save Game State Feature

**Feature:** Added ability to save game state during gameplay.

**Commands:**
- `r` or `remember` - Save current game state to file

**Implementation:**
- Game state saved to file named `game_state` using Java serialization
- All game classes now implement `Serializable`
- Interactions (lambda expressions) handled via transient fields with `initInteractions()` pattern

**Classes Modified:**
| Class | Change |
|-------|--------|
| `Game` | Added `Serializable`, `saveState()` method |
| `Room` | Added `Serializable` |
| `Room.Occupant` | Added `Serializable`, transient `interactions`, `initInteractions()` |
| `Hunter` | Added `serialVersionUID`, `initInteractions()` override |
| `Wumpus` | Added `serialVersionUID`, `initInteractions()` override |
| `Arrow` | Added `serialVersionUID`, `initInteractions()` override |
| `ArrowQuiver` | Added `Serializable` |
| `BottomlessPit` | Added `serialVersionUID`, `initInteractions()` override |
| `ColonyOfBats` | Added `serialVersionUID`, `initInteractions()` override |
| `OccupantManager` | Added `Serializable` |
| `InteractionResolver` | Added `Serializable` |
| `Maze` | Extended `Serializable` |
| `MazeBuilder.MazeStruct` | Added `serialVersionUID` |
| `CommandParser` | Added `r`/`remember` command |

**Usage:**
```
i|l|m|s|t? r
Game state saved to game_state
```

---

## [1.0.8] - 2026-01-18

### Added

#### Docker Support

**Feature:** Added Dockerfile for containerized gameplay with visualization support.

**File:** `Dockerfile`

**Image Details:**
- **Base:** Alpine 3.19 (minimal footprint)
- **Java:** OpenJDK 17 JRE
- **Visualization:** Graphviz + DejaVu fonts
- **Size:** ~354MB
- **Multi-stage build:** Separates build (JDK + Maven) from runtime (JRE only)

**Usage:**
```bash
# Build the image
docker build -t wumpus:latest .

# Run the game interactively
docker run --rm -it wumpus:latest

# Run with custom options
docker run --rm -it wumpus:latest --rooms 30 --wumpi 3

# Show help
docker run --rm wumpus:latest --help
```

**Dockerfile Features:**
- Multi-stage build for minimal runtime image
- Dependency caching via `mvn dependency:go-offline`
- Graphviz included for maze visualization command
- DejaVu fonts for proper text rendering in visualizations

---

## [1.0.7] - 2026-01-18

### Added

#### Game Launcher Script

**Feature:** Added convenience script to launch the game.

**File:** `bin/wump`

**Features:**
- Starts Hunt the Wumpus game
- Auto-detects Java installation (Homebrew, JAVA_HOME, PATH)
- Builds JAR automatically if not present
- Passes all command line arguments to the game

**Usage:**
```bash
./bin/wump                              # Start with defaults
./bin/wump --help                       # Show options
./bin/wump --rooms 30 --wumpi 3         # Custom game settings
```

**Available Options:**
| Option | Description |
|--------|-------------|
| `--arrows #` | Limit the number of arrows |
| `--bats #` | Limit the number of colonies of bats |
| `--pits #` | Limit the number of bottomless pits |
| `--rooms #` | Limit the number of rooms |
| `--seed #` | Set the Randomizer seed |
| `--wumpi #` | Limit the number of wumpi |
| `--max_exits #` | Limit the number of room exits |
| `--help` | Show help |

---

## [1.0.6] - 2026-01-18

### Added

#### UML Static Class Diagram

**Feature:** Added comprehensive UML class diagram for all main source classes.

**Files:**
| File | Description |
|------|-------------|
| `docs/static_class_diagram.png` | PNG diagram (2618x2295 pixels) |
| `docs/static_class_diagram.puml` | PlantUML source for updates |
| `bin/update-class-diagram.sh` | Script to regenerate PNG |

**Diagram Contents:**
- **Inheritance hierarchy:** `Room.Occupant` → `Hunter`, `Wumpus`, `Arrow`, `BottomlessPit`, `ColonyOfBats`
- **Interfaces:** `Maze`, `Quiver`, `Command`, `Interaction`, `RoomNumberer`
- **Inner classes:** `Game.Options`, `Room.Occupant`, `CommandParser.Command`, etc.
- **Composition:** `Room` contains `OccupantManager` and `InteractionResolver`
- **Game flow:** `Main` → `GameController` → `Game` / `CommandParser` → `ConsoleUI`
- **Maze construction:** `MazeBuilder` → `MazeLoader`

**Update Script Features:**
- Auto-detects Java installation (Homebrew, JAVA_HOME, PATH)
- Downloads PlantUML JAR if not cached
- Generates PNG from PlantUML source
- Requires Graphviz (`brew install graphviz`)

**Usage:**
```bash
./bin/update-class-diagram.sh
```

---

## [1.0.5] - 2026-01-18

### Added

#### Executable JAR with Bundled Dependencies

**Feature:** Configure Maven to produce a fully executable fat JAR.

**Changes to pom.xml:**

##### maven-jar-plugin (v3.3.0)
- Generates manifest with `Main-Class: com.noradltd.wumpus.Main`
- Adds default implementation entries (version, title, vendor)

##### maven-shade-plugin (v3.5.1)
- Creates uber-JAR with all runtime dependencies bundled
- `ServicesResourceTransformer` merges META-INF/services files (required for logging)
- Filters exclude signature files (*.SF, *.DSA, *.RSA) to avoid security conflicts

**Generated Manifest:**
```
Manifest-Version: 1.0
Implementation-Title: wumpus
Implementation-Version: 1.0-SNAPSHOT
Implementation-Vendor: NOrad Ltd.
Main-Class: com.noradltd.wumpus.Main
```

**Output JARs:**
| File | Size | Description |
|------|------|-------------|
| `target/wumpus-1.0-SNAPSHOT.jar` | 8MB | Shaded JAR with all dependencies |
| `target/original-wumpus-1.0-SNAPSHOT.jar` | 55K | Original JAR without dependencies |

**Usage:**
```bash
mvn package
java -jar target/wumpus-1.0-SNAPSHOT.jar
```

---

## [1.0.4] - 2026-01-18

### Changed

#### Game.Options - Replace Reflection with Explicit Setters

**Problem:** The `Options` inner class in `Game.java` used reflection-based configuration that bypassed type safety, was hard to debug, and would break with refactoring (identified in TODO comment at lines 151-152).

**Solution:** Replaced reflection with a `Map<String, Consumer<String>>` of explicit setter lambdas.

**Before:**
```java
private static final Map<String, String> optionNameAttrMap = new HashMap<>() {{
    put("--arrows", "initialArrowCount");
    put("--bats", "batCount");
    // ... field name strings
}};

private void setOptionValue(String attrName, String optionValue) {
    try {
        Field field = getClass().getDeclaredField(attrName);
        Method valueOf = field.getType().getMethod("valueOf", String.class);
        field.set(this, valueOf.invoke(null, optionValue));
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
             NoSuchFieldException | NullPointerException ex) {
        Logger.debug("cli: unknown argument " + attrName + " " + ex.getMessage(), ex);
    }
}
```

**After:**
```java
private final Map<String, Consumer<String>> optionSetters = Map.of(
    "--arrows", value -> initialArrowCount = Integer.valueOf(value),
    "--bats", value -> batCount = Integer.valueOf(value),
    "--pits", value -> pitCount = Integer.valueOf(value),
    "--rooms", value -> roomCount = Integer.valueOf(value),
    "--seed", value -> randomSeed = Long.valueOf(value),
    "--wumpi", value -> wumpiCount = Integer.valueOf(value),
    "--max_exits", value -> maxExitCount = Integer.valueOf(value)
);

private void setOptionValue(String optionName, String optionValue) {
    Consumer<String> setter = optionSetters.get(optionName);
    if (setter != null) {
        setter.accept(optionValue);
    } else {
        Logger.debug("cli: unknown argument " + optionName);
    }
}
```

**Benefits:**
- Type-safe: compiler catches type mismatches
- Refactoring-safe: IDE renames update the lambdas automatically
- Debuggable: no reflection stack traces, clear control flow
- Removed reflection imports (`java.lang.reflect.Field`, `Method`, `InvocationTargetException`)
- Removed TODO comment (issue resolved)

---

## [1.0.3] - 2026-01-18

### Changed

#### Room.java Refactoring - Separation of Concerns

**Problem:** `Room.java` violated Single Responsibility Principle - it handled room topology, occupant collection management, and interaction dispatch logic (identified in TODO comment at lines 6-10).

**Solution:** Extracted two new classes to separate responsibilities while keeping the intentionally non-deterministic interaction model.

**New Files Created:**

##### OccupantManager.java
- **Purpose:** Manages the collection of occupants within a room
- **Methods:**
  - `getOccupantsSnapshot()` - Returns a mutable copy of the occupant list (for iteration during interactions)
  - `getOccupants()` - Returns an unmodifiable set of occupants
  - `isEmpty()` - Check if room has no occupants
  - `addOccupant(Occupant)` - Add an occupant to the collection
  - `removeOccupant(Occupant)` - Remove an occupant from the collection
  - `containsSameTypeOfOccupant(Occupant)` - Check if room contains same type of occupant

##### InteractionResolver.java
- **Purpose:** Handles interaction dispatch logic between occupants entering a room and existing occupants
- **Key Features:**
  - Preserves non-deterministic interaction ordering (random boolean determines who acts first)
  - Manages bidirectional interactions (victim responds to instigator, then instigator responds to victim if still in same room)
  - Only adds interloper to room if still alive and present after interactions
- **Methods:**
  - `resolveInteractions(Occupant)` - Main entry point for interaction resolution

**Changes to Room.java:**
- Reduced from 231 lines to 192 lines
- Now delegates occupant management to `OccupantManager`
- Now delegates interaction resolution to `InteractionResolver`
- Focuses on topology: room numbering, exit connections, room identity
- Removed TODO comment about SRP violation (issue resolved)
- Kept TODO comment about non-deterministic interaction ordering (intentional design)

**Design Notes:**
- The `Occupant` inner class remains in `Room` as it is conceptually tied to the Room abstraction
- The `RoomDescriber` record remains in `Room` as it needs access to room internals for description generation
- Non-deterministic interaction ordering is preserved per user requirement

---

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

#### ~~Room.java (Line 6-10)~~ ✅ RESOLVED in v1.0.3
**Issue:** Single Responsibility Principle violation - the class handles room topology, occupant management, and interaction dispatch.
**Resolution:** Split into `Room` (topology), `OccupantManager`, and `InteractionResolver`.

#### Room.java (Line 105-106) - INTENTIONAL DESIGN
**Note:** Non-deterministic interaction ordering due to `Random.getRandomizer().nextBoolean()`.
**Status:** This is intentional game design, not technical debt. The randomness adds unpredictability to encounters.

#### Game.java (Line 93-94)
**Issue:** ThreadLocal state management creates hidden dependencies and makes testing harder.
**Suggestion:** Use explicit dependency injection - pass `Random` instance through constructors.

#### ~~Game.java (Line 172-173)~~ ✅ RESOLVED in v1.0.4
**Issue:** Reflection-based configuration in `Options.setOptionValue()` bypasses type safety and is hard to debug.
**Resolution:** Replaced with `Map<String, Consumer<String>>` of explicit setter lambdas.

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
