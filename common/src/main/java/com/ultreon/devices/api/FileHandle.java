package com.ultreon.devices.api;

public class FileHandle {
    private final String path;
    private final byte[] data;

    private int offset = 0;
    private boolean closed;

    public FileHandle(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public int tell() {
        return offset;
    }

    public void seek(int offset) {
        this.offset = offset;
    }

    public byte[] read(int length) {
        if (closed) {
            throw new IllegalStateException("File is closed");
        }
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        offset += length;
        return result;
    }

    public void write(byte[] data) {
        if (closed) {
            throw new IllegalStateException("File is closed");
        }
        System.arraycopy(data, 0, this.data, offset, data.length);
        offset += data.length;
    }

    public String getPath() {
        return path;
    }

    public void close() {
        this.closed = true;
    }
}
