package dev.ultreon.devices.core;

import com.google.common.base.Preconditions;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jnode.driver.block.BlockDeviceAPI;
import org.jnode.driver.virtual.VirtualDevice;
import org.jnode.fs.*;
import org.jnode.fs.FileSystem;
import org.jnode.fs.FileSystemException;
import org.jnode.fs.ext2.*;
import org.jnode.fs.spi.AbstractFileSystem;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Ext2FS implements FS {
    private final FileSystem<?> fs;
    private final ConcurrentMap<Path, Unit> locks = new ConcurrentHashMap<>();

    private Ext2FS(Ext2FileSystem fs) {
        this.fs = fs;

        Cleaner cleaner = Cleaner.create();
        cleaner.register(this, () -> {
            try {
                fs.close();
            } catch (IOException e) {
                LoggerFactory.getLogger(Ext2FS.class).error("Failed to close filesystem", e);
            }
        });
    }

    public static Ext2FS open(Path filePath) throws IOException, FileSystemException {
        return open(false, filePath);
    }

    public static Ext2FS open(boolean readOnly, Path filePath) throws IOException, FileSystemException {
        var device = new VirtualDevice("MineDisk");
        var blockDevice = new VirtualBlockDevice(filePath.toFile().getAbsolutePath(), Files.size(filePath));
        device.registerAPI(BlockDeviceAPI.class, blockDevice);

        var type = new Ext2FileSystemType();
        var fs = new Ext2FileSystem(device, readOnly, type);
        fs.read();
        return new Ext2FS(fs);
    }

    public static Ext2FS openForced(Path filePath) throws IOException, FileSystemException {
        var device = new VirtualDevice("MineDisk");
        var blockDevice = new VirtualBlockDevice(filePath.toFile().getAbsolutePath(), Files.size(filePath));
        device.registerAPI(BlockDeviceAPI.class, blockDevice);

        var type = new Ext2FileSystemType();
        var fs = new Ext2FileSystem(device, false, type);
        fs.getSuperblock().setState(Ext2Constants.EXT2_VALID_FS);
        fs.flush();
        fs.read();
        return new Ext2FS(fs);
    }

    public static Ext2FS format(Path filePath, long diskSize) throws IOException, FileSystemException {
        if (diskSize <= 16384) throw new IllegalArgumentException("Disk size must be greater than 16 KiB");

        var device = new VirtualDevice("MineDisk");
        device.registerAPI(BlockDeviceAPI.class, new VirtualBlockDevice(filePath.toFile().getAbsolutePath(), diskSize));

        var blockDevice = new VirtualBlockDevice(filePath.toFile().getAbsolutePath(), diskSize);
        var formatter = new Ext2FileSystemFormatter(BlockSize._1Kb);
        var fs = formatter.format(device);
        return new Ext2FS(fs);
    }

    @Override
    public void close() throws IOException {
        fs.close();
    }

    @Override
    public InputStream read(Path path, OpenOption... options) throws IOException {
        FSFile file = getFileAt(path);
        if (file == null) throw new NoSuchFileException("File not found: " + path);
        return new FSInputStream(file, options);
    }

    @Override
    public OutputStream write(Path path, OpenOption... options) throws IOException {
        boolean create = false;
        boolean createNew = false;
        for (OpenOption option : options) {
            if (option == StandardOpenOption.CREATE) {
                create = true;
            } else if (option == StandardOpenOption.CREATE_NEW) {
                createNew = true;
            }
        }

        if (create && createNew) throw new IllegalArgumentException("Cannot create and createNew");

        FSFile file;
        if (create) {
            Ext2Directory parentDir = getDirectoryAt(path.getParent());
            if (parentDir == null) {
                throw new NoSuchFileException(path.getParent().toString(), null, "parent directory not found");
            }
            Ext2Entry entry = (Ext2Entry) parentDir.getEntry(path.getFileName().toString());
            if (entry == null) {
                file = parentDir.addFile(path.getFileName().toString()).getFile();
            } else if (entry.isFile()) {
                file = entry.getFile();
            } else {
                throw new FileAlreadyExistsException("Directory already exists: " + path);
            }
        } else if (createNew) {
            Ext2Directory parentDir = getDirectoryAt(path.getParent());
            if (parentDir == null) {
                throw new NoSuchFileException(path.getParent().toString(), null, "parent directory not found");
            }
            if (parentDir.getEntry(path.getFileName().toString()) != null) {
                throw new FileAlreadyExistsException("File already exists: " + path);
            }
            file = parentDir.addFile(path.getFileName().toString()).getFile();
        } else {
            file = getFileAt(path);
            if (file == null) throw new NoSuchFileException(path.toString(), null, "file not found");
        }
        return new FSOutputStream(file, options);
    }

    @Override
    public boolean exists(Path path) {
        try {
            return getFsEntry(path) != null;
        } catch (IOException e) {
            return false;
        }
    }

    private FSFile getFileAt(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return null;
        return entry.getFile();
    }

    private Ext2Directory getDirectoryAt(Path path) throws IOException {
        Preconditions.checkNotNull(path, "path");
        Ext2Entry fsEntry = getFsEntry(path);
        if (fsEntry == null) return null;
        if (!fsEntry.isDirectory()) throw new IllegalArgumentException("Path is not a directory: " + path);
        return (Ext2Directory) fsEntry.getDirectory();
    }

    private @Nullable Ext2Entry getFsEntry(Path path) throws IOException {
        String string = path.toString().replace("\\", "/");
        if (!string.startsWith("/")) path = Path.of("/" + path);
        if (path.getParent() == null) return (Ext2Entry) fs.getRootEntry();

        Ext2Directory root = (Ext2Directory) fs.getRootEntry().getDirectory();
        for (Path s : path.getParent()) {
            Ext2Entry entry = (Ext2Entry) root.getEntry(s.toString());
            if (!entry.isDirectory()) return null;
            root = (Ext2Directory) entry.getDirectory();
            if (root == null) return null;
        }
        return (Ext2Entry) root.getEntry(path.getFileName().toString());
    }

    @Override
    public void flush() throws IOException {
        if (fs instanceof AbstractFileSystem<?>) ((AbstractFileSystem) fs).flush();
    }

    @Override
    public void createFile(Path path, byte[] data) throws IOException {
        String string = path.toString().replace("\\", "/");
        if (!string.startsWith("/")) path = Path.of("/" + path);
        else if (string.equals("/")) throw new IOException("Invalid path for file: " + path);

        Ext2Directory parentDir = path.getParent() == null ? (Ext2Directory) fs.getRootEntry().getDirectory() : getDirectoryAt(path.getParent());
        if (parentDir == null)
            throw new NoSuchFileException(path.getParent().toString(), null, "parent directory not found");
        FSFile file = parentDir.addFile(path.getFileName().toString()).getFile();
        file.flush();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < Math.ceil((double) data.length / 1024); i++) {
            buffer.clear();
            buffer.put(data, i * 1024, Math.min(1024, data.length - i * 1024));
            buffer.flip();
            file.write(0, buffer);
            file.flush();
        }
        if (fs instanceof AbstractFileSystem<?> absFs) absFs.flush();
    }

    @Override
    public void createDirectory(Path path) throws IOException {
        String string = path.toString().replace("\\", "/");
        if (!string.startsWith("/")) path = Path.of("/" + path);
        Ext2Directory parentDir = path.getParent() == null ? (Ext2Directory) fs.getRootEntry().getDirectory() : getDirectoryAt(path.getParent());
        if (path.getParent() != null && parentDir != null) {
            parentDir.addDirectory(path.getFileName().toString());
            parentDir.flush();
        } else {
            FSDirectory directory = fs.getRootEntry().getDirectory();
            directory.addDirectory(path.getFileName().toString());
            directory.flush();
        }
        if (fs instanceof AbstractFileSystem<?> absFs) absFs.flush();
    }

    @Override
    public Iterator<String> listDirectory(Path of) throws IOException {
        Ext2Directory dir = getDirectoryAt(of);
        if (dir == null) return Collections.emptyIterator();
        return new DirNameIterator(dir);
    }

    @Override
    public void delete(Path path) throws IOException {
        Ext2Entry parent = path.getParent() == null ? (Ext2Entry) fs.getRootEntry() : getFsEntry(path.getParent());
        if (parent == null) return;

        if (!parent.isDirectory()) throw new NotDirectoryException("Path is not a directory: " + path);

        Ext2Entry entry = (Ext2Entry) parent.getDirectory().getEntry(path.getFileName().toString());
        if (entry.isDirectory()) {
            if (entry.getDirectory().iterator().hasNext()) {
                throw new DirectoryNotEmptyException("Directory is not empty: " + path);
            }

            parent.getDirectory().remove(path.getFileName().toString());
            return;
        }

        if (!entry.isFile()) throw new IOException("Path is not a file: " + path);
        parent.getDirectory().remove(path.getFileName().toString());
    }

    @Override
    public long size(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return -1;
        if (!entry.isFile()) return -1;
        return entry.getFile().getLength();
    }

    @Override
    public void rename(Path from, String name) throws IOException {
        if (name.contains("/")) throw new IOException("Invalid name: " + name);

        Ext2Entry parent = from.getParent() == null ? (Ext2Entry) fs.getRootEntry() : getFsEntry(from.getParent());
        if (parent == null) return;

        if (!parent.isDirectory()) throw new NotDirectoryException("Path is not a directory: " + from);

        Ext2Entry entry = (Ext2Entry) parent.getDirectory().getEntry(from.getFileName().toString());
        if (entry == null) return;
        entry.setName(name);
        parent.getDirectory().flush();
    }

    @Override
    public boolean isFolder(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        return entry != null && entry.isDirectory();
    }

    @Override
    public boolean isFile(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        return entry != null && entry.isFile();
    }

    @Override
    public boolean isSymbolicLink(Path path) {
        return false;
    }

    @Override
    public @Nullable LockKey lock(String path) throws IOException {
        if (path == null) throw new IllegalArgumentException("Path must not be null");
        Path pathObj = Path.of(path);
        if (!exists(pathObj)) return null;
        if (this.locks.containsKey(pathObj))
            throw new IOException("Path is already locked: " + pathObj);
        this.locks.put(pathObj, Unit.INSTANCE);
        return new LockKey(pathObj);
    }

    @Override
    public void unlock(String directory) {
        if (directory == null) throw new IllegalArgumentException("Path must not be null");
        this.locks.remove(Path.of(directory));
    }

    @Override
    public boolean isLocked(String directory) {
        return this.locks.containsKey(Path.of(directory));
    }

    @Override
    public void setReadOnly(Path of, boolean b) throws IOException {
        @Nullable Ext2Entry dir = getFsEntry(of);
        if (dir == null) return;
        dir.getAccessRights().setReadable(true, false);
        dir.getAccessRights().setWritable(!b, false);
        dir.getParent().flush();
    }

    @Override
    public void setExecutable(Path of, boolean b) throws IOException {
        @Nullable Ext2Entry dir = getFsEntry(of);
        if (dir == null) return;
        if (dir.isFile()) {
            dir.getAccessRights().setExecutable(b, false);
            dir.getParent().flush();
        } else {
            throw new IOException("Path is not a file: " + of);
        }
    }

    @Override
    public boolean isExecutable(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) return false;
        return entry.isFile() && entry.getAccessRights().canExecute();
    }

    @Override
    public boolean isWritable(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) return false;
        return entry.isFile() && entry.getAccessRights().canWrite();
    }

    @Override
    public boolean isReadable(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) return false;
        return entry.isFile() && entry.getAccessRights().canRead();
    }

    @Override
    public int getOwner(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        return entry.getINode().getUid();
    }

    @Override
    public int getGroup(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        return entry.getINode().getGid();
    }

    @Override
    public int getPermissions(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        return entry.getINode().getMode();
    }

    @Override
    public void setPermissions(Path of, int mode) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        entry.getINode().setMode(mode);
        entry.getParent().flush();
    }

    @Override
    public void setOwner(Path of, int uid, int gid) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        entry.getINode().setUid(uid);
        entry.getINode().setGid(gid);
        entry.getParent().flush();
    }

    @Override
    public void setGroup(Path of, int gid) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        entry.getINode().setGid(gid);
        entry.getParent().flush();
    }

    @Override
    public void setOwner(Path of, int uid) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        entry.getINode().setUid(uid);
        entry.getParent().flush();
    }

    @Override
    public long getGeneration(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        return entry.getINode().getGeneration();
    }

    @Override
    public void setGeneration(Path of, long generation) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        entry.getINode().setGeneration(generation);
        entry.getParent().flush();
    }

    @Override
    public boolean isReadOnly(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) throw new IOException("Path does not exist: " + of);
        return entry.getAccessRights().canRead() && !entry.getAccessRights().canWrite();
    }

    @Override
    public boolean canWrite(Path of) throws IOException {
        return isWritable(of);
    }

    @Override
    public boolean canRead(Path of) throws IOException {
        return isReadable(of);
    }

    @Override
    public boolean canExecute(Path of) throws IOException {
        Ext2Entry entry = getFsEntry(of);
        if (entry == null) return false;
        return entry.getAccessRights().canExecute();
    }

    @Override
    public long lastModified(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return 0;
        return entry.getLastModified();
    }

    @Override
    public long lastAccessed(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return 0;
        return entry.getLastAccessed();
    }

    @Override
    public long creationTime(Path path) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return 0;
        return entry.getINode().getCtime();
    }

    @Override
    public void setLastAccessed(Path path, long time) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return;
        entry.setLastAccessed(time);
        entry.getParent().flush();
    }

    @Override
    public void setLastModified(Path path, long time) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return;
        entry.setLastModified(time);
        entry.getParent().flush();
    }

    @Override
    public void setCreationTime(Path path, long time) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return;
        entry.getINode().setCtime(time);
        entry.getParent().flush();
    }

    @Override
    public long getTotalSpace() throws IOException {
        return fs.getTotalSpace();
    }

    @Override
    public long getUsableSpace() throws IOException {
        return fs.getUsableSpace();
    }

    @Override
    public long getFreeSpace() throws IOException {
        return fs.getFreeSpace();
    }

    @Override
    public void move(Path source, Path destination) throws IOException {
        Ext2Entry sourceEntry = getFsEntry(source);
        Ext2Entry destinationEntry = getFsEntry(destination);
        if (sourceEntry == null || destinationEntry == null) return;

        try (InputStream in = read(source)) {
            createFile(destination, in.readAllBytes());
        }

        sourceEntry.getParent().remove(sourceEntry.getName());
        sourceEntry.getParent().flush();
        destinationEntry.getParent().flush();
    }

    @Override
    public void copy(Path source, Path destination) throws IOException {
        Ext2Entry sourceEntry = getFsEntry(source);
        Ext2Entry destinationEntry = getFsEntry(destination);
        if (sourceEntry == null || destinationEntry == null) return;

        try (InputStream in = read(source)) {
            createFile(destination, in.readAllBytes());
        }

        sourceEntry.getParent().flush();
        destinationEntry.getParent().flush();
    }

    @Override
    public void write(Path path, long offset, byte[] dataBytes) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return;

        if (entry.isFile()) {
            FSFile file = entry.getFile();
            long length = file.getLength();
            if (offset > length) throw new IOException("Offset out of range: " + offset);
            if (offset + dataBytes.length > length) {
                file.setLength(offset + dataBytes.length);
            }
            file.write(offset, ByteBuffer.wrap(dataBytes));
            file.flush();
            entry.getParent().flush();
        } else {
            throw new IOException("Path is not a file: " + path);
        }
    }

    @Override
    public void write(Path path, byte[] dataBytes) throws IOException {
        write(path, 0, dataBytes);
    }

    @Override
    public void truncate(Path path, long size) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return;
        if (entry.isFile()) {
            entry.getFile().setLength(size);
        }
    }

    @Override
    public void read(Path path, ByteBuffer buffer, long offset) throws IOException {
        Ext2Entry entry = getFsEntry(path);
        if (entry == null) return;
        if (entry.isFile()) {
            entry.getFile().read(offset, buffer);
        }
    }

    public FileSystem<?> getFileSystem() {
        return fs;
    }

    private static class FSInputStream extends InputStream {
        private final FSFile file;
        private final ByteBuffer buffer = ByteBuffer.allocate(1024);
        private long bufferOffset;
        private long fileOffset = 0;

        private int markLimit;

        public FSInputStream(FSFile file, OpenOption[] options) throws IOException {
            this.file = file;
            for (OpenOption option : options) {
                if (option == StandardOpenOption.TRUNCATE_EXISTING) {
                    file.setLength(0);
                } else if (option != StandardOpenOption.READ) {
                    throw new UnsupportedOperationException("Option not supported: " + option);
                }
            }

            bufferOffset = 0;
            buffer.clear();
            if (fileOffset + buffer.capacity() > file.getLength()) {
                buffer.limit((int) (file.getLength() - fileOffset));
            }
            file.read(fileOffset, buffer);
            buffer.flip();
        }

        @Override
        public int read() throws IOException {
            if (bufferOffset == buffer.limit()) {
                bufferOffset = 0;
                buffer.clear();
                if (fileOffset + buffer.capacity() > file.getLength()) {
                    buffer.limit((int) (file.getLength() - fileOffset));
                }
                file.read(fileOffset, buffer);
                fileOffset += buffer.capacity();
            }

            return buffer.get() & 0xFF;
        }

        @Override
        public int read(byte @NotNull [] b, int off, int len) throws IOException {
            int bytesRead = 0;
            if (fileOffset >= file.getLength()) {
                return -1;
            }
            while (bytesRead < len) {
                if (fileOffset >= file.getLength()) {
                    return bytesRead;
                }

                int remaining = buffer.remaining();
                int len0 = Math.min(len - bytesRead, remaining);
                if (len0 == 0) {
                    return -1;
                }
                int fileRemaining = (int) (file.getLength() - fileOffset);
                int len1 = Math.min(len0, fileRemaining);
                buffer.get(b, off + bytesRead, len1);
                bufferOffset += len1;
                bytesRead += len1;
                fileOffset += len1;
                if (bufferOffset == buffer.capacity()) {
                    bufferOffset = 0;
                    buffer.clear();
                    if (fileOffset + buffer.capacity() > file.getLength()) {
                        buffer.limit((int) (file.getLength() - fileOffset));
                    }
                    file.read(fileOffset, buffer);
                }

                if (len0 < remaining) {
                    break;
                }
            }
            return bytesRead;
        }

        @Override
        public int read(byte @NotNull [] b) throws IOException {
            return read(b, 0, b.length);
        }
    }

    private static class DirNameIterator implements Iterator<String> {
        private final Iterator<FSEntry> iterator;

        public DirNameIterator(Ext2Directory dir) throws IOException {
            iterator = dir.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public String next() {
            return iterator.next().getName();
        }
    }

    private class FSOutputStream extends OutputStream {
        private final FSFile file;
        private final ByteBuffer buffer = ByteBuffer.allocate(1024);
        private boolean sync = false;
        private long bufferOffset = 0;
        private long fileOffset = 0;

        public FSOutputStream(FSFile file, OpenOption[] options) throws IOException {
            this.file = file;
            for (OpenOption option : options) {
                if (option == StandardOpenOption.TRUNCATE_EXISTING) {
                    file.setLength(0);
                } else if (option == StandardOpenOption.APPEND) {
                    fileOffset = file.getLength();
                } else if (option == StandardOpenOption.SYNC) {
                    sync = true;
                } else if (option != StandardOpenOption.WRITE && option != StandardOpenOption.CREATE && option != StandardOpenOption.CREATE_NEW) {
                    throw new UnsupportedOperationException("Option not supported: " + option);
                }
            }
        }

        @Override
        public void write(int b) throws IOException {
            buffer.put((byte) b);
            bufferOffset++;
            if (bufferOffset == buffer.capacity()) {
                buffer.flip();
                file.write(fileOffset, buffer);
                buffer.clear();
                bufferOffset = 0;
                fileOffset += buffer.capacity();
            }
        }

        @Override
        public void write(byte @NotNull [] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void flush() throws IOException {
            file.flush();
            if (fs instanceof AbstractFileSystem<?> absFs) absFs.flush();
        }

        @Override
        public void close() throws IOException {
            file.flush();
            if (fs instanceof AbstractFileSystem<?> absFs) absFs.flush();
        }
    }
}
