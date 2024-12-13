package com.noradltd.wumpus;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.noradltd.wumpus.Helpers.getAllRooms;
import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public class VisualizerTest {

    //just learning how to use graphviz here
    @Test
    public void test() throws IOException {
        Graph graph = graph("example").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .nodeAttr().with(Font.name("arial"))
                .linkAttr().with("class", "link-class")
                .with(
                        node("a").with(Color.RED).link(node("b")),
                        node("b").link(to(node("c")).with(attr("weight", 5), Style.DASHED))
                );
        Graphviz.fromGraph(graph).height(100).render(Format.PNG).toFile(new File("test.png"));
    }

    String createRoomLabel(Room room) {
        if (!room.occupants().isEmpty()) {
            StringBuilder text = new StringBuilder();
            for (Room.Occupant occupant : room.occupants()) {
                text.append(occupant.getClass().getSimpleName().charAt(0));
            }
            return Integer.toString(room.number()) + text;
        }
        return Integer.toString(room.number());
    }
    Color getRoomColor(Room room, String label) {
        if (isHazardous(room, label)) {
            return Color.BLACK;
        }
        return Color.RED;
    }

    private boolean isHazardous(Room room, String label) {
        return label.equals(Integer.toString(room.number()));
    }

    //ok so that is a basic graph, what do we want our world to look like next?
    @Test
    public void testGraphMaze() throws IOException {
        List<Room> rooms = getAllRooms(MazeLoader.populate(MazeBuilder.build(), new Game.Options("")));
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
                            node(roomLabel)
                                    .with(roomColor)
                                    .link(node(exitLabel))
                    );
                }
            }
        }
        Graphviz.fromGraph(graph).height(1000).render(Format.PNG).toFile(new File("maze.png"));
    }

    //this version does not reveal the hazard type
    @Test
    public void testGraphMaze2() throws IOException {
        List<Room> rooms = getAllRooms(MazeLoader.populate(MazeBuilder.build(), new Game.Options("")));
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
                            node(""+room.number())
                                    .with(roomColor)
                                    .link(node(""+exit.number()))
                    );
                }
            }
        }
        Graphviz.fromGraph(graph).height(1000).render(Format.PNG).toFile(new File("maze.png"));
    }
}
