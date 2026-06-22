package dev.ultreon.devices.virtual.components;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Experimental
public class Memory {

    public void setByte(int position, byte value) {
        write(position, value);
    }

    public byte getByte(int position) {
        return read(position);
    }

    public void setShort(int position, short value) {
        write(position, (byte) (value >> 8));
        write(position + 1, (byte) value);
    }

    public short getShort(int position) {
        return (short) ((read(position) << 8) | read(position + 1));
    }

    public void setInt(int position, int value) {
        write(position, (byte) (value >> 24));
        write(position + 1, (byte) (value >> 16));
        write(position + 2, (byte) (value >> 8));
        write(position + 3, (byte) value);
    }

    public int getInt(int position) {
        return (read(position) << 24) | (read(position + 1) << 16) | (read(position + 2) << 8) | read(position + 3);
    }

    public void setLong(int position, long value) {
        write(position, (byte) (value >> 56));
        write(position + 1, (byte) (value >> 48));
        write(position + 2, (byte) (value >> 40));
        write(position + 3, (byte) (value >> 32));
        write(position + 4, (byte) (value >> 24));
        write(position + 5, (byte) (value >> 16));
        write(position + 6, (byte) (value >> 8));
        write(position + 7, (byte) value);
    }

    public long getLong(int position) {
        return ((long) read(position) << 56) | ((long) read(position + 1) << 48) | ((long) read(position + 2) << 40) | ((long) read(position + 3) << 32) | (read(position + 4) << 24) | (read(position + 5) << 16) | (read(position + 6) << 8) | read(position + 7);
    }

    public void setFloat(int position, float value) {
        setInt(position, Float.floatToIntBits(value));
    }

    public float getFloat(int position) {
        return Float.intBitsToFloat(getInt(position));
    }

    public void setDouble(int position, double value) {
        setLong(position, Double.doubleToLongBits(value));
    }

    public double getDouble(int position) {
        return Double.longBitsToDouble(getLong(position));
    }

    public void setChar(int position, char value) {
        write(position, (byte) (value >> 8));
        write(position + 1, (byte) value);
    }

    public char getChar(int position) {
        return (char) ((read(position) << 8) | read(position + 1));
    }

    public void read(byte[] pixels, int pointer) {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = read(pointer + i);
        }
    }

    public static class Region {
        final int start;
        final int end;
        final MemoryDevice device;

        Region(int start, int end, MemoryDevice device) {
            this.start = start;
            this.end = end;
            this.device = device;
        }

        boolean contains(int address) {
            return address >= start && address <= end;
        }

        int local(int address) {
            return address - start;
        }
    }

    private final List<Region> regions = new ArrayList<>();

    public void map(int start, int end, MemoryDevice device) {
        if (start > end) {
            throw new IllegalArgumentException("Invalid region");
        }

        for (Region r : regions) {
            boolean overlap =
                start <= r.end &&
                end >= r.start;

            if (overlap) {
                throw new IllegalStateException(
                    String.format(
                        "Region %04X-%04X overlaps %04X-%04X",
                        start,
                        end,
                        r.start,
                        r.end
                    )
                );
            }
        }

        regions.add(new Region(start, end, device));
    }

    public byte read(int address) {
        Region region = find(address);

        if (region == null) {
            throw new IllegalStateException(
                String.format("No device mapped at %04X", address)
            );
        }

        return region.device.read(region.local(address));
    }

    public void write(int address, byte value) {
        Region region = find(address);

        if (region == null) {
            throw new IllegalStateException(
                String.format("No device mapped at %04X", address)
            );
        }

        region.device.write(region.local(address), value);
    }

    private Region find(int address) {
        for (Region region : regions) {
            if (region.contains(address)) {
                return region;
            }
        }

        return null;
    }
}