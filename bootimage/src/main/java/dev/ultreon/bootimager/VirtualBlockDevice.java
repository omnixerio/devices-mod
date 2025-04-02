package dev.ultreon.bootimager;

import org.jnode.driver.block.BlockDeviceAPI;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class VirtualBlockDevice implements BlockDeviceAPI {
    private final RandomAccessFile file;

    public VirtualBlockDevice(String filePath, long size) throws IOException {
        Path path = Path.of(filePath);
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        this.file = new RandomAccessFile(filePath, "rw");

        // Ensure the file is initialized to the specified size
        if (file.length() < size) {
            file.setLength(size);
        }
    }

    @Override
    public long getLength() throws IOException {
        return file.length();
    }

    @Override
    public void read(long devOffset, ByteBuffer dest) throws IOException {
        if (devOffset < 0 || devOffset >= file.length()) {
            throw new IOException("Invalid read offset");
        }
        file.seek(devOffset);
        byte[] buffer = new byte[dest.remaining()];
        int bytesRead = file.read(buffer);
        if (bytesRead < 0) {
            throw new IOException("End of file reached");
        }
        dest.put(buffer, 0, bytesRead);
    }

    @Override
    public void write(long devOffset, ByteBuffer src) throws IOException {
        if (devOffset < 0 || devOffset >= file.length()) {
            throw new IOException("Invalid write offset");
        }
        file.seek(devOffset);
        byte[] buffer = new byte[src.remaining()];
        src.get(buffer);
        file.write(buffer);
    }

    @Override
    public void flush() throws IOException {
        file.getFD().sync();
    }

    public void close() throws IOException {
        file.close();
    }
}