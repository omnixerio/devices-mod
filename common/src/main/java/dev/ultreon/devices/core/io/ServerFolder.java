package dev.ultreon.devices.core.io;

import dev.ultreon.devices.core.io.FileSystem.Status;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author MrCrayfish
 */
public final class ServerFolder extends ServerFile {
    private List<ServerFile> files = new ArrayList<>();
    private final Object interrupt = new Object();

    public ServerFolder(String name) {
        this(name, false);
    }

    private ServerFolder(String name, boolean protect) {
        this.name = name;
        this.protect = protect;
    }

    private ServerFolder(String name, boolean protect, CompoundTag tag) {
        this.name = name;
        this.protect = protect;
        this.creationTime = tag.getLong("creationTime");
        this.lastModified = tag.getLong("lastModified");
        this.lastAccessed = tag.getLong("lastAccessed");
    }

    public static ServerFolder fromTag(String name, CompoundTag folderTag) {
        ServerFolder folder = new ServerFolder(name, false, folderTag);

        if (folderTag.contains("protected", Tag.TAG_BYTE)) folder.protect = folderTag.getBoolean("protected");

        CompoundTag fileList = folderTag.getCompound("files");
        for (String fileName : fileList.getAllKeys()) {
            CompoundTag fileTag = fileList.getCompound(fileName);
            if (fileTag.contains("files")) {
                folder.add(ServerFolder.fromTag(fileName, fileTag), false);
            } else {
                folder.add(ServerFile.fromTag(fileName, fileTag), false);
            }
        }
        return folder;
    }

    public FileSystem.Response add(ServerFile file, boolean override) {
        if (file == null) return FileSystem.createResponse(Status.FILE_INVALID, "Illegal file");

        if (!FileSystem.PATTERN_FILE_NAME.matcher(file.getName()).matches())
            return FileSystem.createResponse(Status.FILE_INVALID_NAME, "Invalid file name");

        synchronized (interrupt) {
            ServerFile child = getFile(file.name);
            if (child != null) {
                if (!override)
                    return FileSystem.createResponse(Status.FILE_EXISTS, "A file with that name already exists");
                if (child.isProtected())
                    return FileSystem.createResponse(Status.FILE_IS_PROTECTED, "Unable to override protected files");
                files.remove(child);
            }

            files.add(file);
            file.parent = this;
            file.onCreate();
        }
        return FileSystem.createSuccessResponse();
    }

    public FileSystem.Response delete(String name) {
        return delete(getFile(name));
    }

    public FileSystem.Response delete(ServerFile file) {
        if (file == null) return FileSystem.createResponse(Status.FILE_INVALID, "Illegal file");

        synchronized (interrupt) {
            if (!files.contains(file))
                return FileSystem.createResponse(Status.FILE_INVALID, "The file does not exist in this folder");

            if (file.isProtected())
                return FileSystem.createResponse(Status.FILE_IS_PROTECTED, "Cannot delete protected files");

            file.parent = null;
            files.remove(file);
        }
        return FileSystem.createSuccessResponse();
    }

    public boolean hasFile(String name) {
        return files.stream().anyMatch(file -> file.name.equalsIgnoreCase(name));
    }

    @Nullable
    public ServerFile getFile(String name) {
        return files.stream().filter(file -> file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean hasFolder(String name) {
        return files.stream().anyMatch(file -> file.isFolder() && file.name.equalsIgnoreCase(name));
    }

    @Nullable
    public ServerFolder getFolder(String name) {
        return (ServerFolder) files.stream().filter(file -> file.isFolder() && file.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<ServerFile> getFiles() {
        return files;
    }

    public void setFiles(List<ServerFile> files) {
        this.files = files;
    }

    public List<ServerFile> search(Predicate<ServerFile> conditions, boolean includeSubServerFolders) {
        List<ServerFile> found = NonNullList.create();
        search(found, conditions, includeSubServerFolders);
        return found;
    }

    private void search(List<ServerFile> results, Predicate<ServerFile> conditions, boolean includeSubServerFolders) {
        files.forEach(file -> {
            if (file.isFolder()) {
                if (includeSubServerFolders) {
                    ((ServerFolder) file).search(results, conditions, true);
                }
            } else if (conditions.test(file)) {
                results.add(file);
            }
        });
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag folderTag = new CompoundTag();

        CompoundTag fileList = new CompoundTag();
        files.forEach(file -> fileList.put(file.getName(), file.toTag()));
        folderTag.put("files", fileList);

        if (protect) folderTag.putBoolean("protected", true);

        folderTag.putLong("creationTime", creationTime);
        folderTag.putLong("lastModified", lastModified);
        folderTag.putLong("lastAccessed", lastAccessed);

        return folderTag;
    }

    @Override
    public FileSystem.Response setData(@NotNull CompoundTag data) {
        return FileSystem.createResponse(Status.FILE_INVALID_DATA, "Data can not be set to a folder");
    }

    @Override
    public ServerFile copy() {
        ServerFolder folder = new ServerFolder(name);
        files.forEach(f -> {
            ServerFile copy = f.copy();
            copy.protect = false;
            folder.add(copy, false);
        });
        return folder;
    }

    public ServerFolder copyStructure() {
        ServerFolder folder = new ServerFolder(name, protect);
        files.forEach(f -> {
            if (f.isFolder()) {
                folder.add(((ServerFolder) f).copyStructure(), false);
            }
        });
        return folder;
    }

    /*public void.json print(int startingDepth)
    {
        String indent = "";
        for(int i = 0; i < startingDepth; i++)
        {
            indent += "  ";
        }
        MrCrayfishDeviceMod.getLogger().info(indent + "⌊ " + name);
        for(ServerFile file : files)
        {
            if(file.isFolder())
            {
                ((ServerFolder) file).print(startingDepth + 1);
            }
            else
            {
                MrCrayfishDeviceMod.getLogger().info(indent + "  ⌊ " + file.name);
            }
        }
    }*/
}
