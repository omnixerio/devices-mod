package dev.ultreon.devices.core.io.drive;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.core.DriveManager;
import dev.ultreon.devices.core.Ext2FS;
import dev.ultreon.devices.core.FS;
import dev.ultreon.devices.core.LockKey;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.Path;
import dev.ultreon.devices.core.io.ServerFolder;
import dev.ultreon.devices.core.io.action.FileAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jnode.fs.FileSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.UUID;

/// @author MrCrayfish
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class AbstractDrive implements FS {
    private boolean deferred;
    private Ext2FS fs;
    private boolean invalid = false;
    protected String name;
    protected UUID uuid;
    private boolean damaged;
    private long lastAccessed;

    AbstractDrive() {
        this(UUID.randomUUID());
    }

    AbstractDrive(UUID uuid) {
        name = "Drive";
        this.uuid = uuid;

        if (UltreonDevices.getServer() == null) {
            deferred = true;
            return;
        }

        try {
            java.nio.file.Path resolve = UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drives/" + uuid + ".ext2");
            if (Files.notExists(resolve)) {
                if (Files.notExists(resolve.getParent())) Files.createDirectories(resolve.getParent());
                setFs(Ext2FS.format(resolve, 1L));
                setup();
            } else setFs(Ext2FS.open(resolve));
        } catch (IOException | FileSystemException e) {
            invalid = true;
            setFs(null);
        }
    }

    public static void deleteDrivePath(UUID uuid) {
        try {
            java.nio.file.Path resolve = UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drives/" + uuid + ".ext2");
            if (Files.exists(resolve)) Files.delete(resolve);
        } catch (IOException e) {
            UltreonDevices.LOGGER.error("Failed to delete drive path for drive {}", uuid, e);
        }
    }

    protected void setup() {

    }

    AbstractDrive(UUID uuid, java.nio.file.Path drivePath) throws FileSystemException, IOException {
        name = "OS";
        this.uuid = uuid;
        setFs(Ext2FS.open(drivePath));
    }

    AbstractDrive(String name) {
        this(name, UUID.randomUUID());
    }

    public AbstractDrive(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;

        if (UltreonDevices.getServer() == null) {
            deferred = true;
            setFs(null);
            return;
        }

        try {
            java.nio.file.Path resolve = UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drives/" + uuid + ".ext2");
            if (Files.notExists(resolve)) {
                if (Files.notExists(resolve.getParent())) Files.createDirectories(resolve.getParent());
                setFs(Ext2FS.format(resolve, 16 * 1024 * 1024));
                setup();
            } else setFs(Ext2FS.open(resolve));
        } catch (IOException | FileSystemException e) {
            invalid = true;
            setFs(null);
        }
    }

    public static java.nio.file.Path getDrivePath(UUID uuid) {
        return UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drives/" + uuid + ".ext2");
    }

    private void createProtectedFolder(Ext2FS fs, String name) {
        setLastAccessed(System.currentTimeMillis());
        try {
            Path path = Path.of(name);
            fs.createDirectory(path);
            fs.setReadOnly(path, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        setLastAccessed(System.currentTimeMillis());
        return name;
    }

    public void setName(String name) {
        setLastAccessed(System.currentTimeMillis());
        this.name = name;
    }

    public UUID getUuid() {
        setLastAccessed(System.currentTimeMillis());
        return uuid;
    }

    @Deprecated
    public ServerFolder getRoot(Level level) {
        setLastAccessed(System.currentTimeMillis());
        return null;
    }

    public FileSystem.Response handleFileAction(FileSystem fileSystem, FileAction action, Level level) {
        setLastAccessed(System.currentTimeMillis());
        CompoundTag actionData = action.data();
        try {
            LockKey lock = getFs().lock(actionData.getString("directory"));
            try {
                CompoundTag data = actionData.getCompound("data");
                return switch (action.type()) {
                    case NEW_FILE -> newFile(actionData, data);
                    case NEW_FOLDER -> newFolder(actionData, data);
                    case NEW_FOLDERS -> newFolders(actionData, data);
                    case DELETE -> delete(actionData);
                    case RENAME -> rename(actionData);
                    case WRITE -> writeData(actionData, data);
                    case EXISTS -> exists(actionData);
                    case READ -> readData(actionData);
                    case LIST_DIR -> listDir(actionData);
                    case INFO -> info(actionData);
                    case MOVE -> move(actionData);
                    case COPY -> copy(actionData);
                    case EXTRA_INFO -> extraInfo(actionData);
                    default -> throw new IOException("Invalid FS action: " + action.type());
                };
            } finally {
                if (lock != null) {
                    getFs().unlock(actionData.getString("directory"));
                }
            }
        } catch (IOException e) {
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "I/O error: " + e.getMessage());
        }
    }

    private FileSystem.Response newFolders(CompoundTag actionData, CompoundTag data) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        String string = path.toString();
        if (string.endsWith("/")) string = string.substring(0, string.length() - 1);
        if (string.startsWith("/")) string = string.substring(1);
        String[] folders = string.split("/");
        path = Path.of("/");
        for (int i = 0; i < folders.length; i++) {
            path = path.resolve(folders[i]);
            try {
                if (!getFs().exists(path)) {
                    try {
                        getFs().createDirectory(path);
                    } catch (IOException e) {
                        return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Failed to create folder: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Failed to check folder: " + e.getMessage());
            }
        }

        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "Created folder " + folders[folders.length - 1], info(path));
    }

    private CompoundTag info(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        CompoundTag data = new CompoundTag();
        CompoundTag drive = new CompoundTag();
        drive.putString("name", getName());
        drive.put("uuid", NbtUtils.createUUID(getUuid()));
        drive.putByte("type", (byte) getType().ordinal());
        data.put("drive", drive);
        data.putString("path", path.toString());
        data.putBoolean("protected", getFs().isReadOnly(path));
        data.putBoolean("folder", getFs().isFolder(path));
        data.putLong("size", getFs().size(path));
        return data;
    }

    private FileSystem.Response extraInfo(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Can't ex-stat file, file not found!");
        CompoundTag data = new CompoundTag();
        data.putLong("size", getFs().size(path));
        data.putLong("lastModified", getFs().lastModified(path));
        data.putLong("lastAccessed", getFs().lastAccessed(path));
        data.putLong("creationTime", getFs().creationTime(path));
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", data);
    }

    private FileSystem.Response move(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path source = Path.of(actionData.getString("source"));
        Path destination = Path.of(actionData.getString("destination"));
        if (!getFs().exists(source))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Can't move file, file not found!");
        try {
            getFs().move(source, destination);
            return FileSystem.createSuccessResponse();
        } catch (IOException e) {
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "I/O error: " + e.getMessage());
        }
    }

    private FileSystem.Response copy(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path source = Path.of(actionData.getString("source"));
        Path destination = Path.of(actionData.getString("destination"));
        if (!getFs().exists(source))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Can't copy file, file not found!");
        try {
            getFs().copy(source, destination);
            return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", info(destination));
        } catch (IOException e) {
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "I/O error: " + e.getMessage());
        }
    }

    private FileSystem.Response info(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Can't stat file, file not found!");
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", info(path));
    }

    private FileSystem.Response listDir(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Can't list directory, file not found!");
        CompoundTag data = new CompoundTag();
        ListTag list = new ListTag();
        Iterator<String> iterator = getFs().listDirectory(path);
        while (iterator.hasNext()) {
            String child = iterator.next();
            list.add(info(path.resolve(child)));
        }
        data.put("files", list);
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", data);
    }

    private FileSystem.Response readData(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Can't read file, file not found!");
        long offset = actionData.getLong("offset");
        int length = actionData.getInt("length");
        if (offset < 0 || length < 0) {
            try (InputStream read = read(path, StandardOpenOption.READ)) {
                CompoundTag data = new CompoundTag();
                data.putByteArray("data", read.readAllBytes());
                return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", data);
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);
        fs.read(path, buffer, offset);
        buffer.flip();
        CompoundTag data = new CompoundTag();
        data.putByteArray("data", buffer.array());
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", data);
    }

    private FileSystem.Response exists(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        boolean exists = getFs().exists(path);
        CompoundTag data = new CompoundTag();
        data.putBoolean("exists", exists);
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", data);
    }

    private FileSystem.@NotNull Response newFile(CompoundTag actionData, CompoundTag data) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        boolean override = actionData.getBoolean("override");
        byte[] dataBytes = data.getByteArray("data");
        if (!override && getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "File already exists");
        else if (getFS().exists(path))
            getFs().delete(path);
        createFile(path, dataBytes);
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", info(path));
    }

    private FileSystem.@NotNull Response newFolder(CompoundTag actionData, CompoundTag data) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        if (path.toString().equals("/")) {
            throw new IOException("Can't create root folder");
        }
        boolean override = actionData.getBoolean("override");
        byte[] dataBytes = data.getByteArray("data");
        if (!override && getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "File already exists");
        createDirectory(path);
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", info(path));
    }

    private FileSystem.@NotNull Response delete(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "File not found on server. Please refresh!");
        getFs().delete(path);
        return FileSystem.createSuccessResponse();
    }

    private FileSystem.@NotNull Response rename(CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        String newName = actionData.getString("new_name");
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "File not found on server. Please refresh!");
        getFs().rename(path, newName);
        return FileSystem.createSuccessResponse();
    }

    private FileSystem.@NotNull Response writeData(CompoundTag actionData, CompoundTag data) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path path = Path.of(actionData.getString("path"));
        long offset = actionData.getLong("offset");
        if (!getFs().exists(path))
            return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "Invalid directory");
        byte[] dataBytes = actionData.getByteArray("data");
        if (offset == -1) {
            try (OutputStream write = write(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                write.write(dataBytes);
            }
        } else {
            fs.write(path, offset, dataBytes);
        }
        return FileSystem.createResponse(FileSystem.Status.SUCCESSFUL, "", info(path));
    }

    private FileSystem.@NotNull Response copyOrCut(FileSystem fileSystem, Level level, CompoundTag actionData) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        Path file = Path.of(actionData.getString("source"));
        if (!getFs().exists(file))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "File not found on server. Please refresh!");
        UUID uuid = UUID.fromString(actionData.getString("destination_drive"));
        AbstractDrive drive = fileSystem.getAvailableDrives(level, true).get(uuid);
        if (drive == null)
            return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "Drive unavailable. Please refresh!");
        Path destination = Path.of(actionData.getString("destination_folder"));
        if (!getFs().exists(destination))
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Destination folder not found on server. Please refresh!");
        Path temp = destination;
        while (temp != null) {
            if (temp.equals(file))
                return FileSystem.createResponse(FileSystem.Status.FAILED, "Destination folder can't be a subfolder");
            temp = temp.getParent();
        }

        try (InputStream read = read(file, StandardOpenOption.READ)) {
            createFile(destination, read.readAllBytes());
            try (OutputStream write = write(destination, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                byte[] bytes = read.readAllBytes();
                write.write(bytes);
                write.flush();

                if (!actionData.getBoolean("cut"))
                    return FileSystem.createSuccessResponse();
                getFs().delete(file);
                return FileSystem.createSuccessResponse();
            }
        }
    }

    public abstract CompoundTag toTag();

    public abstract Type getType();

    /// Gets a folder in the file system. To get sub folders, simply use a
    /// '/' between each folder name. If the folder does not exist, it will
    /// return null.
    ///
    /// @param path the directory of the folder
    @Nullable
    @Deprecated
    public ServerFolder getFolder(String path) {
        setLastAccessed(System.currentTimeMillis());
        if (path == null) throw new IllegalArgumentException("The path can not be null");

        if (!FileSystem.PATTERN_DIRECTORY.matcher(path).matches())
            throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

        return null;
    }


    @Deprecated
    public ServerFolder getDriveStructure() {
        setLastAccessed(System.currentTimeMillis());
        return null;
    }

    @Override
    public void close() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().close();
    }

    @Override
    public InputStream read(Path path, OpenOption... options) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().read(path, options);
    }

    @Override
    public OutputStream write(Path path, OpenOption... options) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().write(path, options);
    }

    @Override
    public boolean exists(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().exists(path);
    }

    @Override
    public void flush() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().flush();
    }

    @Override
    public void createFile(Path path, byte[] data) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().createFile(path, data);
    }

    @Override
    public void createDirectory(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().createDirectory(path);
    }

    @Override
    public Iterator<String> listDirectory(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().listDirectory(of);
    }

    @Override
    public void delete(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().delete(path);
    }

    @Override
    public long size(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().size(path);
    }

    @Override
    public void rename(Path from, String name) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().rename(from, name);
    }

    @Override
    public boolean isFolder(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().isFolder(path);
    }

    @Override
    public boolean isFile(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().isFile(path);
    }

    @Override
    public boolean isSymbolicLink(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().isSymbolicLink(path);
    }

    public void setDamaged(boolean b) {
        setLastAccessed(System.currentTimeMillis());
        damaged = b;
    }

    public boolean isDamaged() {
        setLastAccessed(System.currentTimeMillis());
        return damaged;
    }

    @Override
    public boolean canRead(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().canRead(of);
    }

    @Override
    public boolean canWrite(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().canWrite(of);
    }

    @Override
    public void setReadOnly(Path of, boolean b) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().setReadOnly(of, b);
    }

    @Override
    public void setExecutable(Path of, boolean b) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        getFs().setExecutable(of, b);
    }

    @Override
    public boolean canExecute(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs().canExecute(of);
    }

    public Ext2FS getFS() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return getFs();
    }

    public Ext2FS getFs() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        if (deferred || fs == null) {
            deferred = false;
            if (fs != null) return fs;
            try {
                java.nio.file.Path resolve = UltreonDevices.getServer().getWorldPath(LevelResource.ROOT).resolve("data/devices/drives/" + uuid + ".ext2");
                if (Files.notExists(resolve)) {
                    if (Files.notExists(resolve.getParent())) Files.createDirectories(resolve.getParent());
                    setFs(Ext2FS.format(resolve, 16 * 1024 * 1024));
                    setup();
                } else setFs(Ext2FS.open(resolve));
            } catch (IOException | FileSystemException e) {
                invalid = true;
                throw new IOException("Failed to open drive, vHardware failure. Device invalid!", e);
            }
        }
        if (invalid | damaged) throw new IOException("Failed to open drive, vHardware failure. Previous error!");
        if (fs == null) throw new IOException("Failed to open drive, vHardware failure. Invalid filesystem!");
        return fs;
    }

    public void setFs(Ext2FS fs) {
        setLastAccessed(System.currentTimeMillis());
        if (this.fs != null) throw new IllegalStateException("Already set!");
        if (fs == null) throw new IllegalArgumentException("The filesystem can not be null");
        this.fs = fs;
    }

    @Override
    public @Nullable LockKey lock(String path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.lock(path);
    }

    @Override
    public void unlock(String directory) {
        setLastAccessed(System.currentTimeMillis());
        fs.unlock(directory);
    }

    @Override
    public boolean isLocked(String directory) {
        setLastAccessed(System.currentTimeMillis());
        return fs.isLocked(directory);
    }

    @Override
    public boolean isExecutable(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.isExecutable(of);
    }

    @Override
    public boolean isWritable(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.isWritable(of);
    }

    @Override
    public boolean isReadable(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.isReadable(of);
    }

    @Override
    public int getOwner(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getOwner(of);
    }

    @Override
    public int getGroup(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getGroup(of);
    }

    @Override
    public int getPermissions(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getPermissions(of);
    }

    @Override
    public void setPermissions(Path of, int mode) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setPermissions(of, mode);
    }

    @Override
    public void setOwner(Path of, int uid, int gid) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setOwner(of, uid, gid);
    }

    @Override
    public void setGroup(Path of, int gid) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setGroup(of, gid);
    }

    @Override
    public void setOwner(Path of, int uid) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setOwner(of, uid);
    }

    @Override
    public long getGeneration(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getGeneration(of);
    }

    @Override
    public void setGeneration(Path of, long generation) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setGeneration(of, generation);
    }

    @Override
    public boolean isReadOnly(Path of) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.isReadOnly(of);
    }

    @Override
    public long lastModified(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.lastModified(path);
    }

    @Override
    public long lastAccessed(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.lastAccessed(path);
    }

    @Override
    public long creationTime(Path path) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.creationTime(path);
    }

    @Override
    public void setLastAccessed(Path path, long time) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setLastAccessed(path, time);
    }

    @Override
    public void setLastModified(Path path, long time) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setLastModified(path, time);
    }

    @Override
    public void setCreationTime(Path path, long time) throws IOException {
        setLastAccessed(System.currentTimeMillis());
        fs.setCreationTime(path, time);
    }

    @Override
    public long getTotalSpace() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getTotalSpace();
    }

    @Override
    public long getUsableSpace() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getUsableSpace();
    }

    @Override
    public long getFreeSpace() throws IOException {
        setLastAccessed(System.currentTimeMillis());
        return fs.getFreeSpace();
    }

    @Override
    public void move(Path source, Path destination) throws IOException {
        fs.move(source, destination);
        setLastAccessed(System.currentTimeMillis());
    }

    @Override
    public void copy(Path source, Path destination) throws IOException {
        fs.copy(source, destination);
        setLastAccessed(System.currentTimeMillis());
    }

    @Override
    public void write(Path path, long offset, byte[] dataBytes) throws IOException {
        fs.write(path, offset, dataBytes);
        setLastAccessed(System.currentTimeMillis());
    }

    @Override
    public void write(Path path, byte[] dataBytes) throws IOException {
        fs.write(path, dataBytes);
        setLastAccessed(System.currentTimeMillis());
    }

    @Override
    public void truncate(Path path, long size) throws IOException {
        fs.truncate(path, size);
        setLastAccessed(System.currentTimeMillis());
    }

    @Override
    public void read(Path path, ByteBuffer buffer, long offset) throws IOException {
        fs.read(path, buffer, offset);
        setLastAccessed(System.currentTimeMillis());
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
        DriveManager.updateLastAccessed(uuid, lastAccessed);
    }

    public enum Type {
        INTERNAL, EXTERNAL, NETWORK
    }
}
