package dev.ultreon.devices.debug;

import dev.ultreon.mods.xinexlib.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/// ## Debug Logger
/// This class is used for logging debug information.
///
/// @author [Qubilux](https://github.com/XyperCode)
public class DebugLog {
    private static final Logger LOGGER = LoggerFactory.getLogger("Devices : Debugger");

    /// ## Log a message
    /// This method is used for logging debug information.
    ///
    /// @param message The message to log
    public static void log(String message) {
        if (Services.isDevelopmentEnvironment()) {
            LOGGER.info(message);
        }
    }

    /// ## Log a message
    /// This method is used for logging debug information.
    ///
    /// @param message The message to log
    public static void log(Object... message) {
        log(String.join(" ", Arrays.stream(message).map(Objects::toString).toList()));
    }

    /// ## Log a message with a time marker
    /// This method is used for logging debug information.
    ///
    /// @param message The message to log
    public static void logTime(long ticks, String message) {
        if (Services.isDevelopmentEnvironment()) {
            LOGGER.info("(@{} ticks) {}", ticks, message);
        }
    }
}
