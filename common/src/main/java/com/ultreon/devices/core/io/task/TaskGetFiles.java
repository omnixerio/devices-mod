package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Folder;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFile;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author MrCrayfish
 */
public class TaskGetFiles extends Task {
    private String uuid;
    private String path;
    private BlockPos pos;

    private List<ServerFile> files;

    public TaskGetFiles() {
        super("get_files");
    }

    public TaskGetFiles(Folder folder, BlockPos pos) {
        this();
        this.uuid = folder.getDrive().getUUID().toString();
        this.path = folder.getPath();
        this.pos = pos;
    }

    protected static String compileDirectory(ServerFile file) {
        if (file.getParent() == null || file.getParent().getParent() == null) return "/";

        StringBuilder builder = new StringBuilder();
        ServerFolder parent = file.getParent();
        while (parent != null) {
            builder.insert(0, "/" + parent.getName());
            if (parent.getParent() != null) {
                return builder.toString();
            }
            parent = parent.getParent();
        }
        return builder.toString();
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putString("uuid", uuid);
        tag.putString("path", path);
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            UUID uuid = UUID.fromString(tag.getString("uuid"));
            AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
            if (serverDrive != null) {
                ServerFolder found = serverDrive.getFolder(tag.getString("path"));
                if (found != null) {
                    this.files = found.getFiles().stream().filter(f -> !f.isFolder()).collect(Collectors.toList());
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.files != null) {
            ListTag list = new ListTag();
            this.files.forEach(f -> {
                CompoundTag fileTag = new CompoundTag();
                fileTag.putString("file_name", f.getName());
                fileTag.put("data", f.toTag());
                list.add(fileTag);
            });
            tag.put("files", list);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
