# Wumpus

Inspired by Vivian Conrad's homework assignment.

Thanks to Gregory Yob for the years of entertainment.

Guided by Gregory Brown's [article](https://practicingruby.com/articles/wumpus)

## Playing

### Option 1: Using the Launch Script

Ensure that JRE 17+ is available, then run:

```bash
./bin/wump
```

With custom options:

```bash
./bin/wump --rooms 30 --wumpi 3 --arrows 10
```

### Option 2: Using Docker

Build and run with Docker (no Java installation required):

```bash
# Build the image
docker build -t wumpus:latest .

# Run the game interactively
docker run --rm -it wumpus:latest

# Run with custom options
docker run --rm -it wumpus:latest --rooms 30 --wumpi 3
```

### Option 3: Using the JAR Directly

Ensure that JRE 17+ is in your path:

```bash
export PATH=$JAVA_HOME/bin:$PATH
```

Build and run:

```bash
mvn package
java -jar target/wumpus-1.0-SNAPSHOT.jar
```

### Command Line Options

| Option | Description | Default |
|--------|-------------|---------|
| `--rooms #` | Number of rooms in the maze | 20 |
| `--wumpi #` | Number of wumpi | 1 |
| `--arrows #` | Number of arrows | 5 |
| `--pits #` | Number of bottomless pits | 0 |
| `--bats #` | Number of bat colonies | 0 |
| `--max_exits #` | Maximum exits per room | 3 |
| `--seed #` | Random seed for reproducibility | - |
| `--help` | Show help | - |

### Gameplay

Start the Game

```bash
./bin/wump
```

When prompted, type your command followed by 'Enter'

e.g.

```shell script
You are in room 1.
Exits go to: 2, 8, 5
---------------------------------
What do you want to do? (m)ove or (s)hoot? 
```

To Move type `m` followed by return

```shell script
What do you want to do? (m)ove or (s)hoot? m

```

You will be prompted for `Where?`

```shell script
Where?
```

Enter a destination (2, 8, 5)

```shell script
Where? 5

```

## The Game

Cave: By default 20 location in a flattened dodecahedron (classic).

* command line options: --rooms # to specify more or less rooms; 0 = random number of rooms

Room: At least one exit. Exits are labeled by the room they lead to.

* may contain a Wumpus!

* may contain a Bottomless Pit!

* may contain a Superbat

Wumpus: Will eat you! (you loose) or Run Away, game continues

* emits a horrid stench into adjacent rooms

* only moves one room at a time

Bottomless Pit: You die in a tragic fall

* emits a cool drafty breeze into adjacent rooms

Superbat: Moves you a random number of moves up to room-count minus one rooms away; the Superbat then moves itself a
random number of rooms.

* emits a rustling sound into adjacent rooms

Hunter: The player

* moves from room to room

* can shoot into adjacent rooms

* can fall in pits

* can be carried off by Superbats

* can be eaten by a Wumpus

* can sense hazards emitted from rooms

Rules:

* If a player shoots into a room that does _not_ contain the Wumpus, the Wumpus moves to another room

* Hunter has limited arrows

    * command line: --arrows #; 0 == unlimited, default 5

* (opt) Crooked Arrow: Allows an arrow to travel up-to five rooms

    * If the arrow fails to kill the Wumpus the arrow continues to a random room; if that room is the room where the
      Hunter stands, they die

    * (opt) keep track of lost arrows, tell the Hunter when they find them
  
