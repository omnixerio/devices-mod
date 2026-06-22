package dev.ultreon.devices.virtual.components;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

@ApiStatus.Experimental
public class Ram implements MemoryDevice {
    private final ByteBuffer data;

    public Ram(int size) {
        data = MemoryUtil.memAlloc(size);
    }

    public void free() {
        MemoryUtil.memFree(data);
    }

    @Override
    public byte read(int address) {
        return data.get(address);
    }

    @Override
    public void write(int address, byte value) {
        data.put(address, value);
    }
}