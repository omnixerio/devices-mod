package com.ultreon.devices.block.entity;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.annotations.PlatformOverride;
import com.ultreon.devices.util.BlockEntityUtil;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @PlatformOnly("forge")
    @PlatformOverride("forge")
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level != null) {
            this.loadAdditional(Objects.requireNonNull(pkt.getTag()), level.registryAccess());
        } else {
            OmnixerioDevicesMod.LOGGER.error("Level is null during data packet load for block entity " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        if (!pipeline.isEmpty()) {
            CompoundTag updateTag = pipeline;
            saveAdditional(updateTag, provider);
            pipeline = new CompoundTag();
            return updateTag;
        }
        CompoundTag updateTag = saveSyncTag();
        super.saveAdditional(updateTag, provider);
        return updateTag;
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
