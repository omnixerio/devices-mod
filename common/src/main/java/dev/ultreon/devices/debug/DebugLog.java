package dev.ultreon.devices.debug;

import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class DebugLog {
    private static final Logger LOGGER = LoggerFactory.getLogger("Devices : Debugger");

    public static void log(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info(message);
        }
    }

    public static void log(Object... message) {
        log(String.join(" ", Arrays.stream(message).map(Objects::toString).toList()));
    }

    public static void logTime(long ticks, String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info("(@" + ticks + " ticks) " + message);
        }
    }
}
