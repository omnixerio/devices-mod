package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockPos;
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
        super("get_file_system");
    }

    public TaskSetupFileBrowser(BlockPos pos, boolean includeMain) {
        this();
        this.pos = pos;
        this.includeMain = includeMain;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.putBoolean("include_main", includeMain);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            if (tag.getBoolean("include_main")) {
                mainDrive = fileSystem.getMainDrive();
            }
            availableDrives = fileSystem.getAvailableDrives(level, false);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            if (mainDrive != null) {
                CompoundTag mainDriveTag = new CompoundTag();
                mainDriveTag.putString("name", mainDrive.getName());
                mainDriveTag.putString("uuid", mainDrive.getUuid().toString());
                mainDriveTag.putString("type", mainDrive.getType().toString());
                tag.put("main_drive", mainDriveTag);
                tag.put("structure", mainDrive.getDriveStructure().toTag());
            }

            ListTag driveList = new ListTag();
            availableDrives.forEach((k, v) -> {
                DebugLog.log("k = " + k);
                DebugLog.log("v.getUuid() = " + v.getUuid());
                CompoundTag driveTag = new CompoundTag();
                driveTag.putString("name", v.getName());
                driveTag.putString("uuid", v.getUuid().toString());
                driveTag.putString("type", v.getType().toString());
                driveList.add(driveTag);
            });
            tag.put("available_drives", driveList);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
