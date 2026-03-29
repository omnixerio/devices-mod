package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskAdd extends Task {
    private int amount;

    public TaskAdd() {
        super("bank_add");
    }

    public TaskAdd(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        int amount = tag.getIntOr("amount", 0);
        Account sender = BankUtil.INSTANCE.getAccount(player);
        sender.add(amount);
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        tag.putInt("balance", this.amount);
    }

    @Override
    public void processResponse(CompoundTag tag) {
    }
}
