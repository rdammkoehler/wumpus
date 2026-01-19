package com.noradltd.wumpus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
class GameState {
    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonProperty
    List<RoomState> rooms;
    @JsonProperty
    List<OccupantState> occupants;
    @JsonProperty
    HunterState hunter;
    @JsonProperty
    ScoreState score;

    GameState() {}

    static GameState fromGame(Game game, Hunter hunter, Room startRoom) {
        GameState state = new GameState();
        List<Room> allRooms = MazeTraverser.collectAllRoomsAsList(startRoom);

        state.rooms = allRooms.stream()
                .map(RoomState::fromRoom)
                .toList();

        state.occupants = allRooms.stream()
                .flatMap(room -> room.occupants().stream())
                .filter(occ -> !(occ instanceof Hunter))
                .map(OccupantState::fromOccupant)
                .toList();

        state.hunter = HunterState.fromHunter(hunter);
        state.score = ScoreState.fromGame(allRooms);

        return state;
    }

    void saveToFile(String filename) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), this);
    }

    static GameState loadFromFile(String filename) throws IOException {
        return mapper.readValue(new File(filename), GameState.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class RoomState {
        @JsonProperty int number;
        @JsonProperty List<Integer> exits;

        RoomState() {}

        static RoomState fromRoom(Room room) {
            RoomState state = new RoomState();
            state.number = room.number();
            state.exits = room.exits().stream().map(Room::number).toList();
            return state;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OccupantState {
        @JsonProperty String type;
        @JsonProperty int roomNumber;
        @JsonProperty boolean dead;
        @JsonProperty Boolean broken;
        @JsonProperty Boolean killedWumpus;

        OccupantState() {}

        static OccupantState fromOccupant(Room.Occupant occupant) {
            OccupantState state = new OccupantState();
            state.type = occupant.getClass().getSimpleName();
            state.roomNumber = occupant.getRoom().number();
            state.dead = occupant.isDead();

            if (occupant instanceof Arrow arrow) {
                state.broken = arrow.isBroken();
                state.killedWumpus = arrow.killedAWumpus();
            }

            return state;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class HunterState {
        @JsonProperty int roomNumber;
        @JsonProperty boolean dead;
        @JsonProperty int kills;
        @JsonProperty int arrowCount;

        HunterState() {}

        static HunterState fromHunter(Hunter hunter) {
            HunterState state = new HunterState();
            state.roomNumber = hunter.getRoom().number();
            state.dead = hunter.isDead();
            state.kills = hunter.kills();
            state.arrowCount = parseArrowCount(hunter.inventory());
            return state;
        }

        private static int parseArrowCount(String inventory) {
            try {
                String[] lines = inventory.split("\n");
                for (String line : lines) {
                    if (line.contains("Arrows:")) {
                        return Integer.parseInt(line.replaceAll("\\D+", ""));
                    }
                }
            } catch (Exception e) {
                Logger.debug("Failed to parse arrow count: " + e.getMessage());
            }
            return 0;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ScoreState {
        @JsonProperty long hunterDeaths;
        @JsonProperty long wumpusKills;

        ScoreState() {}

        static ScoreState fromGame(List<Room> rooms) {
            ScoreState state = new ScoreState();
            state.hunterDeaths = rooms.stream()
                    .flatMap(r -> r.occupants().stream())
                    .filter(Hunter.class::isInstance)
                    .filter(Room.Occupant::isDead)
                    .count();
            state.wumpusKills = rooms.stream()
                    .flatMap(r -> r.occupants().stream())
                    .filter(Wumpus.class::isInstance)
                    .filter(Room.Occupant::isDead)
                    .count();
            return state;
        }
    }
}
