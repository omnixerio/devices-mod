package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.Folder;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author MrCrayfish
 */
public class TaskGetMainDrive extends Task {
    private BlockPos pos;

    private AbstractDrive mainDrive;

    public TaskGetMainDrive() {
        super("get_main_drive");
    }

    public TaskGetMainDrive(BlockPos pos) {
        this();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(tag.getLong("pos")));
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            mainDrive = fileSystem.getMainDrive();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            CompoundTag mainDriveTag = new CompoundTag();
            mainDriveTag.putString("name", mainDrive.getName());
            mainDriveTag.putString("uuid", mainDrive.getUuid().toString());
            mainDriveTag.putString("type", mainDrive.getType().toString());
            tag.put("main_drive", mainDriveTag);
            tag.put("structure", mainDrive.getDriveStructure().toTag());
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            if (Minecraft.getInstance().screen instanceof Laptop) {
                CompoundTag structureTag = tag.getCompound("structure");
                Drive drive = new Drive(tag.getCompound("main_drive"));
                drive.syncRoot(Folder.fromTag(FileSystem.LAPTOP_DRIVE_NAME, structureTag));
                drive.getRoot().validate();

                if (Laptop.getMainDrive() == null) {
                    Laptop.setMainDrive(drive);
                }
            }
        }
    }
}
