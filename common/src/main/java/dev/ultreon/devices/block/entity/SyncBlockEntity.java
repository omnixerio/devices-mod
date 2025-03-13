package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
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
        Level lvl = this.level;
        if (lvl != null) {
            lvl.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        if (!pipeline.isEmpty()) {
            CompoundTag updateTag = pipeline;
            saveAdditional(updateTag, registries);
            pipeline = new CompoundTag();
            return updateTag;
        }
        CompoundTag updateTag = saveSyncTag();
        super.saveAdditional(updateTag, registries);
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
