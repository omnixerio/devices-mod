package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/// @author MrCrayfish
public class TaskListDirectory extends Task {
    private UUID uuid;
    private BlockPos pos;
    private String path;

    private AbstractDrive drive;
    private String error;
    private ListTag list;
    private List<String> files;

    public TaskListDirectory() {
        super("list_directory");
    }

    public TaskListDirectory(Drive drive, @Nullable BlockPos pos, Path path) {
        this();
        this.uuid = drive.getUUID();
        this.pos = pos;
        this.path = path.toString();
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.put("uuid", NbtUtils.createUUID(uuid));
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockPos pos1 = BlockPos.of(tag.getLong("pos"));

        Devices.getServer().submit(() -> {
            BlockEntity tileEntity = level.getBlockEntity(pos1);
            if (tileEntity instanceof ComputerBlockEntity laptop) {
                FileSystem fileSystem = laptop.getFileSystem();
                UUID uuid = UUID.fromString(tag.getString("uuid"));
                AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
                if (serverDrive != null) {
                    Iterator<String> stringIterator;
                    try {
                        stringIterator = serverDrive.listDirectory(Path.of(path));
                    } catch (IOException e) {
                        this.error = e.getMessage();
                        return;
                    }

                    ListTag list = new ListTag();
                    while (stringIterator.hasNext()) {
                        String name = stringIterator.next();
                        list.add(StringTag.valueOf(name));
                    }
                    this.list = list;
                    this.setSuccessful();
                }
            }
        }).join();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (error != null) {
            tag.putString("error", error);
        } else {
            tag.put("list", list);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
        this.files = this.list.stream().map(Tag::getAsString).toList();
    }

    public List<String> getFiles() {
        return files;
    }
}
