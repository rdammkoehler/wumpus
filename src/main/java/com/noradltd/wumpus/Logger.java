package com.noradltd.wumpus;

import org.slf4j.LoggerFactory;

public class Logger {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger("HuntTheWumpusLogger");

    static final void error(String message) {
        logger.error(message);
    }

    static final void info(String message) {
        logger.info(message);
    }

    static final void debug(String message) {
        logger.debug(message);
    }
}
