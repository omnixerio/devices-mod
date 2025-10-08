package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.DataPath;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/// @author MrCrayfish
public class TaskGetFiles extends Task {
    private String uuid;
    private Path path;
    private BlockPos pos;

    private List<String> files;
    private String error;

    public TaskGetFiles() {
        super();
    }

    public TaskGetFiles(DataPath path, BlockPos pos) {
        this();
        this.uuid = path.drive().toString();
        this.path = path.path();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putString("uuid", uuid);
        tag.putString("path", path.toString());
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (!(tileEntity instanceof ComputerBlockEntity laptop)) {
            return;
        }
        FileSystem fileSystem = laptop.getFileSystem();
        UUID uuid = UUID.fromString(tag.getString("uuid"));
        AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
        if (serverDrive == null) {
            return;
        }
        String path = tag.getString("path");
        try {
            Iterator<String> stringIterator = serverDrive.listDirectory(Path.of(path));
            this.files = new ArrayList<>();
            while (stringIterator.hasNext()) {
                String name = stringIterator.next();
                this.files.add(name);
            }
            this.setSuccessful();
        } catch (IOException e) {
            this.error = e.getMessage();
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        if (this.error != null) {
            tag.putString("error", error);
            return;
        }
        if (this.files != null) {
            ListTag list = new ListTag();
            list.addAll(this.files.stream().map(StringTag::valueOf).toList());
            tag.put("files", list);
        }
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {

    }
}
