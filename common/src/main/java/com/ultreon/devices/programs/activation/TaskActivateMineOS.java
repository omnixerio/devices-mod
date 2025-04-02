package com.ultreon.devices.programs.activation;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.core.Laptop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TaskActivateMineOS extends Task {
    private String name;
    private UUID license;

    public TaskActivateMineOS() {
        super("activate_mine_os");
    }

    public TaskActivateMineOS(UUID license) {
        this();
        this.license = license;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putUUID("License", this.license);
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        if (Laptop.getInstance().activate(nbt.getUUID("License"))) {
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
    }

    @Override
    public void processResponse(CompoundTag nbt) {
    }

}
