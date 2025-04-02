package com.ultreon.devices.programs.system.component;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.FSResponse;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.core.DataPath;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.ultreon.devices.core.io.FileSystem.request;

/// A file or folder in the file browser
///
/// @author [XyperCode](https : / / github.com / XyperCode)
public final class FileInfo {
    public static final Comparator<FileInfo> SORT_BY_NAME = Comparator.comparing(a -> a.path.getFileName().toString());
    public static final Comparator<FileInfo> SORT_BY_TYPE = Comparator.comparing(a -> a.type);
    private final long dev;
    private final int nlink;
    private final int uid;
    private final int gid;
    private final short mode;
    private Path path;
    private final DataPath dataPath;
    private final FSEntryType type;
    private final boolean protectedFile;
    private Drive drive;
    private boolean invalid;
    private long size;
    private long inode;
    private long ino;
    private long lastModified;
    private long lastAccessed;
    private long creationTime;
    private int fileKey;
    private boolean isSymbolicLink;

    /// Creates a new file info object
    public FileInfo(Drive drive, Path path, FSEntryType type, long size, long ino, long lastModified, long lastAccessed, long creationTime, long inode, long dev, int nlink, int uid, int gid, boolean protectedFile, short mode, boolean isSymbolicLink) {
        this.path = path;
        this.type = type;
        this.size = size;
        this.ino = ino;
        this.inode = inode;
        this.lastModified = lastModified;
        this.lastAccessed = lastAccessed;
        this.creationTime = creationTime;
        this.drive = drive;
        this.dev = dev;
        this.nlink = nlink;
        this.uid = uid;
        this.gid = gid;
        this.mode = mode;
        this.isSymbolicLink = isSymbolicLink;
        this.dataPath = new DataPath(drive, path);
        this.protectedFile = protectedFile;
    }

    public static FileInfo fromTag(CompoundTag compoundTag) {
        if (!compoundTag.contains("drive")) throw new IllegalArgumentException("Missing drive");
        return new FileInfo(
                new Drive(compoundTag.getCompound("drive")),
                Path.of(compoundTag.getString("path")),
                compoundTag.getBoolean("folder") ? FSEntryType.FOLDER : FSEntryType.FILE,
                compoundTag.getLong("size"),
                compoundTag.getLong("ino"),
                compoundTag.getLong("lastModified"),
                compoundTag.getLong("lastAccessed"),
                compoundTag.getLong("creationTime"),
                compoundTag.getLong("inode"),
                compoundTag.getLong("dev"),
                compoundTag.getByte("nlink"),
                compoundTag.getInt("uid"),
                compoundTag.getInt("gid"),
                compoundTag.getBoolean("protected"),
                compoundTag.getShort("mode"),
                compoundTag.getBoolean("isSymbolicLink")
        );
    }

    public CompoundTag toTag() {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag driveTag = new CompoundTag();
        if (drive == null) {
            driveTag.putString("name", "Unknown");
            driveTag.put("uuid", NbtUtils.createUUID(dataPath.drive()));
            driveTag.putString("type", Drive.Type.UNKNOWN.toString());
        } else {
            driveTag.putString("name", drive.getName());
            driveTag.putUUID("uuid", drive.getUUID());
            driveTag.putString("type", drive.getType().toString());
        }
        compoundTag.put("drive", driveTag);
        compoundTag.putString("path", path.toString());
        compoundTag.putString("type", type.toString());
        compoundTag.putLong("size", size);
        compoundTag.putBoolean("protected", protectedFile);
        return compoundTag;
    }

    public boolean isFolder() {
        return type == FSEntryType.FOLDER;
    }

    public boolean isFile() {
        return type == FSEntryType.FILE;
    }

    public String getOpeningApp() {
        Devices.LOGGER.warn("getOpeningApp is not implemented");
        return null;
    }

    public boolean isExecutable() {
        return dataPath.path().endsWith(".apx");
    }

    public @NotNull String getName() {
        return path.getFileName().toString();
    }

