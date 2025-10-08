package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskSetupFileBrowser extends Task {
    private BlockPos pos;
    private boolean includeMain;

    private AbstractDrive mainDrive;
    private Map<UUID, AbstractDrive> availableDrives;

    public TaskSetupFileBrowser() {
        super();
    }

    public TaskSetupFileBrowser(BlockPos pos, boolean includeMain) {
        this();
        this.pos = pos;
        this.includeMain = includeMain;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.putBoolean("include_main", includeMain);
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            if (tag.getBoolean("include_main")) {
                mainDrive = fileSystem.getMainDrive();
            }
            availableDrives = fileSystem.getAvailableDrives(level, false);
            this.setSuccessful();
        } else {
            Devices.LOGGER.warn("BlockEntity at pos " + BlockPos.of(tag.getLong("pos")) + " is not a ComputerBlockEntity");
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        if (this.isSuccessful()) {
            if (mainDrive != null) {
                CompoundTag mainDriveTag = new CompoundTag();
                mainDriveTag.putString("name", mainDrive.getName());
                mainDriveTag.putUUID("uuid", mainDrive.getUuid());
                mainDriveTag.putString("type", mainDrive.getType().toString());
                tag.put("main_drive", mainDriveTag);
            }

            ListTag driveList = new ListTag();
            availableDrives.forEach((k, v) -> {
                DebugLog.log("k = " + k);
                DebugLog.log("v.getUuid() = " + v.getUuid());
                CompoundTag driveTag = new CompoundTag();
                driveTag.putString("name", v.getName());
                driveTag.putUUID("uuid", v.getUuid());
                driveTag.putString("type", v.getType().toString());
                driveList.add(driveTag);
            });
            tag.put("available_drives", driveList);
        }
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {

    }
}
