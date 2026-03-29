package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.NotNull;

public class TaskUpdateApplicationData extends Task {
    private int x, y, z;
    private String appId;
    private CompoundTag data;

    public TaskUpdateApplicationData() {
        super("update_application_data");
    }

    public TaskUpdateApplicationData(int x, int y, int z, @NotNull String appId, @NotNull CompoundTag data) {
        this();
        this.x = x;
        this.y = y;
        this.z = z;
        this.appId = appId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putInt("posX", this.x);
        tag.putInt("posY", this.y);
        tag.putInt("posZ", this.z);
        tag.putString("appId", this.appId);
        tag.put("appData", this.data);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(new BlockPos(tag.getIntOr("posX", 0), tag.getIntOr("posY", 0), tag.getIntOr("posZ", 0)));
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            laptop.setApplicationData(tag.getString("appId").orElse(null), tag.getCompoundOrEmpty("appData"));
        }
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
