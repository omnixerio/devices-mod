package dev.ultreon.devices.exception;

import dev.ultreon.devices.core.ComputerScreen;

/// ## Exception for when a world is not loaded
/// This exception occurs whn the world isn't loaded in a world-required environment.
///
/// @author [Qubilux](https://github.com/XyperCode)
/// @see ComputerScreen#isWorldLess()
public class WorldLessException extends Exception {
    public WorldLessException(String message) {
        super(message);
    }
}
