package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.ServerFile;
import dev.ultreon.devices.core.io.ServerFolder;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class TaskVefiFileOperation extends Task {
    private static final String VEFI_BASE = "/Application Data/MineOS";

    private BlockPos pos;
    private String operation;
    private String filePath;
    private String content;

    private boolean success;
    private String resultContent;
    private List<String> resultFiles;
    private boolean resultExists;

    public TaskVefiFileOperation() {
        super("vefi_file_operation");
    }

    public TaskVefiFileOperation(BlockPos pos, String operation, String filePath, String content) {
        this();
        this.pos = pos;
        this.operation = operation;
        this.filePath = filePath;
        this.content = content;
    }

    public boolean getResultSuccess() {
        return success;
    }

    public String getResultContent() {
        return resultContent;
    }

    public List<String> getResultFiles() {
        return resultFiles;
    }

    public boolean getResultExists() {
        return resultExists;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.putString("operation", operation);
        tag.putString("file_path", filePath);
        if (content != null) {
            tag.putString("content", content);
        }
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockPos bePos = BlockPos.of(tag.getLong("pos"));
        BlockEntity tileEntity = level.getChunkAt(bePos).getBlockEntity(bePos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (!(tileEntity instanceof ComputerBlockEntity laptop)) return;

        FileSystem fileSystem = laptop.getFileSystem();
        AbstractDrive drive = fileSystem.getMainDrive();
        if (drive == null) return;

        String op = tag.getString("operation");
        String path = tag.getString("file_path");
        String data = tag.contains("content") ? tag.getString("content") : null;

        String fullPath = VEFI_BASE + (path.startsWith("/") ? "" : "/") + path;

        switch (op) {
            case "read" -> {
                resultContent = readFileContent(drive, fullPath);
                success = resultContent != null;
            }
            case "write" -> success = writeFileContent(drive, fullPath, data);
            case "list" -> {
                resultFiles = listFolder(drive, fullPath);
                success = resultFiles != null;
            }
            case "mkdir" -> success = makeDirectory(drive, fullPath);
            case "exists" -> {
                resultExists = fileExists(drive, fullPath);
                success = true;
            }
            case "delete" -> success = deleteFile(drive, fullPath);
        }

        if (success) {
            laptop.setChanged();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        tag.putBoolean("success", success);
        if (resultContent != null) {
            tag.putString("content", resultContent);
        }
        if (resultFiles != null) {
            ListTag list = new ListTag();
            for (String name : resultFiles) {
                CompoundTag entry = new CompoundTag();
                entry.putString("name", name);
                list.add(entry);
            }
            tag.put("files", list);
        }
        tag.putBoolean("exists", resultExists);
    }

    @Override
    public void processResponse(CompoundTag tag) {
    }

    private static String readFileContent(AbstractDrive drive, String fullPath) {
        ServerFolder parent = drive.getFolder(getParentPath(fullPath));
        if (parent == null) return null;

        ServerFile file = parent.getFile(getFileName(fullPath));
        if (file == null) return null;

        CompoundTag data = file.getData();
        return data != null && data.contains("content") ? data.getString("content") : "";
    }

    private static boolean writeFileContent(AbstractDrive drive, String fullPath, String content) {
        String parentPath = getParentPath(fullPath);
        ServerFolder parent = drive.getFolder(parentPath);
        if (parent == null) {
            makeDirectory(drive, parentPath);
            parent = drive.getFolder(parentPath);
            if (parent == null) return false;
        }

        String fileName = getFileName(fullPath);

        ServerFile existing = parent.getFile(fileName);
        CompoundTag data = new CompoundTag();
        data.putString("content", content);

        if (existing != null) {
            FileSystem.Response response = existing.setData(data);
            return response.getStatus() == FileSystem.Status.SUCCESSFUL;
        } else {
            ServerFile file = new ServerFile(fileName, "text", data);
            FileSystem.Response response = parent.add(file, true);
            return response.getStatus() == FileSystem.Status.SUCCESSFUL;
        }
    }

    private static List<String> listFolder(AbstractDrive drive, String fullPath) {
        ServerFolder folder = drive.getFolder(fullPath);
        if (folder == null) return null;

        List<String> names = new ArrayList<>();
        for (ServerFile file : folder.getFiles()) {
            names.add(file.getName());
        }
        return names;
    }

    private static boolean makeDirectory(AbstractDrive drive, String fullPath) {
        if (fileExists(drive, fullPath)) return true;

        String parentPath = getParentPath(fullPath);
        String dirName = getFileName(fullPath);

        ServerFolder parent = drive.getFolder(parentPath);
        if (parent == null) {
            makeDirectory(drive, parentPath);
            parent = drive.getFolder(parentPath);
            if (parent == null) return false;
        }

        ServerFolder folder = new ServerFolder(dirName);
        FileSystem.Response response = parent.add(folder, false);
        return response.getStatus() == FileSystem.Status.SUCCESSFUL || response.getStatus() == FileSystem.Status.FILE_EXISTS;
    }

    private static boolean fileExists(AbstractDrive drive, String fullPath) {
        String parentPath = getParentPath(fullPath);
        String fileName = getFileName(fullPath);

        if (parentPath.equals(fullPath) || fileName.isEmpty()) {
            return drive.getFolder(fullPath) != null;
        }

        ServerFolder parent = drive.getFolder(parentPath);
        if (parent == null) return false;

        if (parent.getFile(fileName) != null) return true;

        return parent.getFolder(fileName) != null;
    }

    private static boolean deleteFile(AbstractDrive drive, String fullPath) {
        String parentPath = getParentPath(fullPath);
        String fileName = getFileName(fullPath);

        ServerFolder parent = drive.getFolder(parentPath);
        if (parent == null) return false;

        FileSystem.Response response = parent.delete(fileName);
        return response.getStatus() == FileSystem.Status.SUCCESSFUL;
    }

    private static String getParentPath(String fullPath) {
        int lastSlash = fullPath.lastIndexOf('/');
        if (lastSlash <= 0) return "/";
        return fullPath.substring(0, lastSlash);
    }

    private static String getFileName(String fullPath) {
        int lastSlash = fullPath.lastIndexOf('/');
        if (lastSlash < 0) return fullPath;
        return fullPath.substring(lastSlash + 1);
    }
}
