package com.noradltd.wumpus;

public class Helpers {
    public static String reinterpolatEscapedCharacters(String input) {
        return input.replaceAll("\\\\n", "\n");
    }
}
