package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TaskPay extends Task {
    private String uuid;
    private int amount;

    public TaskPay() {
        super("bank_pay");
    }

    public TaskPay(String uuid, int amount) {
        this();
        this.uuid = uuid;
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putString("player", this.uuid);
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        String uuid = tag.getString("uuid").orElse(null);
        int amount = tag.getIntOr("amount", 0);
        Account sender = BankUtil.INSTANCE.getAccount(player);
        Account recipient = BankUtil.INSTANCE.getAccount(UUID.fromString(uuid));
        if (recipient != null && sender.hasAmount(amount)) {
            recipient.add(amount);
            sender.remove(amount);
            this.amount = sender.getBalance();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (isSucessful()) {
            tag.putInt("balance", this.amount);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
    }
}
