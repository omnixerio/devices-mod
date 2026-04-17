package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LaptopBlockEntity extends ComputerBlockEntity {
    private int attachmentCooldown;

    public LaptopBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.LAPTOP, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        super.tick();

        if (attachmentCooldown > 0) {
            attachmentCooldown--;
        }
    }

    public void setAttachmentCooldown(int ticks) {
        this.attachmentCooldown = Math.max(ticks, 0);
    }

    public boolean canChangeAttachment() {
        return this.attachmentCooldown <= 0;
    }
}
