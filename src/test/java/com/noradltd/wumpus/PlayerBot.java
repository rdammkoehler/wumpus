package com.noradltd.wumpus;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;


public class PlayerBot {

    public static final String[] DEFAULT_ARGS = {};

    public static final String[] NO_HAZARDS = {
            "--bats", "0",
            "--pits", "0",
            "--wumpi", "0"
    };

    private static void tick() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void forceLoggersToInfo() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(tmpLogger -> tmpLogger.setLevel(Level.INFO));
    }

    @Disabled("Disabled until because its slow!")
    @Test
    public void fafo() {
//        forceLoggersToInfo();
        String[] script = new String[]{
                "m 1\n",
                "m 2\n",
                "q\n",
                "no\n"
        };

        InputStream originalStdin = System.in;
//        ByteArrayOutputStream stdout = Helpers.captureStdout();
        try {
            Player player = startPlayer();
            Thread gameThread = startGame();

            player.play(script);

            gameThread.join(5000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.setIn(originalStdin);
            Helpers.resetStdout();
//            System.err.println(stdout);
        }
    }

    private static Player startPlayer() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        inputStream.connect(outputStream);
        System.setIn(inputStream);
        Player player = new Player(outputStream);
        new Thread(player).start();
        System.err.println("player is starting");
        return player;
    }

    private static Thread startGame() {
//        Runnable gameRunner = () -> Main.main(DEFAULT_ARGS);
        Runnable gameRunner = () -> Main.main(NO_HAZARDS);
        Thread gameThread = new Thread(gameRunner);
        gameThread.start();
        while (!gameThread.isAlive()) tick();
        System.err.println("game has started");
        return gameThread;
    }

    private static class Player implements Runnable {

        final List<String> commands;
        private final PipedOutputStream outputStream;

        Player(PipedOutputStream outputStream) {
            this.outputStream = outputStream;
            commands = new ArrayList<>();
        }

        public void enqueue(String command) {
            tick(); // TODO so we have to wait between commands, how to do 'quickly'?
            System.err.println("commanding! " + command);
            synchronized (commands) {
                commands.add(command);
                commands.notifyAll();
            }
        }


        public void run() {
//            boolean userQuit = false;
            boolean running = true;
            while (running) {
                try {
                    if (!commands.isEmpty()) {
                        String command = commands.remove(0);
                        outputStream.write(command.getBytes());
                        tick();
                        // TODO insert 'player algorithm here'
                        //  but first you need a way to inspect the activity by
                        //  looking at 'stdout'
                        //  therefore this thing needs to somehow notify the
                        //  outter thread that is using it/running the 'player algo'
//                        running = userQuit && "no".equals(command);
//                        userQuit = "q".equals(command) || "quit".equals(command);
                    }
                    if (running) {
                        synchronized (commands) {
                            commands.wait();
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                    System.err.println("Error");
                }
            }
        }

//        private boolean allCommandsSucceded(String[] commands) {
//            // here I am speculating that I'll need to know if the command
//            // was received correctly or something
//            return Arrays.stream(commands).map(this::enqueue).reduce(Boolean.TRUE, Boolean::logicalAnd);
//        }

        public void play(String[] script) {
//            if (!allCommandsSucceded(script)) {
//                System.err.println("failure, some commands did not succeed");
//            }
            for (String cmd : script) {
                enqueue(cmd);
            }
        }
    }
}
