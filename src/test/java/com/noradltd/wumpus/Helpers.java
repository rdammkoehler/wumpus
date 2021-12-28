package com.noradltd.wumpus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Helpers {
    public static ByteArrayOutputStream captureStdout() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdout));
        return stdout;
    }
}
