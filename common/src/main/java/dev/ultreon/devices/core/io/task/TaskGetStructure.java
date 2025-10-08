package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.ServerFolder;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/// @author MrCrayfish
@Deprecated
public class TaskGetStructure extends Task {
    private String uuid;
    private BlockPos pos;

    private ServerFolder folder;

    @Deprecated
    public TaskGetStructure() {
        super();
    }

    @Deprecated
    public TaskGetStructure(Drive drive, BlockPos pos) {
        this();
        this.uuid = drive.getUUID().toString();
        this.pos = pos;
    }

    @Override
    @Deprecated
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putString("uuid", uuid);
        tag.putLong("pos", pos.asLong());
    }

    @Override
    @Deprecated
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        BlockPos pos1 = BlockPos.of(tag.getLong("pos"));

        Devices.getServer().submit(() -> {
            BlockEntity tileEntity = level.getBlockEntity(pos1);
            if (tileEntity instanceof ComputerBlockEntity laptop) {
                FileSystem fileSystem = laptop.getFileSystem();
                UUID uuid = UUID.fromString(tag.getString("uuid"));
                AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
                if (serverDrive != null) {
                    folder = serverDrive.getDriveStructure();
                    this.setSuccessful();
                }
            }
        }).join();
    }

    @Override
    @Deprecated
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        if (folder != null) {
            tag.putString("file_name", folder.getName());
            tag.put("structure", folder.toTag());
        }
    }

    @Override
    @Deprecated
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {

    }
}
