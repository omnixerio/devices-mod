package dev.ultreon.devices.core;

import org.jetbrains.annotations.Nullable;
import org.jnode.fs.FileSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;

public interface FS {
    void close() throws IOException;

    InputStream read(Path path, OpenOption... options) throws IOException;

    OutputStream write(Path path, OpenOption... options) throws IOException;

    boolean exists(Path path) throws IOException;

    void flush() throws IOException;

    void createFile(Path path, byte[] data) throws IOException;

    void createDirectory(Path path) throws IOException;

    Iterator<String> listDirectory(Path of) throws IOException;

    void delete(Path path) throws IOException;

    long size(Path path) throws IOException;

    void rename(Path from, String name) throws IOException;

    boolean isFolder(Path path) throws IOException;

    boolean isFile(Path path) throws IOException;

    boolean isSymbolicLink(Path path) throws IOException;

    @Nullable LockKey lock(String path) throws IOException;

    void unlock(String directory);

    boolean isLocked(String directory);

    void setReadOnly(Path of, boolean b) throws IOException;

    void setExecutable(Path of, boolean b) throws IOException;

    boolean isExecutable(Path of) throws IOException;

    boolean isWritable(Path of) throws IOException;

    boolean isReadable(Path of) throws IOException;

    int getOwner(Path of) throws IOException;

    int getGroup(Path of) throws IOException;

    int getPermissions(Path of) throws IOException;

    void setPermissions(Path of, int mode) throws IOException;

    void setOwner(Path of, int uid, int gid) throws IOException;

    void setGroup(Path of, int gid) throws IOException;

    void setOwner(Path of, int uid) throws IOException;

    long getGeneration(Path of) throws IOException;

    void setGeneration(Path of, long generation) throws IOException;

    boolean isReadOnly(Path of) throws IOException;

    boolean canWrite(Path of) throws IOException;

    boolean canRead(Path of) throws IOException;

    boolean canExecute(Path of) throws IOException;

    static FS loadExt2(Path path) throws IOException, FileSystemException {
        return Ext2FS.open(false, path);
    }

    static FS loadExt2Forced(Path path) throws IOException, FileSystemException {
        return Ext2FS.openForced(path);
    }

    static FS loadExt2ReadOnly(Path path) throws IOException, FileSystemException {
        return Ext2FS.open(true, path);
    }

    static FS formatExt2(Path path, long diskSize) throws IOException, FileSystemException {
        return Ext2FS.format(path, diskSize);
    }

    long lastModified(Path path) throws IOException;

    long lastAccessed(Path path) throws IOException;

    long creationTime(Path path) throws IOException;

    void setLastAccessed(Path path, long time) throws IOException;

    void setLastModified(Path path, long time) throws IOException;

    void setCreationTime(Path path, long time) throws IOException;

    long getTotalSpace() throws IOException;

    long getUsableSpace() throws IOException;

    long getFreeSpace() throws IOException;

    void move(Path source, Path destination) throws IOException;

    void copy(Path source, Path destination) throws IOException;

    void write(Path path, long offset, byte[] dataBytes) throws IOException;

    void write(Path path, byte[] dataBytes) throws IOException;

    void truncate(Path path, long size) throws IOException;

    void read(Path path, ByteBuffer buffer, long offset) throws IOException;
}
