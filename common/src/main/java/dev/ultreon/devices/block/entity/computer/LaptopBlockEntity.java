package dev.ultreon.devices.block.entity.computer;

import dev.ultreon.devices.block.computer.LaptopBlock;
import dev.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LaptopBlockEntity extends ComputerBlockEntity {
    private static final int OPENED_ANGLE = 102;

    private int attachmentCooldown;
    private int rotation;
    private int prevRotation;

    private boolean open = false;

    public LaptopBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.LAPTOP.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level;
        if (level == null) return;

        if (getBlockState().getValue(LaptopBlock.OPEN) != open) {
            level.setBlock(getBlockPos(), this.getBlockState().setValue(LaptopBlock.OPEN, open), 2);
        }

        if (level.isClientSide) {
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
        if (attachmentCooldown > 0) {
            attachmentCooldown--;
        }
    }

    public float getScreenAngle(float partialTicks) {
        return -OPENED_ANGLE * ((prevRotation + (rotation - prevRotation) * partialTicks) / OPENED_ANGLE);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(compound, registries);

        if (compound.contains("open")) {
            this.open = compound.getBoolean("open");
            Level level = getLevel();
            if (level != null) {
                level.setBlock(getBlockPos(), this.getBlockState().setValue(LaptopBlock.OPEN, open), 2);
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putBoolean("open", open);
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.putBoolean("open", open);
        return tag;
    }

    @Override
    public boolean isPoweredOn() {
        return isOpen();
    }

    @Override
    public void powerOff() {
        super.powerOff();

        if (isOpen()) {
            openClose(null);
        }
    }

    public void setAttachmentCooldown(int ticks) {
        this.attachmentCooldown = Math.max(ticks, 0);
    }

    public boolean canChangeAttachment() {
        return this.attachmentCooldown <= 0;
    }


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

    public boolean isOpen() {
        return open;
    }
}
