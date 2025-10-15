package com.ultreon.devices.core.network.task;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.Device;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

public class TaskGetRouters extends Task {

    // The location of the computer.
    private BlockPos devicePos;
    private ListTag routers;

    public TaskGetRouters() {
        super("task_get_routers");
    }

    public TaskGetRouters(BlockPos devicePos) {
        this();
        this.devicePos = devicePos;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("devicePos", devicePos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockPos devicePos = BlockPos.of(tag.getLong("devicePos"));
        this.routers = new ListTag();

        int range = DeviceConfig.SIGNAL_RANGE.get();

        for (int y = -range; y < range + 1; y++) {
            for (int z = -range; z < range + 1; z++) {
                for (int x = -range; x < range + 1; x++) {
                    assert devicePos != null;
                    BlockPos pos = new BlockPos(devicePos.getX() + x, devicePos.getY() + y, devicePos.getZ() + z);
                    assert level != null;
                    BlockEntity tileEntity = level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
                    if (tileEntity instanceof RouterBlockEntity) {
                        routers.add(new Device((DeviceBlockEntity) tileEntity).toTag(true));
                    }
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            System.out.println(routers);
            tag.put("routers", routers);
        } else {
//            tag.putString("reason", this.reason); TODO do something
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
        // Does not need response processing
    }
}
