package com.ultreon.devices.core.io.drive;

import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFile;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * @author MrCrayfish
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class AbstractDrive {
    protected String name;
    protected UUID uuid;
    protected ServerFolder root;

    AbstractDrive() {
    }

    AbstractDrive(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.root = createProtectedFolder("Root");
    }

    private static ServerFolder createProtectedFolder(String name) {
        try {
            Constructor<ServerFolder> constructor = ServerFolder.class.getDeclaredConstructor(String.class, boolean.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, true);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ServerFolder getRoot(Level level) {
        return root;
    }

    public FileSystem.Response handleFileAction(FileSystem fileSystem, FileAction action, Level level) {
        CompoundTag actionData = action.getData();
        ServerFolder folder = getFolder(actionData.getString("directory"));
        if (folder != null) {
            CompoundTag data = actionData.getCompound("data");
            switch (action.getType()) {
                case NEW:
                    if (data.contains("files", Tag.TAG_COMPOUND)) {
                        return folder.add(ServerFolder.fromTag(actionData.getString("file_name"), data), actionData.getBoolean("override"));
                    }
                    return folder.add(ServerFile.fromTag(actionData.getString("file_name"), data), data.getBoolean("override"));
                case DELETE:
                    return folder.delete(actionData.getString("file_name"));
                case RENAME:
                    ServerFile file = folder.getFile(actionData.getString("file_name"));
                    if (file != null) {
                        return file.rename(actionData.getString("new_file_name"));
                    }
                    return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "File not found on server. Please refresh!");
                case DATA:
                    file = folder.getFile(actionData.getString("file_name"));
                    if (file != null) {
                        return file.setData(actionData.getCompound("data"));
                    }
                    return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "File not found on server. Please refresh!");
                case COPY_CUT:
                    file = folder.getFile(actionData.getString("file_name"));
                    if (file != null) {
                        UUID uuid = UUID.fromString(actionData.getString("destination_drive"));
                        AbstractDrive drive = fileSystem.getAvailableDrives(level, true).get(uuid);
                        if (drive != null) {
                            ServerFolder destination = drive.getFolder(actionData.getString("destination_folder"));
                            if (destination != null) {
                                ServerFolder temp = destination;
                                while (temp != null) {
                                    if (temp == file)
                                        return FileSystem.createResponse(FileSystem.Status.FAILED, "Destination folder can't be a subfolder");
                                    temp = temp.getParent();
                                }

                                FileSystem.Response response = destination.add(file.copy(), actionData.getBoolean("override"));
                                if (response.getStatus() != FileSystem.Status.SUCCESSFUL) {
                                    return response;
                                }
                                if (actionData.getBoolean("cut")) {
                                    return file.delete();
                                }
                                return FileSystem.createSuccessResponse();
                            }
                            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Destination folder not found on server. Please refresh!");
                        }
                        return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "Drive unavailable. Please refresh!");
                    }
                    return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "File not found on server. Please refresh!");
            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "Invalid directory");
    }

    public abstract CompoundTag toTag();

    public abstract Type getType();

    /**
     * Gets a folder in the file system. To get sub folders, simply use a
     * '/' between each folder name. If the folder does not exist, it will
     * return null.
     *
     * @param path the directory of the folder
     */
    @Nullable
    public ServerFolder getFolder(String path) {
        if (path == null) throw new IllegalArgumentException("The path can not be null");

        if (!FileSystem.PATTERN_DIRECTORY.matcher(path).matches())
            throw new IllegalArgumentException("The path \"" + path + "\" does not follow the correct format");

        if (path.equals("/")) return root;

        ServerFolder prev = root;
        String[] folders = path.split("/");
        if (folders.length > 0 && folders.length <= 10) {
            for (int i = 1; i < folders.length; i++) {
                ServerFolder temp = prev.getFolder(folders[i]);
                if (temp == null) return null;
                prev = temp;
            }
            return prev;
        }
        return null;
    }

    public ServerFolder getDriveStructure() {
        return root.copyStructure();
    }

    public enum Type {
        INTERNAL, EXTERNAL, NETWORK
    }
}
