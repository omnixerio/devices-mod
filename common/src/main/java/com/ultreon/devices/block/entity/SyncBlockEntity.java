package com.ultreon.devices.block.entity;

import com.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.Nullable;

public abstract class SyncBlockEntity extends BlockEntity {
    protected CompoundTag pipeline = new CompoundTag();

    public SyncBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    public void sync() {
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    // from SignBlockEntity
    protected void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level != null) {
            TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), pkt.getTag());
        }
    }

    public CompoundTag getUpdateTag() {
        if (!pipeline.isEmpty()) {
            CompoundTag updateTag = pipeline.copy();
            pipeline = new CompoundTag();
            return updateTag;
        }

        TagValueOutput withContext = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        return withContext.buildResult();
    }

    public abstract CompoundTag saveSyncTag();

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public CompoundTag getPipeline() {
        return pipeline;
    }
}
