package com.noradltd.wumpus;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.rough.FillStyle;
import guru.nidi.graphviz.rough.Roughifyer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.node;

// TODO [DRY] Visualizer creates room labels and checks hazardous status in multiple places.
//   The label creation logic in createRoomLabel() and hazard check in isHazardous() could be cleaner.
// TODO [Law of Demeter] visualize_() method deeply navigates room.exits() and room.occupants().
//   Consider adding helper methods to Room for visualization data.
public class Visualizer {


    private static String createDttmStamp() {
        Date utilDate = new Date(); // Date.from(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        return dateFormat.format(utilDate);
    }

    static void visualize(Maze maze) {
        new Visualizer().visualize_(maze);
        Logger.info("Visualizing maze");
    }

    private String createRoomLabel(Room room) {
        Logger.debug("Labeling room " + room.number());
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
            return Color.RED;
        }
        return Color.BLACK;
    }

    private boolean isHazardous(Room room, String label) {
        // note: even a dead wumpus is considered dangerous
        return !label.equals(Integer.toString(room.number()));
    }

    private List<Room> getAllRooms(Maze maze) {
        return MazeTraverser.collectAllRoomsAsList(maze.entrance());
    }

    // TODO [Long Method] This method is doing too much - creating nodes, managing links, and building graph.
    //   Remediation: Extract methods like addRoomNode(), processRoomLinks(), shouldLinkRooms().
    // TODO [Complexity] The nested conditionals for link management are hard to follow.
    //   Consider using a Set<Pair<Integer,Integer>> to track bidirectional links more cleanly.
    private void visualize_(Maze maze) {
        List<Room> rooms = getAllRooms(maze);
        MutableGraph graph = mutGraph("maze").setDirected(false);
        HashMap<String, List<String>> links = new HashMap<>();
        for (Room room : rooms) {
            String roomLabel = createRoomLabel(room);
            Color roomColor = getRoomColor(room, roomLabel);
            graph.add(
                    node("" + room.number())
                            .with(roomColor)
                            .with(Label.raw(roomLabel))
            );
            links.put(roomLabel, new ArrayList<>());
            for (Room exit : room.exits()) {
                String exitLabel = createRoomLabel(exit);
                if (links.containsKey(roomLabel) && links.get(roomLabel).contains(exitLabel)) {
                    //skip!
                } else if (links.containsKey(exitLabel) && links.get(exitLabel).contains(roomLabel)) {
                    //skip!
                } else {
                    links.get(roomLabel).add(exitLabel);
                    if (!links.containsKey(exitLabel)) {
                        links.put(exitLabel, new ArrayList<>());
                    }
                    links.get(exitLabel).add(roomLabel);
                    graph.add(
                            node("" + room.number())
                                    .with(roomColor)
                                    .with(Label.raw(roomLabel))
                                    .link(node("" + exit.number()))
                    );
                }
            }
        }
        exportGraph(graph);
    }

    private static void exportGraph(MutableGraph graph) {
        try {
            String fileName = createDttmStamp() + "_maze.png";
            Graphviz.fromGraph(graph)
                    .processor(new Roughifyer()
                            .bowing(2)
                            .curveStepCount(6)
                            .roughness(1)
                            .fillStyle(FillStyle.hachure().width(2).gap(5).angle(0)))
                    .height(1000)
                    .render(Format.PNG)
                    .toFile(new File(fileName));
            Logger.info("exported maze to " + fileName);
        } catch (IOException ioe) {
            Logger.error("Could not visualize maze", ioe);
        } catch (Exception e) {
            // Handle cases where native libraries (J2V8, Graphviz) are not available
            Logger.error("Visualization unavailable: " + e.getMessage());
        }
    }
}
