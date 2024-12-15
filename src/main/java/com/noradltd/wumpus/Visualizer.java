package com.noradltd.wumpus;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.rough.FillStyle;
import guru.nidi.graphviz.rough.Roughifyer;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.node;

public class Visualizer {
    static void visualize(Maze maze) {
        new Visualizer().visualize_(maze);
    }

    private String createRoomLabel(Room room) {
        if (!room.occupants().isEmpty()) {
            StringBuilder text = new StringBuilder();
            for (Room.Occupant occupant : room.occupants()) {
                text.append(occupant.getClass().getSimpleName().charAt(0));
            }
            return Integer.toString(room.number()) + text;
        }
        return Integer.toString(room.number());
    }

    private Color getRoomColor(Room room, String label) {
        if (isHazardous(room, label)) {
            return Color.BLACK;
        }
        return Color.RED;
    }

    private boolean isHazardous(Room room, String label) {
        return label.equals(Integer.toString(room.number()));
    }

    private List<Room> getAllRooms(Maze maze) {
        return collectRoom(maze.entrance(), new HashSet<>()).stream().toList();
    }

    private Set<Room> collectRoom(Room room, Set<Room> rooms) {
        if (!rooms.contains(room)) {
            rooms.add(room);
            for (Room exit : room.exits()) {
                if (!rooms.contains(exit)) {
                    collectRoom(exit, rooms);
                }
            }
        }
        return rooms;
    }

    private void visualize_(Maze maze) {
        List<Room> rooms = getAllRooms(maze);
        MutableGraph graph = mutGraph("maze").setDirected(false);
        HashMap<String, List<String>> links = new HashMap<>();
        for (Room room : rooms) {
            String roomLabel = createRoomLabel(room);
            Color roomColor = getRoomColor(room, roomLabel);
            links.put(roomLabel, new ArrayList<String>());
            for (Room exit : room.exits()) {
                String exitLabel = createRoomLabel(exit);
                if (links.containsKey(roomLabel) && links.get(roomLabel).contains(exitLabel)) {
                    //skip!
                } else if (links.containsKey(exitLabel) && links.get(exitLabel).contains(roomLabel)) {
                    //skip!
                } else {
                    links.get(roomLabel).add(exitLabel);
                    if (!links.containsKey(exitLabel)) {
                        links.put(exitLabel, new ArrayList<String>());
                    }
                    links.get(exitLabel).add(roomLabel);
                    graph.add(
                            node("" + room.number())
                                    .with(roomColor)
                                    .link(node("" + exit.number()))
                    );
                }
            }
        }
        try {
            Graphviz.fromGraph(graph)
                    .processor(new Roughifyer()
                            .bowing(2)
                            .curveStepCount(6)
                            .roughness(1)
                            .fillStyle(FillStyle.hachure().width(2).gap(5).angle(0))
                            .font("*serif", "Comic Sans MS"))
                    .height(1000)
                    .render(Format.PNG)
                    .toFile(new File("maze.png"));
        } catch (IOException ioe) {
            Logger.error("Could not visualize maze", ioe);
        }
    }
}
