package com.ultreon.devices.api.io;

import com.ultreon.devices.core.DataPath;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import com.ultreon.devices.programs.system.component.FileInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.ultreon.devices.core.io.FileSystem.request;

public class Drive {
    public static final int BUFFER_SIZE = 1024;
    private final String name;
    private final UUID uuid;
    private final Type type;

    private boolean synced = false;
    @Deprecated
    private Folder root;

    public Drive(CompoundTag driveTag) {
        this.name = driveTag.getString("name");
        if (driveTag.contains("uuid")) {
            Tag uuid1 = driveTag.get("uuid");
            this.uuid = NbtUtils.loadUUID(uuid1);
        } else throw new IllegalArgumentException("Drive must have a uuid");
        this.type = Type.fromString(driveTag.getString("type"));
    }

    public Drive(String name, UUID uuid, Type type) {
        this.name = name;
        this.uuid = uuid;
        this.type = type;
    }

    /// Gets the name of the Drive.
    ///
    /// @return the drive name
    public String getName() {
        return name;
    }

    /// Gets the UUID of the Drive.
    ///
    /// @return the drive uuid
    public UUID getUUID() {
        return uuid;
    }

    /// Gets the [Type] of the Drive. This is either internal, external or network. Used for
    /// determining the icon.
    ///
    /// @return the drive type
    public Type getType() {
        return type;
    }

    /// Gets the root [Folder] of this Drive
    ///
    /// @return the root folder
    @Deprecated
    public Folder getRoot() {
        return null;
    }

    /// Do not use! Sync the drive structure to a folder
    ///
    /// @param root the root folder of this drive
    @Deprecated
    public void syncRoot(Folder root) {
        if (!synced) {
            this.root = root;
            root.setDrive(this);
            root.validate();
            synced = true;
        }
    }

    /// Do not use! Checks if the drive structure is synced
    ///
    /// @return is drive structure synced
    @Deprecated
    public boolean isSynced() {
        return synced;
    }

