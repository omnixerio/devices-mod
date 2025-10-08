package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.system.object.Account;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TaskPay extends Task {
    private String uuid;
    private int amount;

    public TaskPay() {
        super();
    }

    public TaskPay(String uuid, int amount) {
        this();
        this.uuid = uuid;
        this.amount = amount;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putString("player", this.uuid);
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        String uuid = tag.getString("uuid");
        int amount = tag.getInt("amount");
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
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        if (isSuccessful()) {
            tag.putInt("balance", this.amount);
        }
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {
    }
}
