package com.noradltd.wumpus;

import org.slf4j.LoggerFactory;

public final class Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger("HuntTheWumpusLogger");

    static void error(String message) {
        logger.error(message);
    }

    static void error(String message, Throwable thrown) {
        logger.error(message, thrown);
    }

    // TODO [Code Hygiene] Remove commented-out duplicate code (lines below were duplicates).
    //   Dead code reduces readability and causes confusion about intended behavior.

    static void info(String message) {
        logger.info(message);
    }

    static void debug(String message) {
        logger.debug(message);
    }

    static void debug(String message, Throwable thrown) {
        logger.debug(message, thrown);
    }
}
