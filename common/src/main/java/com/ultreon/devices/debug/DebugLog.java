package com.ultreon.devices.debug;

import dev.architectury.platform.Platform;
import org.intellij.lang.annotations.PrintFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * The DebugLog class provides logging functionality for development purposes.
 * Messages logged using DebugLog are only displayed in the development environment.
 */
public class DebugLog {
    private static final Logger LOGGER = LoggerFactory.getLogger("Devices : Debugger");

    /**
     * Logs a message in the development environment.
     *
     * @param message the message to log
     */
    public static void log(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info(message);
        }
    }

    /**
     * Logs a message in the development environment.
     *
     * @param message the message to log
     */
    public static void log(Object... message) {
        log(String.join(" ", Arrays.stream(message).map(Objects::toString).toList()));
    }

    /**
     * Logs a message in the development environment.
     *
     * @param message the message to log
     */
    public static void log(@PrintFormat String message, Object... args) {
        log(String.format(message, args));
    }

    /**
     * Logs the time along with a message in the development environment.
     *
     * @param ticks   the number of ticks
     * @param message the message to log
     */
    public static void logTime(long ticks, String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info("(@" + ticks + " ticks) " + message);
        }
    }
}
