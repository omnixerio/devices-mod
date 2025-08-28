package com.ultreon.devices.core.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public class TaskPing extends Task {
    private BlockPos sourceDevicePos;
    private int strength;

    public TaskPing() {
        super("ping");
    }

    public TaskPing(BlockPos sourceDevicePos) {
        this();
        this.sourceDevicePos = sourceDevicePos;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("sourceDevicePos", sourceDevicePos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity blockEntity = level.getChunkAt(BlockPos.of(tag.getLong("sourceDevicePos"))).getBlockEntity(BlockPos.of(tag.getLong("sourceDevicePos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (blockEntity instanceof NetworkDeviceBlockEntity networkDevice) {
            if (networkDevice.isConnected()) {
                this.strength = networkDevice.getSignalStrength();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            tag.putInt("strength", strength);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
