package com.ultreon.devices.block.entity;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public class PaperBlockEntity extends SyncBlockEntity {
    private IPrint print;
    private byte rotation;

    public PaperBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.PAPER.get(), pWorldPosition, pBlockState);
    }

    public void nextRotation() {
        rotation++;
        if (rotation > 7) {
            rotation = 0;
        }
        pipeline.putByte("rotation", rotation);
        sync();
        playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
    }

    public float getRotation() {
        return rotation * 45f;
    }

    @Nullable
    public IPrint getPrint() {
        return print;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("print", Tag.TAG_COMPOUND)) {
            print = IPrint.load(tag.getCompound("print"));
        }
        if (tag.contains("rotation", Tag.TAG_BYTE)) {
            rotation = tag.getByte("rotation");
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (print != null) {
            tag.put("print", IPrint.save(print));
        }
        tag.putByte("rotation", rotation);
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (print != null) {
            tag.put("print", IPrint.save(print));
        }
        tag.putByte("rotation", rotation);
        return tag;
    }

    private void playSound(SoundEvent sound) {
        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1f, 1f);
    }
}
