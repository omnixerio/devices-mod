package com.ultreon.devices.core.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public class TaskConnect extends Task {
    private BlockPos devicePos;
    private BlockPos routerPos;

    public TaskConnect() {
        super("connect");
    }

    public TaskConnect(BlockPos devicePos, BlockPos routerPos) {
        this();
        this.devicePos = devicePos;
        this.routerPos = routerPos;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("devicePos", devicePos.asLong());
        tag.putLong("routerPos", routerPos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("routerPos"))).getBlockEntity(BlockPos.of(tag.getLong("routerPos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof RouterBlockEntity tileEntityRouter) {
            Router router = tileEntityRouter.getRouter();

            BlockEntity tileEntity1 = level.getChunkAt(BlockPos.of(tag.getLong("devicePos"))).getBlockEntity(BlockPos.of(tag.getLong("devicePos")), LevelChunk.EntityCreationType.IMMEDIATE);
            if (tileEntity1 instanceof NetworkDeviceBlockEntity tileEntityNetworkDevice) {
                if (router.addDevice(tileEntityNetworkDevice)) {
                    tileEntityNetworkDevice.connect(router);
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
