package com.noradltd.wumpus;

import java.util.HashMap;
import java.util.Map;

// TODO this is some cluster fuck you've created
public class ScenarioContext {
    private static final String HUNTER = "hunter";
    private static final String MAZE = "maze";
    private static final String ROOM = "room";
    private static final String WUMPUS = "wumpus";
    private static final Map<String, Object> scenarioContext = new HashMap<>();

    static final Hunter getHunter() {
        return (Hunter) getOther(HUNTER);
    }

    static final Hunter setHunter(Hunter hunter) {
        return (Hunter) setOther(HUNTER, hunter);
    }

    static final Maze getMaze() {
        return (Maze) getOther(MAZE);
    }

    static final Maze setMaze(Maze maze) {
        return (Maze) setOther(MAZE, maze);
    }

    static final Room getRoom() {
        return (Room) getOther(ROOM);
    }

    static final Room setRoom(Room room) {
        return (Room) setOther(ROOM, room);
    }

    static final Wumpus getWumpus() {
        return (Wumpus) getOther(WUMPUS);
    }

    static final Wumpus setWumpus(Wumpus wumpus) {
        return (Wumpus) setOther(WUMPUS, wumpus);
    }

    static final Object getOther(String key) {
        return scenarioContext.get(key);
    }

    static final Object setOther(String key, Object other) {
        Object previousValue = scenarioContext.get(key);
        scenarioContext.put(key, other);
        return previousValue;
    }

    public static void reset() {
        scenarioContext.clear();
    }
}