    public boolean isProtected() {
        return protectedFile;
    }

    public void delete(Consumer<FSResponse<Unit>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeDelete(path), callback);
    }

    public void createFile(String name, boolean override, Consumer<FSResponse<FileInfo>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeNewFile(path, name, override), callback);
    }

    public void loadExtraInfo(Callback<Unit> callback) {
        request(dataPath.drive(), FileAction.Factory.makeExtraInfo(path), (response) -> {
            if (response.success()) {
                CompoundTag data = response.data();
                lastModified = data.getLong("lastModified");
                lastAccessed = data.getLong("lastAccessed");
                creationTime = data.getLong("creationTime");
            }

            if (callback != null) {
                callback.execute(Unit.INSTANCE, response.success());
            }
        });
    }

    public DataPath getDataPath() {
        return dataPath;
    }

    public Path getPath() {
        return path;
    }

    public FSEntryType getType() {
        return type;
    }

    public boolean protectedFile() {
        return protectedFile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FileInfo) obj;
        return Objects.equals(this.path, that.path) &&
               Objects.equals(this.type, that.type) &&
               this.protectedFile == that.protectedFile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, type, protectedFile);
    }

    @Override
    public String toString() {
        return "FileInfo[" +
               "path=" + path + ", " +
               "type=" + type + ", " +
               "protectedFile=" + protectedFile + ']';
    }

    public Drive getDrive() {
        return drive;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void info(Consumer<FSResponse<FileInfo>> fileInfoCallback) {
        drive.info(path, fileInfoCallback);
    }

    public void read(Consumer<FSResponse<byte[]>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeRead(path), callback);
    }

    public void write(byte[] data, Consumer<FSResponse<FileInfo>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeWrite(path, -1, data), callback);
    }

    public void rename(String name, Consumer<FSResponse<Unit>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeRename(path, name), (response) -> {
            if (response.success()) {
                this.path = path.resolveSibling(name);
            }

            if (callback != null) {
                callback.accept(response);
            }
        });
    }

    public void moveTo(Path path, boolean override, Consumer<FSResponse<Unit>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeMove(path, this.path, override), response -> {
            if (response.success()) {
                this.path = path;
            }
            callback.accept(response);
        });
    }

    public void createDirectory(String name, Consumer<FSResponse<FileInfo>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeNewFolder(path, name, false), callback);
    }

    public void copyTo(Path currentFolder, boolean override, Consumer<FSResponse<FileInfo>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeCopy(currentFolder, path, override), callback);
    }

    public void copyTo(Drive drive, Path currentFolder, boolean override, Consumer<FSResponse<FileInfo>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeRead(path), response -> request(drive.getUUID(), FileAction.Factory.makeWrite(currentFolder, -1, response.data()), callback));
    }

    public void list(Consumer<FSResponse<List<FileInfo>>> callback) {
        request(dataPath.drive(), FileAction.Factory.makeList(path), callback);
    }

    public void child(String name, Consumer<FSResponse<FileInfo>> callback) {
        drive.info(path.resolve(name), callback);
    }

    public FileInfo withExtension(String name) {
        FileInfo fileInfo = new FileInfo(drive, path.toString().endsWith(name) ? path : path.resolveSibling(getName() + "." + name), type, size, ino, lastModified, lastAccessed, creationTime, inode, dev, nlink, uid, gid, protectedFile, mode, isSymbolicLink);
        fileInfo.size = size;
        fileInfo.lastAccessed = lastAccessed;
        fileInfo.lastModified = lastModified;
        fileInfo.creationTime = creationTime;
        return fileInfo;
    }

    public long getSize() {
        return size;
    }

    public long getIno() {
        return ino;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getDev() {
        return dev;
    }

    public long getInode() {
        return inode;
    }

    public int getFileKey() {
        return fileKey;
    }

    public int getNlink() {
        return nlink;
    }

    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    public int getGid() {
        return gid;
    }

    public int getUid() {
        return uid;
    }

    public int getMode() {
        return mode;
    }
}
