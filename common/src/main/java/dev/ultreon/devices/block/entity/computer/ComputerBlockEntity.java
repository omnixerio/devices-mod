package dev.ultreon.devices.block.entity.computer;

import dev.ultreon.devices.block.computer.LaptopBlock;
import dev.ultreon.devices.block.entity.DriveInfo;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.core.Bios;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public abstract class ComputerBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private CompoundTag applicationData = new CompoundTag();
    private CompoundTag systemData = new CompoundTag();
    private FileSystem fileSystem;

    private DyeColor externalDriveColor;

    protected ComputerBlockEntity(BlockEntityType<? extends ComputerBlockEntity> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
    }

    @Override
    public String getDeviceName() {
        return "Laptop";
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("system_data", Tag.TAG_COMPOUND)) {
            this.systemData = compound.getCompound("system_data");
        }
        if (compound.contains("application_data", Tag.TAG_COMPOUND)) {
            this.applicationData = compound.getCompound("application_data");
        }
        if (compound.contains("file_system")) {
            this.fileSystem = new FileSystem(this, compound.getCompound("file_system"));
        }
        if (compound.contains("external_drive_color", Tag.TAG_BYTE)) {
            this.externalDriveColor = null;
            if (compound.getByte("external_drive_color") != -1) {
                this.externalDriveColor = DyeColor.byId(compound.getByte("external_drive_color"));
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(compound, registries);

        if (systemData != null) {
            compound.put("system_data", systemData);
        }

        if (applicationData != null) {
            compound.put("application_data", applicationData);
        }

        if (fileSystem != null) {
            compound.put("file_system", fileSystem.toTag());
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.put("system_data", getSystemData());

        if (getFileSystem().getAttachedDrive() != null) {
            tag.putByte("external_drive_color", (byte) getFileSystem().getAttachedDriveColor().getId());
        } else {
            tag.putByte("external_drive_color", (byte) -1);
        }

        return tag;
    }

    // Todo: Port to 1.18.2 if possible
//    @Override
//    public double getMaxRenderDistanceSquared() {
//        return 16384;
//    }
//
//    public AxisAlignedBB getRenderBoundingBox() {
//        return INFINITE_EXTENT_AABB;
//    }

    protected static void doNeighborUpdates(Level level, BlockPos pos, BlockState state) {
        state.updateNeighbourShapes(level, pos, 3);
    }

    public CompoundTag getApplicationData() {
        return applicationData != null ? applicationData : new CompoundTag();
    }

    public CompoundTag getSystemData() {
        if (systemData == null) {
            systemData = new CompoundTag();
        }
        return systemData;
    }

    public void setSystemData(CompoundTag systemData) {
        this.systemData = systemData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    public FileSystem getFileSystem() {
        if (fileSystem == null) {
            fileSystem = new FileSystem(this);
        }
        return fileSystem;
    }

    public void setApplicationData(String appId, CompoundTag applicationData) {
        this.applicationData = applicationData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    public boolean isExternalDriveAttached() {
        return externalDriveColor != null;
    }

    public DyeColor getExternalDriveColor() {
        return externalDriveColor;
    }

    public Map<UUID, DriveInfo> getDriveInfo() {
        return getFileSystem().getDrives();
    }

    public Bios getBios() {
        return new BiosImpl(this);
    }

    public boolean isPoweredOn() {
        return true;
    }

    public void powerOff() {

    }
}
