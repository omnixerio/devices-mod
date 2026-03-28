package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ComputerBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private static final int OPENED_ANGLE = 102;

    private boolean open = false;

    private CompoundTag applicationData = new CompoundTag();
    private CompoundTag systemData = new CompoundTag();
    private FileSystem fileSystem;

    private int rotation;

    private int prevRotation;

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
        Level level = this.level;
        if (level == null) return;

        if (getBlockState().getValue(LaptopBlock.OPEN) != open) {
            level.setBlock(getBlockPos(), this.getBlockState().setValue(LaptopBlock.OPEN, open), 2);
        }

        if (level.isClientSide()) {
            prevRotation = rotation;
            if (!open) {
                if (rotation > 0) {
                    rotation -= 10;
                }
            } else {
                if (rotation < OPENED_ANGLE) {
                    rotation += 10;
                }
            }
        }
    }

    @Override
    public void loadAdditional(@NotNull ValueInput compound) {
        super.loadAdditional(compound);
        this.open = compound.getBooleanOr("open", false);
        Level level = getLevel();
        if (level != null) {
            level.setBlock(getBlockPos(), this.getBlockState().setValue(LaptopBlock.OPEN, open), 2);
        }
        compound.read("system_data", ExtraCodecs.NBT).ifPresent(tag -> systemData = tag.asCompound().orElseThrow());
        compound.read("application_data", ExtraCodecs.NBT).ifPresent(tag -> applicationData = tag.asCompound().orElseThrow());
        compound.read("file_system", ExtraCodecs.NBT).ifPresent(tag -> fileSystem = new FileSystem(this, tag.asCompound().orElseThrow()));
        this.externalDriveColor = null;
        if (compound.getByteOr("external_drive_color", (byte) -1) != -1) {
            this.externalDriveColor = DyeColor.byId(compound.getByteOr("external_drive_color", (byte) 0));
        }
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput compound) {
        super.saveAdditional(compound);
        compound.putBoolean("open", open);

        if (systemData != null) {
            compound.store("system_data", ExtraCodecs.NBT, systemData);
        }

        if (applicationData != null) {
            compound.store("application_data", ExtraCodecs.NBT, applicationData);
        }

        if (fileSystem != null) {
            compound.store("file_system", ExtraCodecs.NBT, fileSystem.toTag());
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.putBoolean("open", open);
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

    public void openClose(@Nullable Entity entity) {
        Level level = this.level;
        if (level != null) {
            level.gameEvent(!open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
        }
        boolean oldOpen = open;
        open = !getBlockState().getValue(LaptopBlock.OPEN);
        if (oldOpen != open) {
            pipeline.putBoolean("open", open);
            var d = getBlockState().setValue(LaptopBlock.OPEN, open);
            this.level.setBlock(this.getBlockPos(), d, 18);
            sync();
        }

        if (level != null) {
            markUpdated();
            doNeighborUpdates(level, this.getBlockPos(), this.getBlockState());
        }
    }

    private static void doNeighborUpdates(Level level, BlockPos pos, BlockState state) {
        state.updateNeighbourShapes(level, pos, 3);
    }

    public boolean isOpen() {
        return open;
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
            fileSystem = new FileSystem(this, new CompoundTag());
        }
        return fileSystem;
    }

    public void setApplicationData(String appId, CompoundTag applicationData) {
        this.applicationData = applicationData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    public float getScreenAngle(float partialTicks) {
        return -OPENED_ANGLE * ((prevRotation + (rotation - prevRotation) * partialTicks) / OPENED_ANGLE);
    }

    public boolean isExternalDriveAttached() {
        return externalDriveColor != null;
    }

    public DyeColor getExternalDriveColor() {
        return externalDriveColor;
    }
}