    /// Gets a folder in the file system. To get sub folders, simply use a
    /// '/' between each folder name. If the folder does not exist, it will
    /// return null.
    ///
    /// @param path the directory of the folder
    @Nullable
    @Deprecated
    public final Folder getFolder(String path) {
        if (path == null)
            throw new IllegalArgumentException("The path can not be null");

        if (!FileSystem.PATTERN_DIRECTORY.matcher(path).matches())
            throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

        if (path.equals("/"))
            return root;

        Folder prev = root;
        String[] folders = path.split("/");
        if (folders.length > 0 && folders.length <= 10) {
            for (int i = 1; i < folders.length; i++) {
                Folder temp = prev.getFolder(folders[i]);
                if (temp == null) return null;
                prev = temp;
            }
            return prev;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public void exists(Path dirApplicationData, Consumer<FSResponse<Boolean>> callback) {
        request(uuid, FileAction.Factory.makeExists(dirApplicationData), callback);
    }

    public void info(Path path, Consumer<FSResponse<FileInfo>> callback) {
        request(uuid, FileAction.Factory.makeInfo(path), callback);
    }

    public void list(Path currentFolder, Consumer<FSResponse<List<FileInfo>>> callback) {
        request(uuid, FileAction.Factory.makeList(currentFolder), callback);
    }

    public void delete(Path path, Consumer<FSResponse<Unit>> callback) {
        request(uuid, FileAction.Factory.makeDelete(path), callback);
    }

    public void createFile(Path currentFolder, String name, Consumer<FSResponse<FileInfo>> callback) {
        request(uuid, FileAction.Factory.makeNewFile(currentFolder, name, false), callback);
    }

    public void createFile(Path currentFolder, String name, boolean override, Consumer<FSResponse<FileInfo>> callback) {
        request(uuid, FileAction.Factory.makeNewFile(currentFolder, name, override), callback);
    }

    public void createDirectory(Path currentFolder, String name, Consumer<FSResponse<FileInfo>> callback) {
        request(uuid, FileAction.Factory.makeNewFolder(currentFolder, name, false), callback);
    }

    public void copy(Path currentFolder, Path path, boolean override, Consumer<FSResponse<FileInfo>> callback) {
        request(uuid, FileAction.Factory.makeCopy(currentFolder, path, override), callback);
    }

    public void move(Path source, Path destination, boolean override, Consumer<FSResponse<Unit>> callback) {
        request(uuid, FileAction.Factory.makeMove(source, destination, override), callback);
    }

    public void rename(Path path, String name, Consumer<FSResponse<Unit>> callback) {
        request(uuid, FileAction.Factory.makeRename(path, name), callback);
    }

    public void write(Path path, byte[] data, Consumer<FSResponse<FileInfo>> callback) {
        if (data.length < BUFFER_SIZE) request(uuid, FileAction.Factory.makeWrite(path, -1, data), callback);
        else if (data.length < 4 * 1024 * 1024) writeLarge(path, data, callback);
        else callback.accept(new FSResponse<>(false, FileSystem.Status.TOO_LARGE, null, "File too large"));
    }

    public void read(Path path, Consumer<FSResponse<byte[]>> callback) {
        info(path, (info) -> {
            if (!info.success()) {
                callback.accept(new FSResponse<>(false, FileSystem.Status.FILE_DOES_NOT_EXIST, null, "File does not exist"));
                return;
            }

            if (info.data().getSize() >= BUFFER_SIZE) {
                readLarge(path, info.data().getSize(), callback);
            } else {
                request(uuid, FileAction.Factory.makeRead(path), callback);
            }
        });
    }

    private void readLarge(Path path, long size, Consumer<FSResponse<byte[]>> callback) {
        byte[] output = new byte[(int) size];
        int offset = 0;
        int length = (int) Math.min(BUFFER_SIZE, size - offset);
        var ref = new ReaderCallback(callback, output, offset, length, path, size);
        ref.requestRead(0, BUFFER_SIZE);
    }

    public DataPath getRootDirectory() {
        return new DataPath(uuid, Path.of("/"));
    }

    public DataPath getDirectory(Path path) {
        return new DataPath(uuid, path);
    }

    public void open(Consumer<FSResponse<DriveRoot>> callback) {
    }

    public void createDirectories(Path path, Consumer<FSResponse<FileInfo>> o) {
        request(uuid, FileAction.Factory.makeNewFolders(path), o);
    }

    private void writeLarge(Path path, byte[] data, Consumer<FSResponse<FileInfo>> callback) {
        byte[] buffer = new byte[BUFFER_SIZE];
        AtomicInteger offset = new AtomicInteger(0);
        int length = Math.min(BUFFER_SIZE, data.length - offset.get());
        System.arraycopy(data, 0, buffer, 0, length);
        var ref = new Object() {
            void fsResponseConsumer(FSResponse<FileInfo> r) {
                if (!r.success()) {
                    callback.accept(r);
                    return;
                }

                if (offset.get() + length == data.length) {
                    callback.accept(r);
                    return;
                }

                if (!writeNext(path, data, offset.addAndGet(length), this::fsResponseConsumer)) {
                    info(path, callback);
                }
            }
        };
        request(uuid, FileAction.Factory.makeWrite(path, offset.get(), buffer), ref::fsResponseConsumer);
    }

    private boolean writeNext(Path path, byte[] data, int offset, Consumer<FSResponse<FileInfo>> callback) {
        int length = Math.min(BUFFER_SIZE, data.length - offset);
        if (length <= 0) {
            return false;
        }
        byte[] buffer = new byte[length];
        System.arraycopy(data, offset, buffer, 0, length);
        request(uuid, FileAction.Factory.makeWrite(path, offset, buffer), callback);
        return true;
    }

    public enum Type {
        INTERNAL, EXTERNAL, NETWORK, UNKNOWN;

        public static Type fromString(String type) {
            for (Type t : values()) {
                if (t.toString().equals(type)) {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }

    private class ReaderCallback {
        private final Consumer<FSResponse<byte[]>> callback;
        private final byte[] output;
        private final int length;
        private final Path path;
        private final long size;
        private int offset;

        public ReaderCallback(Consumer<FSResponse<byte[]>> callback, byte[] output, int offset, int length, Path path, long size) {
            this.callback = callback;
            this.output = output;
            this.offset = offset;
            this.length = length;
            this.path = path;
            this.size = size;
        }

        void fsResponseConsumer(FSResponse<byte[]> r) {
            if (!r.success()) {
                callback.accept(r);
                return;
            }

            int len = r.data().length;
            System.arraycopy(r.data(), 0, output, offset, len);

            if (offset + len == size) {
                callback.accept(new FSResponse<>(true, FileSystem.Status.SUCCESSFUL, output, ""));
                return;
            }

            requestRead(offset + len, BUFFER_SIZE);
        }

        private void requestRead(long offset, int length) {
            this.offset = (int) offset;
            request(uuid, FileAction.Factory.makeRead(path, offset, (int) Math.min(length, size - offset)), this::fsResponseConsumer);
        }
    }
}
