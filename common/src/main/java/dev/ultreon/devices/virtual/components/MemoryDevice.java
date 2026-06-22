package dev.ultreon.devices.virtual.components;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface MemoryDevice {
    byte read(int address);

    void write(int address, byte value);
}
