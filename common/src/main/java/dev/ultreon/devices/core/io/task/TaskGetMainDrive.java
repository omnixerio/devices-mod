package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/// @author MrCrayfish
public class TaskGetMainDrive extends Task {
    private BlockPos pos;

    private AbstractDrive mainDrive;

    public TaskGetMainDrive() {
        super();
    }

    public TaskGetMainDrive(BlockPos pos) {
        this();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(tag.getLong("pos")));
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            mainDrive = fileSystem.getMainDrive();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        if (this.isSuccessful()) {
            CompoundTag mainDriveTag = new CompoundTag();
            mainDriveTag.putString("name", mainDrive.getName());
            mainDriveTag.put("uuid", NbtUtils.createUUID(mainDrive.getUuid()));
            mainDriveTag.putString("type", mainDrive.getType().toString());
            tag.put("main_drive", mainDriveTag);
        }
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {
        if (this.isSuccessful()) {
            if (Minecraft.getInstance().screen instanceof ComputerScreen) {
                Drive drive = new Drive(tag.getCompound("main_drive"));

                if (ComputerScreen.getMainDrive() == null) {
                    ComputerScreen.setMainDrive(drive);
                }
            }
        }
    }
}
